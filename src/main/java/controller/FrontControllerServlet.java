package controller;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import annotation.AuthRole;
import config.ClassMethode;
import config.MapFile;
import config.ModelAndView;
import config.Url2Method;
import config.UrlMethode;
import config.Utilitaire;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
@MultipartConfig
public class FrontControllerServlet extends HttpServlet{
    private List<Class<?>> controllers;
    private List<Method> methodes;
    List<UrlMethode> listUrlMethode;
    List<Url2Method> listUrl2Method;
    private List<Method> listMethodeLogin;
    
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
        listMethodeLogin = (List<Method>) servletContext.getAttribute("listMethodeLogin");
        listUrl2Method = (List<Url2Method>) servletContext.getAttribute("listUrl2Method");
        prefixeView = getServletConfig().getInitParameter("prefixeView");
        controllers = (List<Class<?>>) servletContext.getAttribute("classController");
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
    private Object bindObject(Class<?> objectClass,String prefix,HttpServletRequest req) throws Exception {

        Object object = objectClass.getDeclaredConstructor().newInstance();

        for (Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);

            String fullName = prefix + "." + field.getName();

            if (Utilitaire.isSimpleType(field.getType())) {
                String value = req.getParameter(fullName);

                if (value != null) {
                    Object convertedValue =
                        utilitaire.convertValue(value, field.getType());

                    field.set(object, convertedValue);
                }
            } else {
                Object nestedObject = bindObject(
                    field.getType(),
                    fullName,
                    req
                );

                field.set(object, nestedObject);
            }
        }

        return object;
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String httpMethod = req.getMethod();
        HttpSession session = req.getSession(false);

        ClassMethode classMethode = null;

        Url2Method url2Methode = utilitaire.getMethod2Url(listUrl2Method, path);

        if (url2Methode != null
                && url2Methode.getMethodeUrl().getMethode().equals(httpMethod)) {
            classMethode = url2Methode.getClassMethode();
        }

        // Une méthode annotée @Url n'impose pas de méthode HTTP : elle est
        // invocable en GET comme en POST, tant qu'aucune route @Url2 plus
        // spécifique n'a déjà été trouvée pour ce chemin.
        if (classMethode == null) {
            UrlMethode urlMethode = utilitaire.getMethodeUrl(listUrlMethode, path);

            if (urlMethode != null) {
                classMethode = urlMethode.getClassMethode();
            }
        }

