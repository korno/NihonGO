package uk.me.mikemike.nihongo;

import org.junit.Test;

import java.util.Date;

import io.realm.RealmResults;
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

}
