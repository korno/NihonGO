package uk.me.mikemike.nihongo;

import org.junit.Test;

import java.util.Date;

import io.realm.RealmResults;
import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.utils.DateUtils;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 12/4/17.
 */

public class StudyDeckTest extends BaseTest {

    @Test
    public void getOutStandingStudyCardsNoCardsTest(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        RealmResults<StudyCard> res = d.getCardsWithNextReviewDateOlderThan(DateUtils.addDaysToDate(new Date(), -20));
        assertEquals(0, res.size());
    }

    @Test
    public void getOutstandingStudyCardsYesCardsTest(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        RealmResults<StudyCard> res = d.getCardsWithNextReviewDateOlderThan(DateUtils.addDaysToDate(new Date(), 1));
        assertEquals(2, res.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOutstandingStudyCardsInvalidDate(){
        addDecks(1, 1, true);
        StudyDeck d = getStudyDecks().first();
        d.getCardsWithNextReviewDateOlderThan(null);
    }

    @Test
    public void hasReviewsWaitingNoReviewsTest(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(false, d.hasReviewsWaiting(DateUtils.addDaysToDate(new Date(), -20)));
    }

    @Test
    public void hasReviewsWaitingYesReviews(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(true, d.hasReviewsWaiting(DateUtils.addDaysToDate(new Date(), 1)));
    }

    @Test
    public void getNumberOfReviewsWaitingNoReviews(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(0, d.howManyReviewsWaiting(DateUtils.addDaysToDate(new Date(), -20)));
    }

    @Test
    public void getNumberOfReviewsWaiting2Reviews(){
        addDecks(1, 2,true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(2, d.howManyReviewsWaiting(DateUtils.addDaysToDate(new Date(), 1)));
    }


    @Test
    public void getAllNewCardsHasNewCards(){
        addDecks(1, 2, true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(2, d.getAllNewCards().size());
        assertEquals(2, d.howManyNewCards());
    }

    @Test
    public void getAllNewCardsHasSomeNewCards(){
        addDecks(1, 2, true);
        StudyDeck d = getStudyDecks().first();
        mRealm.beginTransaction();
        d.getStudyCards().first().getLearningState().performSR(2, new Date());
        mRealm.commitTransaction();
        assertEquals(1,d.getAllNewCards().size() );
        assertEquals(1, d.howManyNewCards());
    }

    @Test
    public void getAllNewCardsHasNoNewCards(){
        addDecks(1, 2, true);
        StudyDeck d = getStudyDecks().first();
        mRealm.beginTransaction();
        d.getStudyCards().get(0).getLearningState().performSR(2, new Date());
        d.getStudyCards().get(1).getLearningState().performSR(4, new Date());
        mRealm.commitTransaction();
        assertEquals(0, d.getAllNewCards().size());
        assertEquals(0, d.howManyNewCards());

    }

    @Test
    public void getMasteredCards_AllMastered(){
        addDecks(2, 2, true);
        setAllLearningStatesToValues(getLearningStates(), 5, LearningState.MASTERED_E_VALUE);

        for(StudyDeck d: getStudyDecks()){
            assertEquals(2, d.getAllMasteredCards().size());
            assertEquals(2, d.howManyMasteredCards());
        }

    }

    @Test
    public void getMasteredCards_NoneMastered(){
        addDecks(1, 2, true);
        StudyDeck d = getStudyDecks().first();
        assertEquals(0, d.getAllMasteredCards().size());
        assertEquals(0, d.howManyMasteredCards());
    }

}
