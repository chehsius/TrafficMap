package mc.com.geoplaces.models.responses;

import java.util.ArrayList;

import mc.com.geoplaces.models.entities.DeliveryEntity;

public interface OnDeliveryResponseCallBack {
    void onSuccess(ArrayList<DeliveryEntity> deliveryEntities);
    void onError(String errorState);
}
