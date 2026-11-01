package config;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
public class MapFile{
    private String filename;
    // Pour voir de quel type de fichier il s'agit
    private String mimeType;
    // Pour voir ou prendre les fichiers 
    private String targetDirectory = "";
    private final String uploadDirectory = Config.getProperty("upload.dir");

    private byte[] data;

     private void generateName() {
        if (filename == null || filename.isEmpty()) {
            filename = "filename_placeholder";
        }

        String extension = "";
        int lastIndex = filename.lastIndexOf(".");

        if (lastIndex != -1) {
            extension = filename.substring(lastIndex); 
            filename = filename.substring(0, lastIndex);
        }

        String ender = UUID.randomUUID().toString();
        this.filename = filename + "_" + ender + extension;
    }

    public void persistFile() {
        if( data == null){
            throw new RuntimeException("No data in the file");
        }
        
        generateName();

        try {
            Path destination = Path.of(uploadDirectory, targetDirectory, filename);

            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }

            System.out.println(filename);

            Files.write(destination, data);

            System.out.println("Does this work bro");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}