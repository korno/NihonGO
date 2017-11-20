package uk.me.mikemike.nihongo;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import io.realm.RealmList;
import uk.me.mikemike.nihongo.data.XmlImporter;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;

import static junit.framework.Assert.assertEquals;

/**
 * Created by mike on 11/16/17.
 */

public class XmlImporterTest extends  BaseTest {

    @Test
    public void oneDeckOneCardTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testxml),
                                                    "", "", 0);
        importer.importData();
        assertHasNumberOfDecks(1);

        // check that the deck fields are correct
        Deck deck = getDecks().first();
        Deck expected = new Deck("test1", "test1description", "test1author", new RealmList<Card>());
        assertDeckFieldsAreSame(expected, deck);

        RealmList<String> s = new RealmList<>();
        s.add("card1synonym1");
        Card expectedCard = new Card("card1mainlanguage", "card1japanesehiragana",
                            "card1japanesekanji", "card1japanesedisplay", s, Card.CardType.Noun);

        assertCardFieldsAreSame(expectedCard, deck.getCards().first());

        deleteData();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSourceTest() {
        XmlImporter importer = new XmlImporter(mRealm, null, "", "",0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRealmTest() {
        XmlImporter importer = new XmlImporter(null,InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testxml), "", "", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSourceSetSourceTest() {
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testxml), "", "",0);
        importer.setSource(null);
    }

    @Test
    public void noDecksTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testnodecks),
                "", "",  0);
        importer.importData();
        assertHasNumberOfDecks(0);
        deleteData();
    }

    @Test
    public void invalidDeckNoNameTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testinvaliddeckname),
                "", "", 0);
        importer.importData();
        assertHasNumberOfDecks(0);
        deleteData();
    }

    @Test
    public void defaultDescriptionTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testnodescription),
                "defaultdescription", "", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        assertEquals("defaultdescription",getDecks().first().getDescription());
        deleteData();
    }

    @Test
    public void defaultAuthorTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testnodescription),
                "", "defaultauthor", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        assertEquals("defaultauthor",getDecks().first().getAuthor());
        deleteData();
    }


    @Test
    public void insufficientCardsTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testinsufficientcards),
                "", "defaultauthor", 2);
        importer.importData();
        assertHasNumberOfDecks(0);
        deleteData();
    }

    @Test
    public void invalidCards(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testinvalidcard),
                "", "defaultauthor", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        assertHasNumberOfCards(getDecks().first(), 0);
        deleteData();
    }

    @Test
    public void emptySynonymTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testemptysynonym),
                "", "defaultauthor", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        Deck d = getDecks().first();
        assertHasNumberOfCards(d, 1);
        assertEquals(1, d.getCards().get(0).getSynonyms().size());
        deleteData();
    }


    @Test
    public void emptyJapaneseDisplayTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testemptyjapanesedisplay),
                "", "defaultauthor", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        Deck d = getDecks().first();
        assertHasNumberOfCards(d, 1);
        assertEquals("card1japanesekanji", d.getCards().get(0).getJapaneseDisplay());
        deleteData();
    }

    @Test
    public void invalidWordTypeTest(){
        XmlImporter importer = new XmlImporter(mRealm, InstrumentationRegistry.getTargetContext().getResources().getXml(R.xml.testinvalidwordtype),
                "", "defaultauthor", 0);
        importer.importData();
        assertHasNumberOfDecks(1);
        Deck d = getDecks().first();
        assertHasNumberOfCards(d, 2);
        assertIsCardType(d.getCards().get(0), Card.CardType.Other);
        assertIsCardType(d.getCards().get(1), Card.CardType.Other);
        deleteData();
    }

}
