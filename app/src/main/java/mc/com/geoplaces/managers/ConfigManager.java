package mc.com.geoplaces.managers;

public class ConfigManager {

    private static final ConfigManager ourInstance = new ConfigManager();

    private String version = "1.0";
    private String protocol = "https";
    private String url = "mock-api-mobile.dev.lalamove.com";

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    public String getVersion(){
        return version;
    }

    public String getUrl(){
        return url;
    }

    public String getWebApiRoot(){
        return protocol + "://" + url;
    }
}