package library;

import java.util.ResourceBundle;

public final class ApplicationResourcesHandler {
    private static ResourceBundle resourceBundle;

    private ApplicationResourcesHandler(){
    }

    private static ResourceBundle getResourceBundleInstance(){
        if(resourceBundle == null){
            resourceBundle = ResourceBundle.getBundle("application");
        }
        return resourceBundle;
    }

    public static String getProperty(String key){
        return getResourceBundleInstance().getString(key);
    }
}
