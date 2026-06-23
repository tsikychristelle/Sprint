package config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import annotation.Controller;
public class Utilitaire {
    
    public List<Class<?>> getClassesWithAnnotation(String pkg) {
        // 1. Configuration pour scanner le package et chercher les annotations
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackage(pkg)
                .filterInputsBy(new FilterBuilder().includePackage(pkg))
                .setScanners(Scanners.TypesAnnotated) // On active le scanner d'annotations
        );

        // 2. On récupère toutes les classes annotées par @Controller
        // (Reflections gère automatiquement le format Set pour éviter les doublons)
        Set<Class<?>> annotatedClasses = reflections.get(
            Scanners.TypesAnnotated.with(Controller.class).asClass()
        );

        // 3. Conversion du Set en List pour le retour de la méthode
        return new ArrayList<>(annotatedClasses);
    }
        
}
