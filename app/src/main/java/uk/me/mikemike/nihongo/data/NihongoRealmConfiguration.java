package uk.me.mikemike.nihongo.data;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mike on 12/11/17.
 */

public final class NihongoRealmConfiguration {

    public static String NIHONGOREALM_NAME = "nihongo";

    public static void configureNihongoRealm(Context context){
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(NIHONGOREALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
