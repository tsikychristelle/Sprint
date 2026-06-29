package controller;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import config.UrlMethode;
import config.Utilitaire;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class FrontControllerServlet extends HttpServlet{
    private List<Class<?>> controllers;
    private List<Method> methodes;
    List<UrlMethode> listUrlMethode;
    Utilitaire utilitaire = new Utilitaire();
    public void init() throws ServletException {
       
        // controllers = utilitaire.getClassesWithAnnotation("com.monApp");
        // methodes = utilitaire.getMethodsWithAnnotation("com.monApp");
       listUrlMethode = utilitaire.getUrlMethodeByClass("com.monApp");

    }
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        processRequest(req, res);
        
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        processRequest(req, res);
    
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse res)
throws ServletException, IOException {
    res.setContentType("text/html;charset=UTF-8");

    // 1. Récupérer l'URI complète (ex: /MonApplication/test)
    String requestURI = req.getRequestURI(); 
    
    // 2. Récupérer le chemin de base de l'application (ex: /MonApplication)
    String contextPath = req.getContextPath();
    
    // 3. Extraire uniquement la route finale (ex: /test)
    String path = requestURI.substring(contextPath.length());

        // [Optionnel] Si vous utilisez un mapping du style /front/*, retirez le dossier du servlet :
        // String servletPath = req.getServletPath();
        // String path = requestURI.substring(contextPath.length() + servletPath.length());

        // Petit log de secours pour voir ce que Christelle-Framework reçoit réellement :
        System.out.println("[Debug Framework] URI demandée : " + path);

        // 4. Lancer la recherche avec le chemin nettoyé
        UrlMethode urlMethode1 = utilitaire.getMethodeUrl(listUrlMethode, path);
        
        if (urlMethode1 != null) {
            res.getWriter().println("<h3>URL trouvée :</h3>");
            res.getWriter().println("URL: " + urlMethode1.getUrl() + 
                                    ", Classe: " + urlMethode1.getClassMethode().getClasse().getName() + 
                                    ", Méthode: " + urlMethode1.getClassMethode().getMethode().getName());
        } else {
            res.getWriter().println("<h3>URL non trouvée (" + path + "). Voici les URLs disponibles :</h3>");
            for (UrlMethode urlMethode : listUrlMethode) {
                res.getWriter().println("<p>URL: " + urlMethode.getUrl() + 
                                        ", Classe: " + urlMethode.getClassMethode().getClasse().getName() + 
                                        ", Méthode: " + urlMethode.getClassMethode().getMethode().getName() + "</p>");
            }
        }
    }

}