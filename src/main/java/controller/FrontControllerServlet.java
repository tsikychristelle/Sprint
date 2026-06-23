package controller;
import java.io.IOException;
import java.util.List;

import config.Utilitaire;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class FrontControllerServlet extends HttpServlet{
    private List<Class<?>> controllers;
    public void init() throws ServletException {
        Utilitaire utilitaire = new Utilitaire();
        controllers = utilitaire.getClassesWithAnnotation("com.monApp");
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
       String method = req.getMethod();
    //    res.getWriter().println("Bienvenue dans le Framework de Christelle");
    //     res.getWriter().println("Vous avez utilisé la méthode: " + method);
        for (Class<?> controller : controllers) {
            res.getWriter().println("Controller trouvé: " + controller.getName());
        }
        

    }

}