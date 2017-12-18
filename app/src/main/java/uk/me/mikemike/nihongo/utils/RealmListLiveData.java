package uk.me.mikemike.nihongo.utils;

import android.arch.lifecycle.LiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by mike on 12/12/17.
 */

public class RealmListLiveData<T extends RealmModel> extends LiveData<RealmList<T>> {

    private RealmList<T> results;
    private final RealmChangeListener<RealmList<T>> listener =
            new RealmChangeListener<RealmList<T>>() {
                @Override
                public void onChange(RealmList<T> results) {
                    setValue(results);
                }
            };

    public RealmListLiveData(RealmList<T> realmResults) {
        results = realmResults;
        if(realmResults.isLoaded()){
            setValue(results);
            postValue(results);

        }
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }
}
