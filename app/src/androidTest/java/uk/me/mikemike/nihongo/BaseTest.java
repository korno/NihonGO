package uk.me.mikemike.nihongo;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 11/16/17.
 */

public abstract class BaseTest {

    Realm mRealm;

    @Before
    public void setupBlankRealm() {
        Realm.init(InstrumentationRegistry.getTargetContext());
        RealmConfiguration r = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().name("test").build();
        Realm.setDefaultConfiguration(r);
        mRealm = Realm.getDefaultInstance();
        deleteData();
    }


    public void deleteData(){
        mRealm.beginTransaction();
        mRealm.where(Card.class).findAll().deleteAllFromRealm();
        mRealm.where(Deck.class).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    public void assertHasNumberOfDecks(int number){
        assertEquals(number, mRealm.where(Deck.class).findAll().size());
    }

    public RealmResults<Deck> getDecks(){
        return mRealm.where(Deck.class).findAll();
    }

    public void assertDeckFieldsAreSame(Deck expected, Deck b){
        assertEquals(expected.getName(), b.getName());
        assertEquals(expected.getAuthor(), b.getAuthor());
        assertEquals(expected.getDescription(), b.getDescription());
    }

    public void assertHasNumberOfCards(Deck d, int expected){
        assertEquals(expected, d.getCards().size());
    }

    @After
    public  void closeRealm(){
        mRealm.close();
    }
}
