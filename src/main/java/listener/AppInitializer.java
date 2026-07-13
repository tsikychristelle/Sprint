package listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import config.ClassMethode;
import config.MethodeUrl;
import config.Url2Method;
import config.UrlMethode;
import config.Utilitaire;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
public class AppInitializer implements ServletContextListener {
    private List<String> listControllers = new ArrayList<>();
    private Map<MethodeUrl, ClassMethode> urlMethodMappings = new HashMap<>();
    List<UrlMethode> listUrlMethode;
    List<Url2Method> listUrl2Method;
    Utilitaire utilitaire = new Utilitaire();
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        listUrlMethode = utilitaire.getUrlMethodeByClass("com.monApp");
        listUrl2Method = utilitaire.getUrl2MethodeByClass("com.monApp");

        // Vérification des doublons pour l'annotation @Url2
        HashSet<String> uniqueKeys = new HashSet<>();
        for (Url2Method route : listUrl2Method) {
            // On crée une clé unique combinant la méthode HTTP et l'URL (ex: "GET /test1")
            String uniqueKey = route.getMethodeUrl().getMethode() + " " + route.getMethodeUrl().getUrl();
            
            // .add() retourne false si l'élément existe déjà dans le HashSet
            if (!uniqueKeys.add(uniqueKey)) {
                throw new ServletException("Erreur de configuration : La route [" + uniqueKey + "] est déclarée plusieurs fois !");
            }
        }

        servletContext.setAttribute("listControllers", listControllers);
        servletContext.setAttribute("urlMethodMappings", urlMethodMappings);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
    }
    
}
