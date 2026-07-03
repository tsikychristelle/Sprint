package annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.METHOD)
// L'annotation est disponible à l'exécution, ce qui signifie qu'elle peut être lue via la réflexion pendant l'exécution du programme.
@Retention(RetentionPolicy.RUNTIME)
public @interface Url2 {
    String url();
    String value() default "GET";
}
