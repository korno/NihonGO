package uk.me.mikemike.nihongo;

import org.junit.Test;
import java.util.Date;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.utils.DateUtils;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 12/7/17.
 */

public class StudySessionTest extends BaseTest {


    @Test
    public void createStudySession_ValidStudyDeckWithOustandingCards(){
        addDecks(1, 5, true);
        StudyDeck d = getStudyDecks().first();
        StudySession session = new StudySession(d, new Date());
        assertEquals(5, session.getTotalStudyCardsCount());
    }

    @Test
    public void doStudySession_ValidStudyWithOneOutstandingCardCorrectAnswerTest(){
        addDecks(1, 1, true);
        StudySession ss = new StudySession(getStudyDecks().first(),new Date());
        assertEquals(1, ss.getTotalStudyCardsCount());
        assertEquals(1, ss.getRemainingStudyCardsCount());
        assertEquals(0, ss.getCorrectCards().size());

        mRealm.beginTransaction();
        // answer correctly
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        mRealm.commitTransaction();

        assertEquals(true, ss.isFinished());
        assertEquals(1, ss.getCorrectCards().size());
        assertEquals(0, ss.getWrongCards().size());
        assertEquals(0, ss.getRemainingStudyCardsCount());

    }

    @Test
    public void doStudySession_ValidStudyWithOneOutstandingCardWrongAnswerTest(){
        addDecks(1, 1, true);
        StudySession ss = new StudySession(getStudyDecks().first(),new Date());
        assertEquals(1, ss.getTotalStudyCardsCount());
        assertEquals(1, ss.getRemainingStudyCardsCount());
        assertEquals(0, ss.getCorrectCards().size());

        mRealm.beginTransaction();
        // answer correctly
        ss.answerJapanese("wrong answer");
        mRealm.commitTransaction();

        assertEquals(false, ss.isFinished());
        assertEquals(0, ss.getCorrectCards().size());
        assertEquals(1, ss.getWrongCards().size());
        assertEquals(1, ss.getRemainingStudyCardsCount());

        mRealm.beginTransaction();
        // answer correctly
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        mRealm.commitTransaction();

        assertEquals(true, ss.isFinished());
        assertEquals(0, ss.getCorrectCards().size());
        assertEquals(1, ss.getWrongCards().size());
        assertEquals(0, ss.getRemainingStudyCardsCount());
    }

    @Test(expected = RuntimeException.class)
    public void doStudySession_AnswerQuestionOnFinishedTest(){
        addDecks(1, 1, true);
        StudySession ss = new StudySession(getStudyDecks().first(), new Date());
        mRealm.beginTransaction();
        ss.answerJapanese("wrong");
        mRealm.commitTransaction();
        ss.answerJapanese("bang");

    }

    @Test
    public void createStudySession_NoOutstandingReviewsTest(){
        addDecks(1, 0, true );
        StudySession ss = new StudySession(getStudyDecks().first(), DateUtils.addDaysToDate(new Date(), -20));
        assertEquals(true, ss.isFinished());
        assertEquals(0, ss.getTotalStudyCardsCount());

    }

    @Test
    public void answerCurrentQuestion_DoNotUpdateState(){
        addDecks(1, 1, true);
        StudySession ss = new StudySession(getStudyDecks().first(),new Date());
        assertEquals(ss.getCorrectCards().size(), 0);
        assertEquals(ss.getRemainingStudyCardsCount(), 1);
        assertEquals(ss.getWrongCards().size(), 0);
        mRealm.beginTransaction();
        // we can do this as many time as we want as the study session state will not be updated
        ss.answerJapanese("", false);
        ss.answerCurrentQuestion("", false);
        mRealm.commitTransaction();
        // no change to state
        assertEquals(ss.getCorrectCards().size(), 0);
        assertEquals(ss.getRemainingStudyCardsCount(), 1);
        assertEquals(ss.getWrongCards().size(), 0);
        assertEquals(ss.isFinished(), false);
    }

