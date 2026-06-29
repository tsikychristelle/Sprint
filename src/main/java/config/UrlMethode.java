package config;
public class UrlMethode{
    private String url;
    private ClassMethode classMethode;
    public UrlMethode(String url, ClassMethode classMethode) {
        this.url = url;
        this.classMethode = classMethode;   
    }
    public String getUrl() {
        return url;
    }
    public ClassMethode getClassMethode() {
        return classMethode;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setClassMethode(ClassMethode classMethode) {
        this.classMethode = classMethode;
    }
}