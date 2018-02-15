package uk.me.mikemike.nihongo.utils;


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
    }

    @Override
    public void setValue(T value){
        if(value != getValue()){
           if(getValue() != null) {
               getValue().removeAllChangeListeners();
           }
           if(value != null) {
               value.addChangeListener(listener);
           }
       }
        super.setValue(value);
    }

    @Override
    protected void onActive() {
        if(mObject != null) mObject.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        if(mObject != null) mObject.removeChangeListener(listener);
    }

}
