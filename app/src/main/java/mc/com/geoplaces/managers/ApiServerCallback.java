package mc.com.geoplaces.managers;

import org.json.JSONArray;

public interface ApiServerCallback {

    boolean onSuccess(String htmlSourceCode);
    boolean onFailure(String errorState);
}
