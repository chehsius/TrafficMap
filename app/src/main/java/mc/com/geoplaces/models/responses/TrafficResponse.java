package mc.com.geoplaces.models.responses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mc.com.geoplaces.R;
import mc.com.geoplaces.managers.ApiManager;
import mc.com.geoplaces.managers.ApiServerCallback;
import mc.com.geoplaces.managers.ConfigManager;
import mc.com.geoplaces.models.entities.TrafficEntity;
import mc.com.geoplaces.views.activities.SplashActivity;

public class TrafficResponse {

//    private static final String END_POINT = "/deliveries";
//    private static final int LIMIT_LOAD_DELIVERY = 20;

    public TrafficResponse() {
    }

//    private String getTrafficUrl(int offset){
//        return ConfigManager.getInstance().getWebApiRoot() + END_POINT + "?offset=" + offset + "&limit=" + LIMIT_LOAD_DELIVERY;
//    }

    private String getTrafficUrl(int page) {
        return ConfigManager.getInstance().getWebApiRoot() + page;
    }

    public void getTrafficProblems(int page, final OnTrafficResponseCallBack onTrafficResponseCallBack) {
        ApiManager.getsInstance().GET(getTrafficUrl(page), new ApiServerCallback() {
            @Override
            public boolean onSuccess(String htmlSourceCode) {
                onTrafficResponseCallBack.onSuccess(parseJsonToTrafficProblems(htmlSourceCode));
                return false;
            }

            @Override
            public boolean onFailure(String errorState) {
                onTrafficResponseCallBack.onError(errorState);
                return false;
            }
        });
    }

    private ArrayList<TrafficEntity> parseJsonToTrafficProblems(String htmlSourceCode) {
        ArrayList<TrafficEntity> trafficEntities = new ArrayList<>();

        //try {
            Document doc = Jsoup.parse(htmlSourceCode);
            Elements total = doc.select("[data-title=項次]");
            Elements types = doc.select("[data-title=通報類別]");
            Elements addresses = doc.select("[data-title=挖掘地點] a");
            Elements fromDates = doc.select("[data-title=起始日期]");
            Elements endDates = doc.select("[data-title=結束日期]");

            Iterator id = total.iterator();
            Iterator type = types.iterator();
            Iterator address = addresses.iterator();
            Iterator fromDate = fromDates.iterator();
            Iterator endDate = endDates.iterator();
            while (id.hasNext()) {
                String date = "";

                TrafficEntity trafficEntity = new TrafficEntity();
                trafficEntity.setId(Integer.valueOf(((Element)id.next()).ownText()));
                //trafficEntity.setType(((Element)type.next()).ownText());
                trafficEntity.setAddress(((Element)address.next()).ownText());
                date = ((Element)fromDate.next()).ownText() + " 至 "  + ((Element)endDate.next()).ownText();
                trafficEntity.setDate(date);

                LatLng location;
                String addressString = trafficEntity.getAddress();
                String updatedAddress = UpdateAddress(addressString);
                location = getLocationFromAddress(SplashActivity.getAppContext(), updatedAddress);

                if (location == null) {
                    // 若無搜尋到結果，將地點設為總統府
                    location = new LatLng(25.04, 121.5114);
                }
                trafficEntity.setLat(location.latitude);
                trafficEntity.setLng(location.longitude);
                float distance = SplashActivity.getDistanceFromCurrentLocation(location);
                Log.d("TrafficDetailsFragment", "trafficEntity Latitude:" + trafficEntity.getLat());
                Log.d("TrafficDetailsFragment", "trafficEntity Longitude:" + trafficEntity.getLng());
                Log.d("TrafficDetailsFragment", "trafficEntity Distance:" + distance);
                String TypeAndDistance = "       距離："+ Math.round(distance) + " 公尺";
                trafficEntity.setType(((Element)type.next()).ownText() + TypeAndDistance);
                trafficEntities.add(trafficEntity);
            }

            return trafficEntities;
        /*} catch (Exception e) {
            e.printStackTrace();
        }
        return null;*/
    }


    public LatLng getLocationFromAddress(Context context, String inputAddress)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng AddressLatLng = null;
        Log.d("TrafficDetailsFragment", "SearchAddress:" + inputAddress);
        // GeoCoder 無法執行
        if (!Geocoder.isPresent())
        {
            Log.d("TrafficDetailsFragment", "Geocoder Fail");
            return null;
        }
        else Log.d("TrafficDetailsFragment", "Geocoder Success");
        try
        {
            address = coder.getFromLocationName(inputAddress, 5);
            // 找不到結果
            if (address == null)
            {
                Log.d("TrafficDetailsFragment", "Result Not Found");
                return null;
            }
            // 第一個結果
            Address location = address.get(0);
            double Lat = location.getLatitude();
            double Lon = location.getLongitude();
            Log.d("TrafficDetailsFragment", "Latitude:" + Lat);
            Log.d("TrafficDetailsFragment", "Longitude:" + Lon);
            AddressLatLng = new LatLng(Lat, Lon);
        }
        catch (Exception e)
        {
            Log.d("TrafficDetailsFragment", "Exception e");
            e.printStackTrace();
        }
        return AddressLatLng;
    }

    public String UpdateAddress(String inputAddress)
    {
        String returnAddress;
        int FirstIndex, LastIndex;
        FirstIndex = inputAddress.indexOf("區");

        if (FirstIndex == -1) {
            FirstIndex = inputAddress.indexOf("里");
            if (FirstIndex == -1)
            {
                FirstIndex = 0;
            }
            else FirstIndex -= 2;
        }
        else
            FirstIndex -= 2;   // 區前2字開始取

        LastIndex = inputAddress.indexOf("號");
        if (LastIndex == -1) {
            LastIndex = inputAddress.indexOf("巷");
            if (LastIndex == -1) {
                LastIndex = inputAddress.indexOf("段");
                if (LastIndex == -1) {
                    LastIndex = inputAddress.indexOf("路");
                    if (LastIndex == -1) {
                        LastIndex = inputAddress.indexOf("街");
                    }
                }
            }
        }
        if (LastIndex == -1) LastIndex = inputAddress.length() - 1;
        returnAddress = inputAddress.substring(FirstIndex, LastIndex + 1);
        return returnAddress;
    }
}
