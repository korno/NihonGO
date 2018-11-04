package uk.me.mikemike.nihongo;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;

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


    public void assertMoreThan(float lowlimit, float value){
        assertEquals(true, value >= lowlimit);
    }

    public void deleteData(){
        mRealm.beginTransaction();
        mRealm.where(StudySession.class).findAll().deleteAllFromRealm();
        mRealm.where(Card.class).findAll().deleteAllFromRealm();
        mRealm.where(Deck.class).findAll().deleteAllFromRealm();
        mRealm.where(LearningState.class).findAll().deleteAllFromRealm();
        mRealm.where(StudyCard.class).findAll().deleteAllFromRealm();
        mRealm.where(StudyDeck.class).findAll().deleteAllFromRealm();

        mRealm.commitTransaction();
    }

    public void assertHasNumberOfDecks(int number){
        assertEquals(number, mRealm.where(Deck.class).findAll().size());
    }

    public RealmResults<Deck> getDecks(){
        return mRealm.where(Deck.class).findAll();
    }

    public RealmResults<StudyDeck> getStudyDecks() { return mRealm.where(StudyDeck.class).findAll();}

    public void assertDeckFieldsAreSame(Deck expected, Deck b){
        assertEquals(expected.getName(), b.getName());
        assertEquals(expected.getAuthor(), b.getAuthor());
        assertEquals(expected.getDescription(), b.getDescription());
    }


    public void assertCardFieldsAreSame(Card expected, Card value){
        assertEquals(expected.getCardType(), value.getCardType());
        assertEquals(expected.getJapaneseDisplay(), value.getJapaneseDisplay());
        assertEquals(expected.getJapaneseKanji(), value.getJapaneseKanji());
        assertEquals(expected.getMainLanguage(), value.getMainLanguage());
        assertEquals(expected.getJapaneseHiragana(), value.getJapaneseHiragana());
        int i=0;
        RealmList<String> valueS =value.getSynonyms();
        for(String s: expected.getSynonyms()){
            assertEquals(s, valueS.get(i));
            i++;
        }
    }


    public void assertStudyDeckFieldsAreSame(StudyDeck expected, StudyDeck d, boolean checkSourceDeck){
        assertEquals(expected.getStudyDeckID(), d.getStudyDeckID());
        assertEquals(expected.getName(), d.getName());
        if(checkSourceDeck){
            assertDeckFieldsAreSame(expected.getSourceDeck(), d.getSourceDeck());
        }
    }

    public void assertHasNumberOfCards(Deck d, int expected){
        assertEquals(expected, d.getCards().size());
    }

    public void assertIsCardType(Card c, Card.CardType expected){
        assertEquals(c.getCardType(), expected);
    }

    public void assertNoDeckWithName(String name){
        assertEquals(null, mRealm.where(Deck.class).equalTo("mName", name).findFirst());
    }

    public Deck getDeckByName(String name){
        return mRealm.where(Deck.class).equalTo("mName", name).findFirst();
    }

    public Card getDummyNonRealmCard(){
        return new Card("main", "hiragana",
                "kanji", "display", new RealmList<String>(), Card.CardType.Noun);
    }

    public LearningState getDummyNonRealmLearningState(){
        return new LearningState(new Date(), new Date(),LearningState.STARTING_E_VALUE, 0, 0);
    }

    public void addDecks(int count, int cardCount, boolean addStudyDeck){
        for(int i =0; i<count; i++){
            String currentCount = String.valueOf(i);
            Deck d = new Deck("deck_" + currentCount, "description_" + currentCount, "test_author",
                            new RealmList<Card>());
            mRealm.beginTransaction();
            Deck managedD = mRealm.copyToRealmOrUpdate(d);
            for(int y=0; y<cardCount; y++){
                String currentCardCount = String.valueOf(y);
                Card c = new Card("mainLanguage" + currentCardCount, "hiragana" + currentCardCount,
                        "kanji" + currentCardCount, "display" + currentCardCount, new RealmList<String>(), Card.CardType.Other);
                managedD.getCards().add(c);
            }
            mRealm.commitTransaction();

        }
        if(addStudyDeck){
            for(Deck dd: mRealm.where(Deck.class).findAll()){
                addStudyDeck(dd);
            }
        }
    }


    public void addStudyDeck(Deck d){
        StudyDeck sd = new StudyDeck(d.getName(), new RealmList<StudyCard>(), d);
        mRealm.beginTransaction();
        StudyDeck managedDeck = mRealm.copyToRealmOrUpdate(sd);
        for(Card c: d.getCards()){
            managedDeck.getStudyCards().add(new StudyCard(c, new LearningState()));
        }
        mRealm.commitTransaction();
    }


    public RealmResults<StudySession> getStudySessions(){
        return mRealm.where(StudySession.class).findAll();
    }

    public RealmResults<LearningState> getLearningStates(){
        return mRealm.where(LearningState.class).findAll();
    }

    public RealmResults<StudyCard> getStudyCards(){
        return  mRealm.where(StudyCard.class).findAll();
    }


    public void setAllLearningStatesToValues(RealmResults<LearningState> states, int tries, float evalue){
        mRealm.beginTransaction();
        for(LearningState s: states ){
            s.setEasiness(evalue);
            s.setTotalTries(tries);
        }
        mRealm.commitTransaction();
    }

    @After
    public  void closeRealm(){
        mRealm.close();
    }
}
