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
        assertEquals(0, s.getReps());
    }

    @Test(expected = IllegalArgumentException.class)
    public void performSRWithNullDateTest(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(5, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void startLearningWithNullDateTest(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
       s.startLearning(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void performSRWithInvalidAnswerGradeLow(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(-1, new Date());
    }


    @Test(expected = IllegalArgumentException.class)
    public void performSRWithInvalidAnswerGradeHigh(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(6, new Date());
    }

    @Test
    public  void performFirstTwoReviewsTest(){
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        assertEquals(0f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   1f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   6f, s.getInterval(), 1e-15);
    }

    @Test
    public  void performFirstTwoReviewsTestThenFail() {
        Date d = new Date();
        CardLearningState s = new CardLearningState(getDummyNonRealmCard(),d,CardLearningState.STARTING_E_VALUE,
                0,0);
        assertEquals(0f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   1f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   6f, s.getInterval(), 1e-15);

        // fail
        s.performSR(1,d);
        assertEquals(   1f, s.getInterval(), 1e-15);


    }


}
