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


    @Test
    public void isHiriganaEqual_EqualTest(){
        Card c = new Card();
        c.setJapaneseHiragana("ひりがな");
        assertEquals(true, c.isHiraganaEqual("ひりがな"));
    }

    @Test
    public void isHiriganaEqual_NoEqualTest(){
        Card c = new Card();
        c.setJapaneseHiragana("ひりがな");
        assertEquals(false, c.isHiraganaEqual("からかな"));
    }
    @Test
    public void isHiriganaEqual_NoHiriganaSet(){
        Card c = new Card();
        assertEquals(false, c.isHiraganaEqual("ひりがな"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHiriganaEqual_NullParamater(){
        Card c = new Card();
        c.setJapaneseHiragana("ひりがな");
        c.isHiraganaEqual(null);
    }

    @Test
    public void isASynonym_NoSynonymsTest(){
        Card c = new Card();
        assertEquals(false, c.isASynonym("banana"));
    }

    @Test
    public void isASynonym_OneSynonymCorrect(){
        Card c = new Card();
        c.getSynonyms().add("banana");
        assertEquals(true, c.isASynonym("banana"));
    }

    @Test
    public void isASynonym_OneSynonymWrong(){
        Card c = new Card();
        c.getSynonyms().add("apple");
        assertEquals(false, c.isASynonym("banana"));
    }

    @Test
    public void isASynonym_MultipleSynonymCorrect(){
        Card c = new Card();
        c.getSynonyms().add("coffee");
        c.getSynonyms().add("apple");
        c.getSynonyms().add("banana");
        assertEquals(true, c.isASynonym("banana"));
    }

    @Test
    public void isASynonym_MultipleSynonymWrong(){
        Card c = new Card();
        c.getSynonyms().add("coffee");
        c.getSynonyms().add("apple");
        c.getSynonyms().add("banana");
        assertEquals(false, c.isASynonym("princess"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isASynonym_NullParamater(){
        Card c = new Card();
        c.isASynonym(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isMainLanguageEquals_NullParamater(){
        Card c = new Card();
        c.isMainLanguageEqual(null, false);
    }

    @Test
    public void isMainLanguageEquals_CorrectMainLanguage(){
        Card c = new Card();
        c.setMainLanguage("banana");
        assertEquals(true, c.isMainLanguageEqual("banana", true));
    }

    @Test
    public void isMainLanguageEquals_WrongMainLanguageButCorrectSynonym(){
        Card c = new Card();
        c.setMainLanguage("banana");
        c.getSynonyms().add("apple");
        assertEquals(true, c.isMainLanguageEqual("apple", true));
    }

    @Test
    public void isMainLanguageEquals_WrongMainLanguageButWrongSynonym(){
        Card c = new Card();
        c.setMainLanguage("banana");
        c.getSynonyms().add("apple");
        assertEquals(false, c.isMainLanguageEqual("princess", true));
    }

    @Test
    public void isMainLanguageEquals_WrongMainLanguageButCorrectSynonymButDontIncludeSynonymCheck(){
        Card c = new Card();
        c.setMainLanguage("banana");
        c.getSynonyms().add("apple");
        assertEquals(false, c.isMainLanguageEqual("apple", false));
    }






}
