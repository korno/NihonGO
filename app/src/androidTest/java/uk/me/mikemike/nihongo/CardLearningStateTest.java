package uk.me.mikemike.nihongo;

import org.junit.Test;

import java.util.Date;

import io.realm.RealmList;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.CardLearningState;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 11/21/17.
 */

public class CardLearningStateTest extends  BaseTest {

    @Test
    public void startLearningTest(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                                                0,0);
        s.startLearning(d);
        assertEquals(d, s.getNextDueData());
        assertEquals(s.getEasyness(), CardLearningState.STARTING_E_VALUE, 1e-15);
        assertEquals(0.0f,s.getInterval(), 1e-15 );
        assertEquals(0, s.getConsecutiveCorrectAnswers());
    }

    @Test(expected = IllegalArgumentException.class)
    public void performSRWithNullDateTest(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(5, null);
    }
}
