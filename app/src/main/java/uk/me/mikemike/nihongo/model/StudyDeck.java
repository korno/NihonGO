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

import java.util.Date;
import java.util.UUID;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 *
 * Represents a set of cards being studied
 * Created by mike on 11/28/17.
 */
public class StudyDeck extends RealmObject {

    @PrimaryKey
    @Required
    protected String mStudyDeckID = UUID.randomUUID().toString();

    protected String mName;
    protected RealmList<StudyCard> mStudyCards;
    protected Deck mSourceDeck;

    public String getStudyDeckID(){return mStudyDeckID;}
    public String getName(){return mName;}
    public RealmList<StudyCard> getStudyCards() { return mStudyCards;}
    public Deck getSourceDeck() { return mSourceDeck; }


    /* required by realm */
    public StudyDeck(){
    }

    public StudyDeck(String name, RealmList<StudyCard> studyCards, Deck sourceDeck){
        mStudyCards = studyCards;
        mName = name;
        mSourceDeck = sourceDeck;
    }

    public RealmResults<StudyCard> getCardsWithNextReviewDateOlderThan(Date date){
        if(date == null) throw new IllegalArgumentException("the date must not be null");
        return mStudyCards.where().lessThanOrEqualTo("mLearningState.mNextDueDate", date).findAll();
    }

    public boolean hasReviewsWaiting(Date date){
        return getCardsWithNextReviewDateOlderThan(date).size() > 0;
    }
}
