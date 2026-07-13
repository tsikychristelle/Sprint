package controller;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import config.MethodeUrl;
import config.ModelAndView;
import config.Url2Method;
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
    List<Url2Method> listUrl2Method;
    String prefixeView ;
    String suffixeView ;
    Utilitaire utilitaire = new Utilitaire();
    public void init() throws ServletException {
        // listUrlMethode = utilitaire.getUrlMethodeByClass("com.monApp");
        // listUrl2Method = utilitaire.getUrl2MethodeByClass("com.monApp");

        // // Vérification des doublons pour l'annotation @Url2
        // HashSet<String> uniqueKeys = new HashSet<>();
        // for (Url2Method route : listUrl2Method) {
        //     // On crée une clé unique combinant la méthode HTTP et l'URL (ex: "GET /test1")
        //     String uniqueKey = route.getMethodeUrl().getMethode() + " " + route.getMethodeUrl().getUrl();
            
        //     // .add() retourne false si l'élément existe déjà dans le HashSet
        //     if (!uniqueKeys.add(uniqueKey)) {
        //         throw new ServletException("Erreur de configuration : La route [" + uniqueKey + "] est déclarée plusieurs fois !");
        //     }
        // }
        prefixeView = getServletConfig().getInitParameter("prefixeView");
        suffixeView = getServletConfig().getInitParameter("suffixeView");
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
        UrlMethode urlMethode1 = utilitaire.getMethodeUrl(listUrlMethode, path);
    
        HashSet<MethodeUrl> uniqueRoutes = new HashSet<>();
            Url2Method url2Methode = utilitaire.getMethod2Url(listUrl2Method, path);
            String httpMethod = req.getMethod();

        if (url2Methode != null && url2Methode.getMethodeUrl().getMethode().equals(httpMethod)) {
            try {
                Object controllerInstance = url2Methode.getClassMethode().getClasse().getDeclaredConstructor().newInstance();
                Method methodToInvoke = url2Methode.getClassMethode().getMethode();
                ModelAndView modelAndView = (ModelAndView) methodToInvoke.invoke(controllerInstance);
                String nomPage = modelAndView.getView();
                Map<String, Object> model = modelAndView.getModel();
                // Object result = methodToInvoke.invoke(controllerInstance);
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }

                req.getRequestDispatcher(prefixeView + nomPage + suffixeView).forward(req, res);
                // res.getWriter().write((String) result);
            } catch (Exception e) {
                e.printStackTrace();
                res.getWriter().write("Erreur d'exécution : " + e.getMessage());
            }
        } else {
            // La route n'existe pas : on renvoie un code 404 et on liste les options
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("<h1>404 - Page non trouvée</h1>");
            res.getWriter().write("<p>Le chemin <b>" + path + "</b> (" + httpMethod + ") n'existe pas.</p>");
            res.getWriter().write("<h3>Routes disponibles :</h3><ul>");
            
            for (Url2Method route : listUrl2Method) {
                res.getWriter().write("<li><b>" + route.getMethodeUrl().getMethode() + "</b> : " 
                    + route.getMethodeUrl().getUrl() + " -> " 
                    + route.getClassMethode().getClasse().getSimpleName() + "." 
                    + route.getClassMethode().getMethode().getName() + "()</li>");
            }
            res.getWriter().write("</ul>");
        }
            
    }

}