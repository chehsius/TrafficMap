package mc.com.geoplaces.managers;

import io.realm.Realm;

public class RealmManager {

    private static Realm realm;

    public static Realm open() {
        realm = Realm.getDefaultInstance();
        return realm;
    }

    public static void close() {
        if (realm != null) {
            realm.close();
        }
    }

    public static void checkForOpenRealm() {
        if (realm == null || realm.isClosed()) {
            throw new IllegalStateException("RealmManager: Realm is closed, call open() method first");
        }
    }

    public static Realm getRealm() {
        return realm;
    }
}