    @Test
    public void doStudySession_SRIsCalledCorrectlyTest(){
        addDecks(1, 1, true);
        StudySession ss = new StudySession(getStudyDecks().first(),new Date());
        StudyCard sc = ss.getCurrent();
        mRealm.beginTransaction();
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        mRealm.commitTransaction();
        assertEquals(1,sc.getLearningState().getReps());

    }

    @Test
    public void doStudySession_multipleQuestionsTest(){
        addDecks(1, 3, true);
        StudySession ss = new StudySession(getStudyDecks().first(), new Date());

        mRealm.beginTransaction();
        ss.answerJapanese("wrong");
        ss.answerJapanese("wrong");
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        ss.answerJapanese(ss.getCurrent().getSourceCard().getJapaneseHiragana());
        mRealm.commitTransaction();

        assertEquals(true, ss.isFinished());
        assertEquals(1, ss.getCorrectCards().size());
        assertEquals(2, ss.getWrongCards().size());
        assertEquals(0,ss.getRemainingStudyCardsCount());

    }


    @Test
    public void doStudySession_SaveAndResume(){
        addDecks(1, 2, true);
        StudySession ss =new StudySession(getStudyDecks().first(), new Date());
        mRealm.beginTransaction();
        ss.answerJapanese("wrong");
        mRealm.commitTransaction();

        // save the session
        mRealm.beginTransaction();
        mRealm.insert(ss);
        mRealm.commitTransaction();

        // get the session
        StudySession s2 = getStudySessions().first();



        assertEquals(false, s2.isFinished());
        assertEquals(1, s2.getWrongCards().size());
        assertEquals(0, s2.getCorrectCards().size());
        assertEquals(2, s2.getRemainingStudyCardsCount());
        assertEquals(2, s2.getTotalStudyCardsCount());

        // finish the test

        mRealm.beginTransaction();
        s2.answerJapanese(s2.getCurrent().getSourceCard().getJapaneseHiragana());
        mRealm.commitTransaction();

        assertEquals(false, s2.isFinished());
        assertEquals(1, s2.getWrongCards().size());
        assertEquals(1, s2.getCorrectCards().size());
        assertEquals(1, s2.getRemainingStudyCardsCount());
        assertEquals(2, s2.getTotalStudyCardsCount());


    }


    @Test(expected = RuntimeException.class)
    public void doStudySession_EmptyStudySession(){
        addDecks(1, 2, true);
        StudySession ss =new StudySession(getStudyDecks().first(), DateUtils.addDaysToDate(new Date(), -5));
        assertEquals(true, ss.isFinished());
        mRealm.beginTransaction();
        ss.answerJapanese("aaa");
        mRealm.commitTransaction();
    }


    @Test(expected = IllegalArgumentException.class)
    public void createStudySession_NullDate(){
        addDecks(1, 2, true);
        StudySession s = new StudySession(getStudyDecks().first(), null);
    }


    @Test(expected = IllegalArgumentException.class)
    public  void createStudySession_NullStudyDeck(){
        StudySession s = new StudySession(null, new Date());
    }


    @Test
    public void usingAnswerCurrentQuestion_WrongAnswerTest(){
        addDecks(1, 2, true);
        StudySession s = new StudySession(getStudyDecks().first(), new Date());
        // answer the wrong question
        mRealm.beginTransaction();
        if(s.isCurrentQuestionJapaneseAnswer()){
            assertEquals(false, s.answerCurrentQuestion(s.getCurrent().getSourceCard().getMainLanguage()));
        }
        else{
            assertEquals(false, s.answerCurrentQuestion(s.getCurrent().getSourceCard().getJapaneseHiragana()));
        }
        mRealm.commitTransaction();

    }

    @Test
    public void usingAnswerCurrentQuestion_CorrectAnswerTest(){
        addDecks(1, 2, true);
        StudySession s = new StudySession(getStudyDecks().first(), new Date());
        mRealm.beginTransaction();
        if(s.isCurrentQuestionJapaneseAnswer()){
            assertEquals(true, s.answerCurrentQuestion(s.getCurrent().getSourceCard().getJapaneseHiragana()));
        }
        else
        {
            assertEquals(true, s.answerCurrentQuestion(s.getCurrent().getSourceCard().getMainLanguage()));
        }
        mRealm.commitTransaction();
    }
}
