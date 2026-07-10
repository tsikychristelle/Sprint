package listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.Controller;
import annotation.Url2;
import config.ClassMethode;
import config.MethodeUrl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import util.Util;
public class AppInitializer implements ServletContextListener {
    private List<String> listControllers = new ArrayList<>();
    private Map<MethodeUrl, ClassMethode> urlMethodMappings = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        List<String> packageNames = Util.splitString(servletContext.getInitParameter("scanPackages"), ",");

        try {
            listControllers = Util.findClasses(packageNames, Controller.class);
            Util.findUrlMethodMappings(packageNames, urlMethodMappings, Controller.class, Url2.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing application", e);
        }

        servletContext.setAttribute("listControllers", listControllers);
        servletContext.setAttribute("urlMethodMappings", urlMethodMappings);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
    }
    
}
