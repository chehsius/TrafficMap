package mc.com.geoplaces.models.dao;

import android.support.annotation.NonNull;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import mc.com.geoplaces.models.entities.DeliveryEntity;

public class DeliveryDao {
    private Realm realm;

    public DeliveryDao(@NonNull Realm realm) {
        this.realm = realm;
    }

    public void save(final List<DeliveryEntity> deliveryEntities) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(deliveryEntities);
            }
        });
    }

    public RealmResults<DeliveryEntity> loadAll() {
        return realm.where(DeliveryEntity.class).findAll();
    }

    public DeliveryEntity loadById(int id) {
        return realm.where(DeliveryEntity.class).equalTo("id", id).findFirst();
    }

    public long count() {
        return realm.where(DeliveryEntity.class).count();
    }
}
