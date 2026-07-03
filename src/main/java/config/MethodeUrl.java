package config;

import java.util.Objects;
public class MethodeUrl{
    private String url;
    private String methode;

    public MethodeUrl(){

    }
    public MethodeUrl(String url,String methode){
        this.url = url;
        this.methode = methode;
    }
    public String getUrl(){
        return url;
    }
    public String getMethode(){
        return methode;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setMethode(String methode){
        this.methode = methode;
    }
    @Override   
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodeUrl that = (MethodeUrl) o;
        // Deux objets sont égaux si l'url et la méthode sont identiques
        return Objects.equals(url, that.url) && Objects.equals(methode, that.methode);
    }

    @Override
    public int hashCode() {
        // Génère un code de hachage basé sur l'url et la méthode
        return Objects.hash(url, methode);
    }
}