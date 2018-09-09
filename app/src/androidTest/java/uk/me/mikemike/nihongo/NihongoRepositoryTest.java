package uk.me.mikemike.nihongo;

import org.junit.Test;

import java.util.Date;


import io.realm.RealmResults;
import uk.me.mikemike.nihongo.data.NihongoRepository;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.utils.DateUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by mike on 11/29/17.
 */

public class NihongoRepositoryTest extends BaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullRealmTest(){
        NihongoRepository repos = new NihongoRepository(null);
    }

    @Test
    public void getAllDecks_NoDecksTest(){
        NihongoRepository repos = new NihongoRepository(mRealm);
        assertEquals(0, repos.getAllDecks().size());
        assertEquals(0, repos.getTotalNumberOfDecks());
    }

    @Test
    public void getAllDecks_DecksPresentTest(){
        addDecks(5, 5, false);
        NihongoRepository repos = new NihongoRepository(mRealm);
        assertEquals(5, repos.getAllDecks().size());
        assertEquals(5, repos.getTotalNumberOfDecks());
    }

    @Test
    public void getAllStudyDecks_NoDecksTest(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertEquals(0, repository.getAllStudyDecks().size());
        assertEquals(0, repository.getTotalNumberOfStudyDecks());
    }

    @Test
    public void getAllStudyDecks_DecksPresentTest(){
        addDecks(5, 5, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertEquals(5,repository.getTotalNumberOfStudyDecks());
        assertEquals(5, repository.getAllStudyDecks().size());
    }


    @Test(expected =  IllegalArgumentException.class)
    public void getStudyDeck_ByIDNullIDTest(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.getStudyDeckByID(null);
    }

    @Test
    public void getStudyDeck_ByIDInvalidIDTest(){
        addDecks(3, 3, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        StudyDeck d = repository.getStudyDeckByID("invalidID");
        assertNull(d);
    }

    @Test
    public void getStudyDeck_ValidIDTest(){
        addDecks(3, 3, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        StudyDeck d = getStudyDecks().first();
        StudyDeck d2 = repository.getStudyDeckByID(d.getStudyDeckID());
        assertNotNull(d2);
        assertStudyDeckFieldsAreSame(d, d2, true);
    }


    @Test
    public void getWaitingStudyDecksTest(){
        // create 5 new decks all with study decks
        addDecks(5, 1, true);

        // make one of those study decks have its study cards date set twenty days in the future
        // this means only 4 studye decks should be returned by the query
        mRealm.beginTransaction();
        StudyDeck d = getStudyDecks().first();
        d.getStudyCards().first().getLearningState().startLearning(DateUtils.addDaysToDate(new Date(), 20));
        mRealm.commitTransaction();

        // the actual test
        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<StudyDeck> studyDecks = repository.getStudyDecksWithReviewsWaiting(new Date());
        assertEquals(4, studyDecks.size());

    }


    @Test
    public void getWaitingStudyDecks_OnlySomeSudyCardsWaitingTest(){
        addDecks(1, 2, true);
        // set one of the card to not be due for study
        mRealm.beginTransaction();
        StudyDeck d = getStudyDecks().first();
        d.getStudyCards().first().getLearningState().startLearning(DateUtils.addDaysToDate(new Date(), 20));
        mRealm.commitTransaction();

        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<StudyDeck> r = repository.getStudyDecksWithReviewsWaiting( new Date());
        assertEquals(1, r.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWaitingStudyDecks_InvalidDateTest(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.getStudyDecksWithReviewsWaiting(null);
    }


    @Test
    public void getWaitingStudyDecks_NoDecksTest(){
        addDecks(2, 1, true);
        // make all of the study cards be set to the future
        mRealm.beginTransaction();
        Date future = DateUtils.addDaysToDate(new Date(), 20);
        for(StudyDeck d: getStudyDecks()){
            d.getStudyCards().first().getLearningState().startLearning(future);
        }
        mRealm.commitTransaction();

        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<StudyDeck> r = repository.getStudyDecksWithReviewsWaiting(new Date());
        assertEquals(0, r.size());
    }

    @Test
    public void getWaitingStudyDecks_CheckChangeAfterStudyTest(){

        addDecks(1, 1, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<StudyDeck> decks = repository.getStudyDecksWithReviewsWaiting(new Date());
        assertEquals(1, decks.size());

        mRealm.beginTransaction();
        // ok study the single card in the studydeck
        decks.first().getStudyCards().first().getLearningState().performSR(4, new Date());
        mRealm.commitTransaction();

        // realm results are auto updating so this should update without having to re run the query
        assertEquals(0, decks.size());

    }


    @Test
    public void startStudying_ValidDeckTest(){
        addDecks(1, 2, false);
        Deck d = getDecks().first();
        NihongoRepository repository =  new NihongoRepository(mRealm);
        Date date = new Date();
        StudyDeck newStudyDeck = repository.startStudying(d, date, 5, 1 );
        RealmResults<StudyDeck> sdecks = getStudyDecks();
        assertEquals(1, sdecks.size());
        assertStudyDeckFieldsAreSame(sdecks.first(), newStudyDeck, true);
        assertEquals(2, sdecks.first().getStudyCards().size());
        assertEquals(2, getLearningStates().size());
        assertEquals(date, sdecks.first().getStudyCards().first().getLearningState().getNextDueDate());
        assertEquals(date, sdecks.first().getStudyCards().get(1).getLearningState().getNextDueDate());
    }

    @Test(expected =  IllegalArgumentException.class)
    public void startStudying_InvalidMaxCardsPerDayValueTest(){
        addDecks(1, 2, false);
        Deck d = getDecks().first();
        NihongoRepository repository =  new NihongoRepository(mRealm);
        Date date = new Date();
        StudyDeck newStudyDeck = repository.startStudying(d, date, 0, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void startStudying_InvalidIntervalBetweenMaxCardsValueTest(){
        addDecks(1, 2, false);
        Deck d = getDecks().first();
        NihongoRepository repository =  new NihongoRepository(mRealm);
        Date date = new Date();
        StudyDeck newStudyDeck = repository.startStudying(d, date, 5, -1);
    }

    @Test
    public void startStudying_ValidDeckMultipleDays(){
        addDecks(1, 6, false);
        Deck d = getDecks().first();

        // start studying and split the new cards into 2 cards per day over three days (max=2, interval=1)
        NihongoRepository repository = new NihongoRepository(mRealm);
        Date startDate = new Date();
        StudyDeck sd = repository.startStudying(d, startDate,2,1);

        // check that the study state exists
        RealmResults<StudyDeck> studyDecks = getStudyDecks();
        assertEquals(1, studyDecks.size());

        // assert that the study card exsists
        assertEquals(6, getStudyCards().size());

        // assert the study deck returned by realm and our method match
        assertStudyDeckFieldsAreSame(studyDecks.first(), sd, true);

        // assert that there are two study cards that match the current date
        assertEquals(2, studyDecks.first().getStudyCards().where().equalTo("mLearningState.mNextDueDate", startDate).findAll().size()
                            );

        // assert that there two study cards on the next day
        Date startDatePlus1Day = DateUtils.addDaysToDate(startDate, 1);
        assertEquals(2, studyDecks.first().getStudyCards().where().equalTo("mLearningState.mNextDueDate", startDatePlus1Day).findAll().size()
        );

        // assert that there two study cards on the third day
        Date startDatePlus2Days = DateUtils.addDaysToDate(startDate, 2);
        assertEquals(2, studyDecks.first().getStudyCards().where().equalTo("mLearningState.mNextDueDate", startDatePlus2Days).findAll().size()
        );
    }


    /* getAllDecksNotBeingStudied Tests */
    @Test
    public void getAllDecksNotBeingStudied_AllBeingStudiedTest(){
        addDecks(4, 4, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertEquals(0, repository.getAllDecksNotBeingStudied().size());
    }

    @Test
    public void getAllDecksNotBeingStudied_NoDecksBeingStudiedTest(){
        addDecks(4, 4, false);
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertEquals(4, repository.getAllDecksNotBeingStudied().size());
    }

    @Test
    public void getAllDecksNotBeingStudied_SomeDecksBeingStudiedTest(){
        addDecks(4, 4,false);
        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<Deck> res = repository.getAllDecksNotBeingStudied();
        assertEquals(4, res.size());
        addStudyDeck(getDecks().first());
        assertEquals(3, res.size() );

    }

    @Test
    public void deleteEverythingTest(){
        addDecks(1, 1, true);
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.deleteEverything();
        assertEquals(0, getDecks().size());
        assertEquals(0, getStudyCards().size());
        assertEquals(0, getStudyDecks().size());
        assertEquals(0, getStudySessions().size());
        assertEquals(0,getLearningStates().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeckByID_nullIDTest(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.getDeckByID(null);
    }

    @Test
    public void getDeckByID_invalidIDTest(){
        addDecks(1, 1, false);
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertNull(repository.getDeckByID("badid"));
    }

    @Test
    public void getDeckByID_validIDTest(){
        addDecks(1, 1, false);
        NihongoRepository repository = new NihongoRepository(mRealm);
        assertNotNull(repository.getDeckByID(getDecks().first().getDeckID()));
    }


    @Test
    public void getStudySessionByID_validIDTest(){
        addDecks(1, 1, true);
        StudyDeck d = getStudyDecks().first();
        NihongoRepository  r = new NihongoRepository(mRealm);
        StudySession s = r.createStudySession(d, new Date());
        String id = s.getStudySessionID();

        assertEquals(r.getStudySessionByID(id), s);
    }

    @Test
    public void getStudySessionByID_nullIDTest(){
        addDecks(1, 1, true);
        StudyDeck d = getStudyDecks().first();
        NihongoRepository  r = new NihongoRepository(mRealm);
        StudySession s = r.createStudySession(d, new Date());
        assertNull(r.getStudySessionByID("banana"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void answerCurrentStudySessionQuestion_nullSessionTest(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.answerStudySessionCurrentQuestion("", null, true);
    }

    @Test(expected =  IllegalArgumentException.class)
    public void stopStudyingNullStudyDeck(){
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.stopStudying(null);
    }

    @Test
    public void stopStudyingValidStudyDeck(){
        addDecks(2, 2, true);
        StudyDeck d = getStudyDecks().first();
        String id = d.getStudyDeckID();
        NihongoRepository repository = new NihongoRepository(mRealm);
        repository.stopStudying(d);
        assertEquals(1, getStudyDecks().size());
        assertEquals(2, getStudyCards().size());
        assertEquals(2, getLearningStates().size());
        assertEquals(0, getStudyDecks().where().equalTo("mStudyDeckID", id).findAll().size());

    }

}
