package mc.com.geoplaces.models.responses;

import java.util.ArrayList;

import mc.com.geoplaces.models.entities.TrafficEntity;

public interface OnTrafficResponseCallBack {

    void onSuccess(ArrayList<TrafficEntity> trafficEntities);
    void onError(String errorState);
}
