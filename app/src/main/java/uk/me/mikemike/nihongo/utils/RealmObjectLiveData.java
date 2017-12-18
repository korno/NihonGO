package uk.me.mikemike.nihongo.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmObjectLiveData<T extends RealmObject> extends MutableLiveData<T> {

    private T mObject;
    private final RealmChangeListener<T> listener =
            new RealmChangeListener<T>() {
                @Override
                public void onChange(T results) {
                    setValue(results);
                }
            };

    public RealmObjectLiveData(T realmResults) {
        mObject = realmResults;
        setValue(mObject);
        postValue(mObject);
    }

    @Override
    public void setValue(T value){
       super.setValue(value);
    }

    @Override
    protected void onActive() {
        mObject.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        mObject.removeChangeListener(listener);
    }

}
