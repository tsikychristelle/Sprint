package config;

public class Url2Method {
    private MethodeUrl methodeUrl;
    private ClassMethode classMethode;
    public Url2Method(MethodeUrl methodeUrl, ClassMethode classMethode) {
        this.methodeUrl = methodeUrl;
        this.classMethode = classMethode;
    }
    public MethodeUrl getMethodeUrl() {
        return methodeUrl;
    }
    public ClassMethode getClassMethode() {
        return classMethode;
    }
    public void setMethodeUrl(MethodeUrl methodeUrl) {
        this.methodeUrl = methodeUrl;
    }
    public void setClassMethode(ClassMethode classMethode) {
        this.classMethode = classMethode;
    }

}
