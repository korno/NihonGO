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
}
