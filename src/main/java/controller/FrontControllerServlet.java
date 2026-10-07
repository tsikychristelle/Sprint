package controller;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import config.ModelAndView;
import config.Url2Method;
import config.UrlMethode;
import config.Utilitaire;
import jakarta.servlet.ServletContext;
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
         ServletContext servletContext = getServletContext();
        methodes = (List<Method>) servletContext.getAttribute("listMethodeJSON");
        listUrlMethode = (List<UrlMethode>) servletContext.getAttribute("listUrlMethode");
        listUrl2Method = (List<Url2Method>) servletContext.getAttribute("listUrl2Method");
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

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String httpMethod = req.getMethod();

        Url2Method url2Methode = utilitaire.getMethod2Url(listUrl2Method, path);

        if (url2Methode == null
                || !url2Methode.getMethodeUrl().getMethode().equals(httpMethod)) {

            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.setContentType("text/html;charset=UTF-8");

            res.getWriter().write("<h1>404 - Page non trouvée</h1>");
            res.getWriter().write(
                    "<p>Le chemin <b>" + path + "</b> ("
                            + httpMethod + ") n'existe pas.</p>"
            );

            res.getWriter().write("<h3>Routes disponibles :</h3><ul>");

            if (listUrl2Method != null) {
                for (Url2Method route : listUrl2Method) {
                    res.getWriter().write(
                            "<li><b>"
                            + route.getMethodeUrl().getMethode()
                            + "</b> : "
                            + route.getMethodeUrl().getUrl()
                            + " -> "
                            + route.getClassMethode().getClasse().getSimpleName()
                            + "."
                            + route.getClassMethode().getMethode().getName()
                            + "()</li>"
                    );
                }
            }

            res.getWriter().write("</ul>");
            return;
        }

        try {
            Class<?> controllerClass =
                    url2Methode.getClassMethode().getClasse();

            Object controllerInstance =
                    controllerClass.getDeclaredConstructor().newInstance();

            Method methodToInvoke =
                    url2Methode.getClassMethode().getMethode();

            Parameter[] parameters = methodToInvoke.getParameters();
            Object[] parameterValues = new Object[parameters.length];

            /*
            * Récupération et conversion des paramètres HTTP.
            */
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                String parameterName = parameter.getName();
                String parameterValue = req.getParameter(parameterName);
                


                parameterValues[i] = utilitaire.convertValue(
                        parameterValue,
                        parameter.getType()
                );
            }

            /*
            * La méthode doit être appelée une seule fois,
            * après avoir préparé tous ses arguments.
            */
            Object result = methodToInvoke.invoke(
                    controllerInstance,
                    parameterValues
            );

            if (methodToInvoke.isAnnotationPresent(annotation.JSON.class)) {
                if (methodToInvoke.getReturnType() == String.class) {
                    res.setContentType("text/plain;charset=UTF-8");

                    if (result != null) {
                        res.getWriter().write(result.toString());
                    }
                } else {
                    if(result !=null) {
                         // Retourne un objet vide si le résultat est null
                          res.setContentType("application/json;charset=UTF-8");

                        com.google.gson.Gson gson =
                            new com.google.gson.Gson();
                        res.getWriter().write(gson.toJson(result));
                       
                    }
                    else {
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("{}"); // Retourne un objet JSON vide
                    }
                   
                }

                return;
            }
           
            else if(result instanceof ModelAndView) {

                ModelAndView modelAndView = (ModelAndView) result;

                String nomPage = modelAndView.getView();
                Map<String, Object> model = modelAndView.getModel();

                if (model != null) {
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                }

                req.getRequestDispatcher(
                        prefixeView + nomPage + suffixeView
                ).forward(req, res);
            }
            else{
                res.setContentType("application/json;charset=UTF-8");
                com.google.gson.Gson gson = new com.google.gson.Gson();
                res.getWriter().write(gson.toJson(result));


            }

        } catch (Exception e) {
            e.printStackTrace();

            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("text/plain;charset=UTF-8");
            res.getWriter().write(
                    "Erreur d'exécution : " + e.getMessage()
            );
        }
    }
}