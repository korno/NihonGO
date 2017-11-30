package uk.me.mikemike.nihongo;

import org.junit.Test;

import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyCard;

/**
 * Created by mike on 11/27/17.
 */

public class StudyCardTest extends BaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullLearningStateTest(){
        Card c = getDummyNonRealmCard();
        StudyCard sc = new StudyCard(c, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSourceCardTest(){
        LearningState s = getDummyNonRealmLearningState();
        StudyCard sc = new StudyCard(null, s);
    }

}