        if (classMethode == null) {

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

                if (listUrlMethode != null) {
                    for (UrlMethode route : listUrlMethode) {
                        res.getWriter().write(
                                "<li><b>GET/POST</b> : "
                                + route.getUrl()
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
                Class<?> controllerClass = classMethode.getClasse();

                if (!controllerClass.isAnnotationPresent(annotation.Controller.class)) {
                    sendError(
                            res,
                            HttpServletResponse.SC_FORBIDDEN,
                            "403 - Interdit",
                            "La classe " + controllerClass.getSimpleName()
                                    + " n'est pas annotée @Controller."
                    );
                    return;
                }

                Object controllerInstance =
                        controllerClass.getDeclaredConstructor().newInstance();

                Method methodToInvoke = classMethode.getMethode();

                Parameter[] parameters = methodToInvoke.getParameters();
                Object[] parameterValues = new Object[parameters.length];

                /*
                * Récupération et conversion des paramètres HTTP.
                */
                for (int i = 0; i < parameters.length; i++) {

                    Parameter parameter = parameters[i];
                    Class<?> classeParameters = parameter.getType();
                    if (Utilitaire.isSimpleType(classeParameters)) {
                            String value = req.getParameter(parameter.getName());

                            if (value != null) {
                                parameterValues[i] = utilitaire.convertValue(
                                    value,
                                    classeParameters
                                );
                            }
                            
                    }
                    
                    else if(parameter.getType()==MapFile.class) {
                        String partName = parameter.getName();
                        Part filePart = req.getPart(partName);

                        if (filePart == null || filePart.getSize() == 0) {
                            throw new ServletException(
                                "Aucun fichier reçu pour le champ : " + partName
                            );
                        }

                        parameterValues[i] = utilitaire.manageFile(filePart);
                    }
                    else if (parameter.getType() == HttpSession.class) {
                        parameterValues[i] = req.getSession(true);
                    }
                    else if (parameter.getType() == HttpServletRequest.class) {
                        parameterValues[i] = req;
                    }
                    else if (parameter.getType() == HttpServletResponse.class) {
                        parameterValues[i] = res;
                    }
                    else {
                            parameterValues[i] = bindObject(
                                classeParameters,
                                parameter.getName(),
                                req
                            );
                    }
                    
                    // else{
                    //     Object paramInstance = classeParameters.getDeclaredConstructor().newInstance();
                    //     boolean hasValue = false;
                    //     Field[] fields = classeParameters.getDeclaredFields();
                    //     for(Field field : fields){
                    //         String fieldName = field.getName();
                    //         String fieldValue = req.getParameter(fieldName);
                    //         if(fieldValue != null){
                    //             Object convertedValue = utilitaire.convertValue(
                    //                 fieldValue,
                    //                 field.getType()
                    //             );
                    //             field.setAccessible(true);
                    //             hasValue = true;
                    //             field.set(paramInstance, convertedValue);
                    //             if(hasValue){
                    //                 parameterValues[i] = paramInstance;
                    //             }
                    //         }
                    //     }
                    // }
                    
                }
                if (methodToInvoke.isAnnotationPresent(annotation.Login.class)) {
                     session = req.getSession(true);

                    Object username = parameterValues[0];
                    Object role = parameterValues[1];
                    if (username instanceof String user
                            && !user.isBlank()) {

                        session.setAttribute("user", user);
                        session.setAttribute("role", role);
                        session.setAttribute("isAuthentificate", true);
                    }
                }
                if( methodToInvoke.isAnnotationPresent(annotation.Auth.class)) {
                    session = req.getSession(false);

                    if (session == null
                            || session.getAttribute("isAuthentificate") == null
                            || !(Boolean) session.getAttribute("isAuthentificate")) {

                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("text/html;charset=UTF-8");

                        res.getWriter().write("<h1>401 - Non autorisé</h1>");
                        res.getWriter().write(
                                "<p>Vous devez être authentifié pour accéder à cette ressource.</p>"
                        );

                        return;
                    }
                }
                if (!checkAccess(methodToInvoke, req, res)) {
                    return;
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
    private void sendError(HttpServletResponse res, int status, String titre, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("text/html;charset=UTF-8");
        res.getWriter().write("<h1>" + titre + "</h1><p>" + message + "</p>");
    }

    /** Retourne true si l'accès est autorisé, sinon écrit la réponse d'erreur. */
    private boolean checkAccess(Method m, HttpServletRequest req, HttpServletResponse res) throws IOException {
        AuthRole authRole = m.getAnnotation(AuthRole.class);
        if (authRole == null) {
            authRole = m.getDeclaringClass().getAnnotation(AuthRole.class); // annotation sur la classe
        }
        if (authRole == null) {
            return true; // pas de restriction
        }

        HttpSession s = req.getSession(false);
        boolean connecte = s != null
                && Boolean.TRUE.equals(s.getAttribute("isAuthentificate"))
                && s.getAttribute("role") != null;

        if (!connecte) {
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "401 - Non autorisé",
                    "Vous devez être authentifié pour accéder à cette ressource.");
            return false;
        }

        String userRole = s.getAttribute("role").toString();
        if (!Arrays.asList(authRole.role()).contains(userRole)) {
            sendError(res, HttpServletResponse.SC_FORBIDDEN, "403 - Interdit",
                    "Vous n'avez pas le rôle requis pour accéder à cette ressource.");
            return false;
        }
        return true;
    }
}