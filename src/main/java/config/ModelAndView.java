package config;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> model = new java.util.HashMap<>();

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }
    public ModelAndView(){
        
    }
    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public Map<String, Object> getModel() {
        return model;
    }
    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
    public void setAttribute(String key, Object value) {
        this.model.put(key, value);
    }

    
}
