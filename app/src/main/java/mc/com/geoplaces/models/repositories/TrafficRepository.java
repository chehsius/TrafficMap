package mc.com.geoplaces.models.repositories;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import mc.com.geoplaces.managers.RealmManager;
import mc.com.geoplaces.models.dao.TrafficDao;
import mc.com.geoplaces.models.entities.TrafficEntity;
import mc.com.geoplaces.models.responses.TrafficResponse;
import mc.com.geoplaces.models.responses.OnTrafficResponseCallBack;
import mc.com.geoplaces.utils.Utils;

public class TrafficRepository {

    private TrafficDao trafficDao;
    private TrafficResponse trafficRepository;
    private int page = 1;

    public TrafficRepository() {
        trafficDao = new TrafficDao(Realm.getDefaultInstance());
        trafficRepository = new TrafficResponse();
    }

    public void getTrafficProblems(Context context, boolean hasNext, final OnTrafficProblemsLoadedCallback onTrafficProblemsLoadedCallback){
        try {
            RealmManager.open();
            if (Utils.isNetworkAvailable(context)){
                trafficRepository.getTrafficProblems(page, new OnTrafficResponseCallBack() {
                    @Override
                    public void onSuccess(ArrayList<TrafficEntity> trafficEntitiesResult) {
                        trafficDao.save(trafficEntitiesResult);
//                        page = trafficEntitiesResult.get(trafficEntitiesResult.size() - 1).getId() + 1;
                        page++;
                        Log.d("Page Test onSuccess", String.valueOf(page));
                        onTrafficProblemsLoadedCallback.onSuccess(trafficEntitiesResult);
                    }

                    @Override
                    public void onError(String errorState) {
                        if (page == 1 && trafficDao.loadAll().size() != 0){
                            ArrayList<TrafficEntity> trafficEntities = new ArrayList<>();
                            trafficEntities.addAll(trafficDao.loadAll());
//                            page = trafficEntities.get(trafficEntities.size() - 1).getId() + 1;
                            page++;
                            Log.d("Page Test onError", String.valueOf(page));
                            onTrafficProblemsLoadedCallback.onSuccess(trafficEntities);
                        } else {
                            onTrafficProblemsLoadedCallback.onError(errorState);
                        }
                    }
                });
            } else {
                if (!hasNext) {
                    if (trafficDao.loadAll().size() != 0){
                        ArrayList<TrafficEntity> trafficEntities = new ArrayList<>();
                        trafficEntities.addAll(trafficDao.loadAll());
//                        page = trafficEntities.get(trafficEntities.size() - 1).getId() + 1;
                        page++;
                        Log.d("Page Test !hasNext", String.valueOf(page));
                        onTrafficProblemsLoadedCallback.onSuccess(trafficEntities);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onTrafficProblemsLoadedCallback.onError("error");
                            }
                        },1000);
                    }
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onTrafficProblemsLoadedCallback.onError("error");
                        }
                    },1000);
                }
            }
        } finally {
            RealmManager.close();
        }
    }

    public TrafficEntity getTrafficProblemsById(int id){
        try {
            RealmManager.open();
            return trafficDao.loadById(id);

        } finally {
            RealmManager.close();
        }
    }
}
