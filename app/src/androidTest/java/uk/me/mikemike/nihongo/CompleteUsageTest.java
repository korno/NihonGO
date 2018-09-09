package uk.me.mikemike.nihongo;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.Date;

import io.realm.RealmResults;
import uk.me.mikemike.nihongo.data.NihongoRepository;
import uk.me.mikemike.nihongo.data.XmlImporter;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


/**
 * Created by mike on 12/4/17.
 */

public class CompleteUsageTest extends BaseTest {

    @Test
    public void doOneDeckStudy(){

        // we have one deck with two cards ready
        addDecks(1, 2, false);

        NihongoRepository repository = new NihongoRepository(mRealm);
        Date studyDate = new Date();

        // firstly there should be no study decks waiting
        RealmResults<StudyDeck> outstandingDecks = repository.getStudyDecksWithReviewsWaiting(studyDate);
        assertEquals(0, outstandingDecks.size());


        // lets start studying, the repository should return one deck
        RealmResults<Deck> allDecks = repository.getAllDecksNotBeingStudied();
        assertEquals(1, allDecks.size());

        StudyDeck d = repository.startStudying(allDecks.first(),studyDate,5,1);

        // the results should auto update but lets also do a manual check
        assertEquals(1, outstandingDecks.size());
        assertEquals(1, repository.getStudyDecksWithReviewsWaiting(studyDate).size());
        assertEquals(2, getStudyCards().size());
        assertEquals(0, allDecks.size());
        // study the card
        repository.performSR(d.getCardsWithNextReviewDateOlderThan(studyDate).first().getLearningState(),
                                4, studyDate);
        // still one card left so shouldnt change
        assertEquals(1, outstandingDecks.size());

        // study the next card
        // study the card
        repository.performSR(d.getCardsWithNextReviewDateOlderThan(studyDate).first().getLearningState(),
                4, studyDate);

        assertEquals(0, outstandingDecks.size());

    }


    @Test
    public void importThenStudyTest(){

        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testxml),
                "", "", 0);
        importer.importData();
        assertEquals(1, getDecks().size());

        NihongoRepository repository = new NihongoRepository(mRealm);
        RealmResults<StudyDeck> currentlyStudying = repository.getAllStudyDecks();
        assertEquals(0, currentlyStudying.size());

        RealmResults<Deck> nonStudyingDecks = repository.getAllDecksNotBeingStudied();
        assertEquals(1, nonStudyingDecks.size());

        repository.startStudying(nonStudyingDecks.first(), new Date(), 10, 0);
        assertEquals(0, nonStudyingDecks.size());
        assertEquals(1,currentlyStudying.size());

        RealmResults<StudyDeck> outstandingStudyDecks =  repository.getStudyDecksWithReviewsWaiting(new Date());
        assertEquals(1, outstandingStudyDecks.size());


        repository.performSR(outstandingStudyDecks.first().getCardsWithNextReviewDateOlderThan(new Date()).first().getLearningState(), 4, new Date());
        assertEquals(0, outstandingStudyDecks.size());

    }


    @Test
    public void importCreateStudyDoStudy(){

        // import the xml data
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testxml),
                "", "", 0);
        importer.importData();

        //CHECK 1 - there should be one deck in the realm
        assertEquals(1, getDecks().size());

        // create a repository
        NihongoRepository repository = new NihongoRepository(mRealm);

        // get the first deck not being studied
        Deck deckToStudy = repository.getAllDecksNotBeingStudied().first();

        //CHECK 2 - we got the deck and getAllDecksShould return one result
        assertNotNull(deckToStudy);
        assertEquals(1, repository.getAllDecksNotBeingStudied().size());


        // start studying
        StudyDeck studyDeck = repository.startStudying(deckToStudy, new Date(), 10, 0);

        //CHECK 3 - the study has been created in realm and getAllDecksNotBeingStudied should now be 0
        assertNotNull(studyDeck);
        assertEquals(0, repository.getAllDecksNotBeingStudied().size());
        assertEquals(1, getStudyDecks().size());
        assertEquals(1, repository.getAllStudyDecks().size());

        // instead of using the returned object from startStudying, lets use the method to get
        // study decks with outstanding reviews
        RealmResults<StudyDeck> studyDecksWithReviews = repository.getStudyDecksWithReviewsWaiting(new Date());

        //CHECK 4 - the repository is returning the correct values for study decks with reviews waiting
        assertEquals(1, studyDecksWithReviews.size());

        // create a study session
        StudySession s = repository.createStudySession(studyDecksWithReviews.first(), new Date());
        // only one card to study
        repository.answerStudySessionJapaneseAnswer(s, s.getCurrent().getSourceCard().getJapaneseHiragana(), true);


        // CHECK 5 - the test has finished with one correct answer
        assertEquals(true, s.isFinished());
        assertEquals(1, s.getCorrectCards().size());
        assertEquals(0, s.getRemainingStudyCardsCount());
        assertEquals(0, s.getWrongCards().size());


        // CHECK 6 - now we have studied, there should be no outstanding studydecks
        assertEquals(0, repository.getStudyDecksWithReviewsWaiting(new Date()).size());
        // (realm should have auto updated this)
        assertEquals(0, studyDecksWithReviews.size());




    }
}
