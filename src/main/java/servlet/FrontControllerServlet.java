package src.main.java.servlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

public class FrontControllerServlet extends HttpServlet{
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
       res.getWriter().println("Bienvenue dans le Framework de Christelle");
        res.getWriter().println("Vous avez utilisé la méthode: " + method);

    }

}