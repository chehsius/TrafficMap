package mc.com.geoplaces.models.repositories;


import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;

import io.realm.Realm;
import mc.com.geoplaces.managers.RealmManager;
import mc.com.geoplaces.models.dao.DeliveryDao;
import mc.com.geoplaces.models.entities.DeliveryEntity;
import mc.com.geoplaces.models.responses.DeliveryResponse;
import mc.com.geoplaces.models.responses.OnDeliveryResponseCallBack;
import mc.com.geoplaces.utils.Utils;

public class DeliveryRepository {
    private DeliveryDao deliveryDao;
    private DeliveryResponse deliveryRepository;
    private int index = 0;

    public DeliveryRepository() {
        deliveryDao = new DeliveryDao(Realm.getDefaultInstance());
        deliveryRepository = new DeliveryResponse();
    }

    public void getDeliveryList(Context context,boolean hasNext, final OnDeliveryListLoadedCallback onDeliveryListLoadedCallback){
        try {
            RealmManager.open();
            if (Utils.isNetworkAvailable(context)){
                deliveryRepository.getDeliveryList(index, new OnDeliveryResponseCallBack() {
                    @Override
                    public void onSuccess(ArrayList<DeliveryEntity> deliveryEntitiesResult) {
                        deliveryDao.save(deliveryEntitiesResult);
                        index = deliveryEntitiesResult.get(deliveryEntitiesResult.size()-1).getId()+1;
                        onDeliveryListLoadedCallback.onSuccess(deliveryEntitiesResult);
                    }

                    @Override
                    public void onError(String errorState) {
                        if (index == 0 && deliveryDao.loadAll().size() != 0){
                            ArrayList<DeliveryEntity> deliveryEntities = new ArrayList<>();
                            deliveryEntities.addAll(deliveryDao.loadAll());
                            index = deliveryEntities.get(deliveryEntities.size()-1).getId()+1;
                            onDeliveryListLoadedCallback.onSuccess(deliveryEntities);
                        } else {
                            onDeliveryListLoadedCallback.onError(errorState);
                        }
                    }
                });
            } else {
                if(!hasNext){
                    if (deliveryDao.loadAll().size() != 0){
                        ArrayList<DeliveryEntity> deliveryEntities = new ArrayList<>();
                        deliveryEntities.addAll(deliveryDao.loadAll());
                        index = deliveryEntities.get(deliveryEntities.size()-1).getId()+1;
                        onDeliveryListLoadedCallback.onSuccess(deliveryEntities);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onDeliveryListLoadedCallback.onError("error");
                            }
                        },1000);
                    }
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onDeliveryListLoadedCallback.onError("error");
                        }
                    },1000);
                }
            }
        } finally {
            RealmManager.close();
        }

    }

    public DeliveryEntity getDeliveryById(int id){
        try {
            RealmManager.open();
            return deliveryDao.loadById(id);

        } finally {
            RealmManager.close();
        }
    }
}
