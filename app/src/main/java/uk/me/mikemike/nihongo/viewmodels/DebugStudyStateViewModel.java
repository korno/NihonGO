package uk.me.mikemike.nihongo.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.realm.RealmList;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.utils.RealmListLiveData;
import uk.me.mikemike.nihongo.utils.RealmObjectLiveData;

/**
 * Created by mike on 12/18/17.
 */

public class DebugStudyStateViewModel extends  BaseNihongoViewModel {

    protected LiveData<StudyDeck> mSourceDeck;
    protected LiveData<RealmList<StudyCard>> mStudyCards;

    public DebugStudyStateViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSourceStudyDeck(String id){
        if(mSourceDeck == null){
            mSourceDeck = new RealmObjectLiveData<>(mRepos.getStudyDeckByID(id));
            mStudyCards = new RealmListLiveData<>(mSourceDeck.getValue().getStudyCards());
        }
    }

    public LiveData<StudyDeck> getStudyDeck(){return  mSourceDeck;}
    public LiveData<RealmList<StudyCard>> getStudyCard(){return mStudyCards;}

}
