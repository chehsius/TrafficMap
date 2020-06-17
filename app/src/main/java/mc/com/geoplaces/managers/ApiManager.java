package mc.com.geoplaces.managers;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import mc.com.geoplaces.GeoPlacesApplication;

public class ApiManager {

    private static ApiManager ourInstance = null;

    private ApiManager() {
    }

    public static ApiManager getsInstance() {
        return ourInstance == null ? new ApiManager() : ourInstance;
    }


    public void GET(final String url, final ApiServerCallback callback) {
        try {
            HttpsTrustManager.allowAllSSL();
            JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                callback.onSuccess(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.onFailure(e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        callback.onFailure(error.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e.getMessage());
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };
            int x = 2;
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                    x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            GeoPlacesApplication.getInstance().addToRequestQueue(jsonObjReq, "tag");
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        }

    }

    public void CANCELALLPENDINGREQUESTS() {
        GeoPlacesApplication.getInstance().cancelPendingRequests(GeoPlacesApplication.class
                .getSimpleName());
    }
}