package annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// Seulement les classes ,les enums et les interfaces peuvent être annotées avec @Controller
// Si methode on fait METHOD
@Target(ElementType.METHOD)
// L'annotation @Controller est disponible à l'exécution, ce qui signifie qu'elle peut être lue via la réflexion pendant l'exécution du programme.
@Retention(RetentionPolicy.RUNTIME)

public @interface Url {
    String value() ;
    
}

