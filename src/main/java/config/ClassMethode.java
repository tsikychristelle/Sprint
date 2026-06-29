package config;
import java.lang.reflect.Method;
public class ClassMethode {
    private Class<?> classe;
    private Method methode;
    public ClassMethode(Class<?> classe, Method methode) {
        this.classe = classe;
        this.methode = methode; 
    }
    public Class<?> getClasse() {
        return classe;
    }
    public Method getMethode() {
        return methode;
    }
    public void setClasse(Class<?> classe) {
        this.classe = classe;
    }
    public void setMethode(Method methode) {
        this.methode = methode;
    }

}
