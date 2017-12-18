package uk.me.mikemike.nihongo.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmList;
import uk.me.mikemike.nihongo.NihongoRealmConfiguration;
import uk.me.mikemike.nihongo.data.NihongoRepository;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.utils.RealmListLiveData;
import uk.me.mikemike.nihongo.utils.RealmResultsLiveData;
import uk.me.mikemike.nihongo.utils.RealmObjectLiveData;

/**
 * Created by mike on 12/12/17.
 */

public class DebugCardListViewModel extends BaseNihongoViewModel {

    protected RealmObjectLiveData<Deck> mSourceDeck;
    protected RealmListLiveData<Card> mCards;

    public DebugCardListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSourceDeck(String id){
        if(mSourceDeck == null){
            Deck d = mRepos.getDeckByID(id);
            mSourceDeck = new RealmObjectLiveData<>(d);
        }
    }

    public RealmObjectLiveData<Deck> getSourceDeck(){
        return mSourceDeck;
    }

    public LiveData<RealmList<Card>> getSourceDeckCardsAsLiveData(){
        if(mCards == null){
            mCards = new RealmListLiveData<>(mSourceDeck.getValue().getCards());
        }
        return mCards;
    }

}
