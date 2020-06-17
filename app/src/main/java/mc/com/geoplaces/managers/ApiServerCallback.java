package mc.com.geoplaces.managers;

import org.json.JSONArray;

public interface ApiServerCallback {

    boolean onSuccess(JSONArray result);
    boolean onFailure(String errorState);

}
