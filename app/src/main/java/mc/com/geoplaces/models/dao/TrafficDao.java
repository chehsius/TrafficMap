package mc.com.geoplaces.models.dao;

import androidx.annotation.NonNull;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import mc.com.geoplaces.models.entities.TrafficEntity;

public class TrafficDao {

    private Realm realm;

    public TrafficDao(@NonNull Realm realm) {
        this.realm = realm;
    }

    public void save(final List<TrafficEntity> trafficEntities) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(trafficEntities);
            }
        });
    }

    public RealmResults<TrafficEntity> loadAll() {
        return realm.where(TrafficEntity.class).findAll();
    }

    public TrafficEntity loadById(int id) {
        return realm.where(TrafficEntity.class).equalTo("id", id).findFirst();
    }

    public long count() {
        return realm.where(TrafficEntity.class).count();
    }
}
