package library;

import java.util.ResourceBundle;

public final class ResourceHandler {
    private final ResourceBundle resourceBundle;

    public ResourceHandler(String profile){
        this.resourceBundle = ResourceBundle.getBundle(profile);
    }

    public String getProperty(String key){
        return resourceBundle.getString(key);
    }
}
