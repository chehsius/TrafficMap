package mc.com.geoplaces;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class GeoPlacesApplication extends Application {

    public static final String TAG = GeoPlacesApplication.class
            .getSimpleName();
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    private static GeoPlacesApplication ourInstance;
    private RequestQueue requestQueue;

    public static synchronized GeoPlacesApplication getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("traffic.realm").build();
        Realm.setDefaultConfiguration(config);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getBaseContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
