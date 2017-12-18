package uk.me.mikemike.nihongo;

import org.junit.Test;

import uk.me.mikemike.nihongo.model.Card;

import static junit.framework.Assert.assertEquals;

/**
 * Tests all of the non realm functionality of the card class
 */
public class CardTest extends BaseTest {

    @Test
    public void testEnumConversion(){
        Card c = new Card();
        // nothing has been set so the card should be "other"
        assertEquals(Card.CardType.Other, c.getCardType());
        // set to another type, internally the enum is being converted to a string
        c.setCardType(Card.CardType.Phrase);
        assertEquals(Card.CardType.Phrase, c.getCardType());
    }


    @Test
    public void getJapaneseDisplayIfPresentKanjiIfNot_NoDisplayJapaneseTest(){
        Card c = new Card();
        c.setJapaneseKanji("kanji");
        c.setJapaneseDisplay(null);
        assertEquals("kanji", c.getJapaneseDisplayIfPresentKanjiIfNot());
    }


    @Test
    public void getJapaneseDisplayIfPresentKanjiIfNot_YesDisplayJapaneseTest(){
        Card c = new Card();
        c.setJapaneseKanji("kanji");
        c.setJapaneseDisplay("display");
        assertEquals("display", c.getJapaneseDisplayIfPresentKanjiIfNot());
    }

    @Test
    public void createSynonymsString_NoSynonymsTest(){
        addDecks(1, 1, false);
        Card c = getDecks().first().getCards().first();
        assertEquals("", c.createSynonymsString(","));
    }

    @Test
    public void createSynonymsString_OneSynonymTest(){
        addDecks(1, 1, false);
        Card c = getDecks().first().getCards().first();
        mRealm.beginTransaction();
        c.getSynonyms().add("one");
        mRealm.commitTransaction();
        assertEquals("one", c.createSynonymsString(","));
    }

    @Test
    public void createSynonymsString_MultipleSynonymTest(){
        addDecks(1, 1, false);
        Card c = getDecks().first().getCards().first();
        mRealm.beginTransaction();
        c.getSynonyms().add("one");
        c.getSynonyms().add("two");
        c.getSynonyms().add("three");
        mRealm.commitTransaction();
        // not sure how realm will actually handle insert order?
        assertEquals("one,two,three", c.createSynonymsString(","));
    }


}
