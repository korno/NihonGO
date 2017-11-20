/**
 * NihonGO!
 * <p>
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.me.mikemike.nihongo.data;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.Collections;
import io.realm.Realm;
import io.realm.RealmList;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.utils.StringUtils;

/**
 * @author mike
 * XmlImporter for importing cards and decks in xml format
 */
public class XmlImporter {

    /* definitions for the xml tags */
    public static String ROOT_TAG = "decks";
    public static String DECK_TAG = "deck";
    public static String CARD_TAG = "card";
    public static String SYNONYM_TAG = "synonym";
    public static String CARD_JAPANESEDISPLAY_TAG = "japaneseDisplay";
    public static String DECK_NAME_ATTR = "name";
    public static String DECK_DESCRIPTION_ATTR = "description";
    public static String DECK_FIXED_ORDER_ATTR = "fixedOrder";
    public static String DECK_AUTHOR_TAG = "author";
    public static String CARD_JAPANESE_ATTR = "japaneseKanji";
    public static String CARD_JAPANESE_KANA_ATTR = "japaneseHiragana";
    public static String CARD_MAIN_LANGUAGE_ATTR = "mainLanguage";
    public static String CARD_WORDTYPE_ATTR = "wordType";


    protected XmlPullParser mSource;
    protected Realm mRealm;
    protected final String mNameSpace = null;
    /**
     * The default description that is used if none exists in the xml
     */
    protected String mDefaultDeckDescription;
    /**
     * The default author if none is present in the xml
     */
    protected String mDefaultAuthor;

    /**
     * The minimum number of cards that must be present for a deck to be imported
     */
    protected int mMinimumCards;

    public XmlImporter(Realm target, XmlPullParser source, String defaultDeckDescription, String defaultAuthor, int minimumCards) {
        if(target == null) throw new IllegalArgumentException("the realm  target parameter must not be null");
        if(source == null) throw new IllegalArgumentException("the source xml pull parser paramerer must not be null");
        mRealm = target;
        mSource = source;
        mDefaultDeckDescription = defaultDeckDescription;
        mMinimumCards = minimumCards;
        mDefaultAuthor = defaultAuthor;
    }

    /**
     * Set the xml parser that the importer will use. This allows you you to import multiple
     * xml with the same parser
     * @param source The source that this importer will read from
     */
    public void setSource(XmlPullParser source) {
        if(source == null)throw new IllegalArgumentException("the source xml pull parser must not be null");
        mSource = source;
    }

    /**
     * Star the import process, this is a blocking method so should be done on a seperate thread if possible.
     */
    public void importData() {
        try {
            mRealm.beginTransaction();
            // this is required to get to the correct tag, not sure why?
            mSource.next();
            mSource.next();
            mSource.require(XmlPullParser.START_TAG, mNameSpace, ROOT_TAG);
            while (mSource.next() != XmlPullParser.END_TAG) {

                if (mSource.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = mSource.getName();
                if (name.equalsIgnoreCase(DECK_TAG)) {
                    Deck d = loadDeck();
                    if (d != null) {
                        mRealm.copyToRealmOrUpdate(d);
                    }
                } else {
                    ignore();
                }

            }
        } catch (XmlPullParserException e) {
            mRealm.cancelTransaction();
            return;
        } catch (IOException e) {
            mRealm.cancelTransaction();
            return;
        }

        mRealm.commitTransaction();
    }


    protected Deck loadDeck() throws IOException, XmlPullParserException {
        boolean isFixedOrder;
        mSource.require(XmlPullParser.START_TAG, mNameSpace, DECK_TAG);
        Deck d = new Deck();
        String data;
        // null name not allowed
        data = mSource.getAttributeValue(null, DECK_NAME_ATTR);
        if (StringUtils.isEmptyOrNull(data)) return null;
        d.setName(data);
        // null description is ok, we will add a "No description" field
        data = mSource.getAttributeValue(null, DECK_DESCRIPTION_ATTR);
        String s = mSource.getAttributeValue(null, DECK_FIXED_ORDER_ATTR);
        isFixedOrder = s == null ? false : s.equalsIgnoreCase("true");
        d.setDescription(data == null ? mDefaultDeckDescription : data);
        data = mSource.getAttributeValue(null, DECK_AUTHOR_TAG);
        d.setAuthor(data == null ? mDefaultAuthor : data);
        RealmList<Card> cards = new RealmList<>();
        d.setCards(new RealmList<Card>());
        while (mSource.next() != XmlPullParser.END_TAG) {

            if (mSource.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = mSource.getName();
            if (name.equalsIgnoreCase(CARD_TAG)) {
                Card c= loadCard();
                if(c != null){
                    cards.add(c);
                }
            } else {
                ignore();
            }

        }
        // are the enough cards?
        if (cards.size() < mMinimumCards) return null;
        if (!isFixedOrder) {
            Collections.shuffle(d.getCards());
        }
        d.setCards(cards);
        return d;
    }


    protected Card loadCard() throws IOException, XmlPullParserException {

        mSource.require(XmlPullParser.START_TAG, mNameSpace, CARD_TAG);
        Card c = new Card();

        String data;

        // the kana is required
        data = mSource.getAttributeValue(null, CARD_JAPANESE_KANA_ATTR);
        if(StringUtils.isEmptyOrNull(data)) return null;
        c.setJapaneseHiragana(data);

        // main language is required
        data = mSource.getAttributeValue(null, CARD_MAIN_LANGUAGE_ATTR);
        if(StringUtils.isEmptyOrNull(data)) return null;
        c.setMainLanguage(data);

        data = mSource.getAttributeValue(null, CARD_JAPANESE_ATTR);
        // if there is no kanji lets use the hiragana, this is useful when there are words
        // with no kanji
        c.setJapaneseKanji(StringUtils.isEmptyOrNull(data) ? c.getJapaneseHiragana() : data);

        c.setSynonyms(new RealmList<String>());

        // try and get the word type
        Card.CardType type = Card.CardType.Other;
        try {
            type = Card.CardType.valueOf(mSource.getAttributeValue(null, CARD_WORDTYPE_ATTR));
        } catch (Exception e) {
            type = Card.CardType.Other;
        }

        c.setCardType(type);

        // display japanese by default is the kanji japanese
        // this will be overwritten if there is a display japanese tag
        c.setJapaneseDisplay(c.getJapaneseKanji());

        while (mSource.next() != XmlPullParser.END_TAG) {
            if (mSource.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = mSource.getName();

            if (name.equals(SYNONYM_TAG)) {
                mSource.require(XmlPullParser.START_TAG, mNameSpace, SYNONYM_TAG);
                String text = readText(mSource);
                if(!StringUtils.isEmptyOrNull(text)){
                    c.getSynonyms().add(text);
                }
                mSource.require(XmlPullParser.END_TAG, mNameSpace, SYNONYM_TAG);
            } else if (name.equals(CARD_JAPANESEDISPLAY_TAG)) {
                mSource.require(XmlPullParser.START_TAG, mNameSpace, CARD_JAPANESEDISPLAY_TAG);
                String text = readText(mSource);
                if(!StringUtils.isEmptyOrNull(text)){
                    c.setJapaneseDisplay(text);
                }
                mSource.require(XmlPullParser.END_TAG, mNameSpace, CARD_JAPANESEDISPLAY_TAG);
            } else {
                ignore();
            }
        }

        return c;
    }


    // based on googles example
    protected void ignore() throws XmlPullParserException, IOException {
        if (mSource.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (mSource.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }

    // more google
    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result.trim();
    }


}
