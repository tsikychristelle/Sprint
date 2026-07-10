package util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import annotation.Url2;
import config.ClassMethode;
import config.MethodeUrl;

public class Util {
    public static List<String> findClasses(List<String> packageNames, Class<? extends Annotation> classAnnotation) throws Exception {
        List<String> classes = new ArrayList<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String packageName: packageNames) {
            String path = packageName.replace('.', '/');
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                continue;
            }
    
            File directory = new File(resource.toURI());
    
            for (File file: directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
    
                    if (clazz.isAnnotationPresent(classAnnotation)) {
                        classes.add(className);
                    }
                }
            }
        }

        return classes;
    }

    public static void findUrlMethodMappings(List<String> packageNames, Map<MethodeUrl, ClassMethode> urlMethodMappings, Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String packageName: packageNames) {
            String path = packageName.replace('.', '/');
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                continue;
            }
    
            File directory = new File(resource.toURI());
    
            for (File file: directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
    
                    if (clazz.isAnnotationPresent(classAnnotation)) {
                        Method[] methods = clazz.getDeclaredMethods();

                        for (Method m: methods) {
                            if (m.isAnnotationPresent(methodAnnotation)) {
                                Annotation methAnnotation = m.getAnnotation(methodAnnotation);

                                if (methAnnotation.annotationType() == Url2.class) {
                                    Url2 urlMapping = (Url2) methAnnotation;
                                    String url = urlMapping.url();
                                    String httpMethod = urlMapping.value();

                                    MethodeUrl methodeUrl = new MethodeUrl(url, httpMethod);
                                    
                                    ClassMethode classMethode = new ClassMethode(clazz, m);

                                    if (!urlMethodMappings.containsKey(methodeUrl)) {
                                        urlMethodMappings.put(methodeUrl, classMethode);
                                    } else {
                                        throw new Exception("Duplicate URL mapping found for " + httpMethod + " " + url + " in class " + clazz.getName() + " method " + m.getName());
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public static List<String> splitString(String str, String separator) {
        String[] splitted = str.split(separator);
        return new ArrayList<>(List.of(splitted));
    }
}
