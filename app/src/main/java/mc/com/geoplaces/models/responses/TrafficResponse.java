package mc.com.geoplaces.models.responses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

import mc.com.geoplaces.managers.ApiManager;
import mc.com.geoplaces.managers.ApiServerCallback;
import mc.com.geoplaces.managers.ConfigManager;
import mc.com.geoplaces.models.entities.TrafficEntity;

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

        try {
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
                trafficEntity.setType(((Element)type.next()).ownText());
                trafficEntity.setAddress(((Element)address.next()).ownText());
                date = ((Element)fromDate.next()).ownText() + " 至 "  + ((Element)endDate.next()).ownText();
                trafficEntity.setDate(date);

                trafficEntities.add(trafficEntity);
            }

            return trafficEntities;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
