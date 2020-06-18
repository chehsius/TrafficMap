package mc.com.geoplaces.models.repositories;

import java.util.ArrayList;

import mc.com.geoplaces.models.entities.TrafficEntity;

public interface OnTrafficProblemsLoadedCallback {

    void onSuccess(ArrayList<TrafficEntity> trafficEntities);
    void onError(String errorState);
}
