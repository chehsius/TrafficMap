package mc.com.geoplaces.models.repositories;

import java.util.ArrayList;

import mc.com.geoplaces.models.entities.DeliveryEntity;

public interface OnDeliveryListLoadedCallback {

    void onSuccess(ArrayList<DeliveryEntity> deliveryEntities);
    void onError(String errorState);

}
