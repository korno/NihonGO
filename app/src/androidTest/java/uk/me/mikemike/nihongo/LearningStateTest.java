package uk.me.mikemike.nihongo;

import org.junit.Test;

import java.util.Date;

import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.utils.DateUtils;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 11/21/17.
 */

public class LearningStateTest extends  BaseTest {

    @Test
    public void startLearningTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                                                0,0);
        s.startLearning(d);
        assertEquals(d, s.getNextDueDate());
        assertEquals(s.getEasyness(), LearningState.STARTING_E_VALUE, 1e-15);
        assertEquals(0.0f,s.getInterval(), 1e-15 );
        assertEquals(0, s.getReps());
        assertEquals(0, s.getTotalTries());
        assertEquals(0, s.getStudyLevel());
    }

    @Test(expected = IllegalArgumentException.class)
    public void performSRWithNullDateTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(5, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void startLearningWithNullDateTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
       s.startLearning(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void performSRWithInvalidAnswerGradeLow(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(-1, new Date());
    }


    @Test(expected = IllegalArgumentException.class)
    public void performSRWithInvalidAnswerGradeHigh(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
        s.performSR(6, new Date());
    }

    @Test
    public  void performFirstTwoReviewsTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
        assertEquals(0f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   1f, s.getInterval(), 1e-15);
        s.performSR(5, d);
        assertEquals(   6f, s.getInterval(), 1e-15);
        assertEquals(2, s.getTotalTries());
        assertEquals(1, s.getStudyLevel());
    }

    @Test
    public void performFirstTwoReviewsThenFailIntervalCheckTest() {
        Date d = new Date();
        LearningState s = new LearningState(d, d, LearningState.STARTING_E_VALUE,
                0,0);
        assertEquals(0f, s.getInterval(), 1e-15);
        assertEquals(d, s.getNextDueDate());

        s.performSR(5, d);
        assertEquals(   1f, s.getInterval(), 1e-15);
        assertEquals(DateUtils.addDaysToDate(d, 1), s.getNextDueDate());

        s.performSR(5, d);
        assertEquals(DateUtils.addDaysToDate(d, 6), s.getNextDueDate());
        assertEquals(   6f, s.getInterval(), 1e-15);

        // fail
        s.performSR(1,d);
        assertEquals(DateUtils.addDaysToDate(d, 1), s.getNextDueDate());
        assertEquals(   1f, s.getInterval(), 1e-15);
        assertEquals(3, s.getTotalTries());


        assertEquals(1, s.getStudyLevel());

    }


    @Test(expected =  IllegalArgumentException.class)
    public void invalidEasinessValueTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d,  -1f, 0, 0);
    }

    @Test(expected =  IllegalArgumentException.class)
    public void invalidIntervalValueTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d,  2.3f, 0, -2);
    }

    @Test(expected =  IllegalArgumentException.class)
    public void invalidConsecutiveValueTest(){
        Date d = new Date();
        LearningState s = new LearningState(d, d,  2.3f, -1, 0);
    }

    @Test
    public void minimumEasinessValueTest(){
        Date d = new Date();
        // starting at the minimum e value
        LearningState s = new LearningState(d, d, LearningState.MINIMUM_E_VALUE, 5, 5 * LearningState.MINIMUM_E_VALUE);
        // get it right a few times
        s.performSR(3, d);
        s.performSR(3, d);
        s.performSR(3, d);
        assertMoreThan(LearningState.MINIMUM_E_VALUE, s.getEasyness());
    }

    @Test
    public void getStudyLevelTest_AllMastered(){
        addDecks(2, 2, true);
        setAllLearningStatesToValues(getLearningStates(), 5, LearningState.MASTERED_E_VALUE);

        for(LearningState s: getLearningStates()){
            assertEquals(2, s.getStudyLevel());
        }

    }

    @Test
    public void getStudyLevelTest_AllNew(){
        addDecks(2, 2, true);
        //setAllLearningStatesToValues(getLearningStates(), 5, 2.3f);

        for(LearningState s: getLearningStates()){
            assertEquals(0, s.getStudyLevel());
        }
    }



}
