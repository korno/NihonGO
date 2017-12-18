/**
 * NihonGO!
 *
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
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
package uk.me.mikemike.nihongo.model;


import java.util.UUID;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import uk.me.mikemike.nihongo.utils.StringUtils;

/**
 * @author mike
 * Represents a single vocabulary item also known as a flash card
 */
public class Card extends RealmObject {

    public enum CardType {
        Other,
        NounDo,
        Noun,
        VerbRU,
        VerbU,
        VerbIrregular,
        Phrase,
        Adjective,
        Adverb
    }

    @PrimaryKey
    @Required
    protected String mCardID = UUID.randomUUID().toString();
    protected String mMainLanguage;
    protected String mJapaneseHiragana;
    protected String mJapaneseKanji;
    protected String mJapaneseDisplay;
    protected RealmList<String> mSynonyms;
    protected String mCardType;

    public String getCardID(){return mCardID;}
    public String getJapaneseHiragana(){return mJapaneseHiragana;}
    public String getMainLanguage(){return mMainLanguage;}
    public String getJapaneseKanji(){return mJapaneseKanji;}
    public String getJapaneseDisplay(){return mJapaneseDisplay;}
    public RealmList<String> getSynonyms(){return mSynonyms;}
    /*
    Realm does not allow us to store enums in the database so we silently convert the
    enum into a string representation.
     */
    public CardType getCardType(){
        CardType type;
        try{
            type = CardType.valueOf(mCardType);
        }
        catch(Exception e){
            type = CardType.Other;
        }
        return type;
    }

    public void setJapaneseHiragana(String value){mJapaneseHiragana=value;}
    public void setMainLanguage(String value){mMainLanguage=value;}
    public void setJapaneseKanji(String value){mJapaneseKanji=value;}
    public void setJapaneseDisplay(String value){mJapaneseDisplay=value;}
    public void setSynonyms(RealmList<String> value){mSynonyms =value;}

    /*
    As with the getCardType() method, realm does not allow us to store
    enums in the database so lets silently convert it to a string for storage
     */
    public void setCardType(CardType value){
        mCardType = value.name();
    }


    public Card(String mainLanguage, String japaneseHiragana, String japaneseKanji,
                String japaneseDisplay, RealmList<String> synonyms, CardType cardType) {
        mMainLanguage = mainLanguage;
        mJapaneseHiragana = japaneseHiragana;
        mJapaneseKanji = japaneseKanji;
        mJapaneseDisplay = japaneseDisplay;
        mSynonyms = synonyms;
        setCardType(cardType);
    }

    /*
   Required by realm
    */
    public Card(){

    }

    /**
     * Returns Japanese display if present otherwise the normal Japanese.
     * @return
     */
    public String getJapaneseDisplayIfPresentKanjiIfNot(){
        return StringUtils.isEmptyOrNull(mJapaneseDisplay) ? mJapaneseKanji : mJapaneseDisplay;
    }


    public boolean hasSynonyms(){
       return mSynonyms.size() > 0;
    }

    public String createSynonymsString(String seperator){
        if(seperator == null) throw new IllegalArgumentException("The seperator must not be null");
        String list = "";
        if(hasSynonyms()){
            list = mSynonyms.get(0);
            for(int i=1
                ; i< mSynonyms.size()-1; i++){
                list += seperator + mSynonyms.get(i);
            }
            if(mSynonyms.size() > 1){
                list += seperator + mSynonyms.get(mSynonyms.size()-1);
            }
        }
        return list;
    }

}
