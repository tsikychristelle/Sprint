package config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
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
    public List<Class<?>> getAllClass(String pkg) throws Exception {
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pkg))
                .filterInputsBy(new FilterBuilder().includePackage(pkg))
                .setScanners(Scanners.SubTypes.filterResultsBy(s -> true))
        );

        // 1. Récupération des classes
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        
        // 2. Conversion directe et retour (plus rapide et plus propre)
        return new ArrayList<>(classes);
    }

    public List<Method> getMethodsWithAnnotation(String pkg) {
        List<Class<?>> controllerClasses = new ArrayList<>();
        try {
            controllerClasses = getAllClass(pkg);
        }  catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des méthodes annotées : " + e.getMessage(), e);
        }
       
        List<Method> annotatedMethods = new ArrayList<>();
        for (Class<?> controllerClass : controllerClasses) {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation.Url.class)) {
                    annotatedMethods.add(method);
                }
            }
        }
        
        return annotatedMethods;
    }
    public List<ClassMethode> getClassMethodWithAnnotation(String pkg) {
        List<Class<?>> controllerClasses = new ArrayList<>();
        try {
            controllerClasses = getAllClass(pkg);
        }  catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des méthodes annotées : " + e.getMessage(), e);
        }
       
        List<ClassMethode> annotatedClassMethods = new ArrayList<>();
        for (Class<?> controllerClass : controllerClasses) {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation.Url.class)) {
                    annotatedClassMethods.add(new ClassMethode(controllerClass, method));
                }
            }
        }
        
        return annotatedClassMethods;
    }
    public List<UrlMethode> getUrlMethodeByClass(String pkg){
        List<UrlMethode> urlMethodes = new ArrayList<>();
        try {
            List<Class<?>> allClasses = getAllClass(pkg);
        
        for (Class<?> controllerClass : allClasses) {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation.Url.class)) {
                    urlMethodes.add(new UrlMethode(method.getAnnotation(annotation.Url.class).value(), new ClassMethode(controllerClass, method)));
                }
            }
        }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return urlMethodes;
    }
    public void printAllUrlMethode(String pkg){
        List<UrlMethode> urlMethodes = getUrlMethodeByClass(pkg);
        for (UrlMethode urlMethode : urlMethodes) {
            System.out.println("URL: " + urlMethode.getUrl() + ", Classe: " + urlMethode.getClassMethode().getClasse().getName() + ", Méthode: " + urlMethode.getClassMethode().getMethode().getName());
        }
    }
    // public void gererRequete (String url,List<UrlMethode> urlMethodes,String pkg){
    //     for(UrlMethode urlMethode : urlMethodes){
    //         if(urlMethode.getUrl().equals(url)){
    //             System.out.println("URL trouvée: " + urlMethode.getUrl() + ", Classe: " + urlMethode.getClassMethode().getClasse().getName() + ", Méthode: " + urlMethode.getClassMethode().getMethode().getName());
    //         }
    //         else{
    //             printAllUrlMethode(pkg);
    //         }
    //     }
    // }
    public UrlMethode getMethodeUrl(List<UrlMethode> urlMethodes, String url) {
        if (urlMethodes == null || url == null) {
            
            return null;
        }

        // Nettoyage de l'URL demandée (ex: "/mon-url/" devient "mon-url")
        String cleanUrl = url.trim().replaceAll("^/+|/+$", "");

        for (UrlMethode urlMethode : urlMethodes) {
            if (urlMethode.getUrl() != null) {
                // Nettoyage de l'URL stockée
                String cleanStoredUrl = urlMethode.getUrl().trim().replaceAll("^/+|/+$", "");

                if (cleanStoredUrl.equals(cleanUrl)) {
                    return urlMethode;
                }
            }
        }
        return null;
    }
        
}
