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
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
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
    protected Date mStartStudyDate;


    public String getStudyDeckID(){return mStudyDeckID;}
    public String getName(){return mName;}
    public RealmList<StudyCard> getStudyCards() { return mStudyCards;}
    public Deck getSourceDeck() { return mSourceDeck; }
    public Date getStartedStudyDate(){return mStartStudyDate;}



    /* required by realm */
    public StudyDeck(){
    }

    public StudyDeck(String name, RealmList<StudyCard> studyCards, Deck sourceDeck){
        mStudyCards = studyCards;
        mName = name;
        mSourceDeck = sourceDeck;
        mStartStudyDate = new Date();
    }

    /**
     * Returns all study cards belonging to this deck that have review dates older than the date parameter
     * @param date The date to dest
     * @return a realmresults containing all the results
     */
    public RealmResults<StudyCard> getCardsWithNextReviewDateOlderThan(Date date){
        if(date == null) throw new IllegalArgumentException("the date must not be null");
        return mStudyCards.where().lessThanOrEqualTo(StudyCardFields.LEARNING_STATE.NEXT_DUE_DATE, date).findAll();
    }


    /***
     * Gets the next date that this StudyDeck can be studied (the studycard with the nearest study date)
     * @return Date of the next time this can be studied
     */
    public Date getNextStudyDate(){
        return mStudyCards.sort(StudyCardFields.LEARNING_STATE.NEXT_DUE_DATE, Sort.ASCENDING).first().getLearningState().getNextDueDate();
    }

    /**
     * Does the study have reviews that are older than the specified date
     * @param date the date to check
     * @return true if there are reviews older or false if there are not
     */
    public boolean hasReviewsWaiting(Date date){
        return getCardsWithNextReviewDateOlderThan(date).size() > 0;
    }

    /**
     * Returns the number of reviews waiting that are older than the specified date
     * This is a shorthand method which calls through to getCardsWithNextReviewDateOlderThan and returns
     * the length of those results
     * @param date the date to check
     * @return how many reviews are waiting
     */
    public int howManyReviewsWaiting(Date date){
        return getCardsWithNextReviewDateOlderThan(date).size();
    }

    /**
     * Returns all the cards that have never been studied (mStudyLevel == 0)
     * @return A realm result
     */
    public RealmResults<StudyCard> getAllNewCards(){
        return getCardsForLevelQuery(LearningState.NEW_LEVEL).findAll();
    }

    /**
     * Returns all the cards currently being learnt (not new and not mastered)
     * @return a realm result
     */
    public RealmResults<StudyCard> getAllLearningCards(){
        return getCardsForLevelQuery(LearningState.STUDYING_LEVEL).findAll();
    }

    /**
     * Reeturns all the studycards that have been mastered (studyLevel == 2)
     * @return
     */
    public RealmResults<StudyCard> getAllMasteredCards(){
        return getCardsForLevelQuery(LearningState.MASTERED_LEVEL).findAll();
    }

    public int howManyMasteredCards(){
        return getAllMasteredCards().size();
    }


    /**
     * Returns the number of cards (cards that have never been studied)
     * @return how many new cars
     */
    public int howManyNewCards(){
        return getAllNewCards().size();
    }

    /**
     * Returns the percentage of cards that are new cards (have never been studied)
     * @return
     */
    public int getNewCardPercentage(){
        return calculateCardPercentage(howManyNewCards());
    }


    /**
     * Returns the percentage of cards that have been mastered
     * @return
     */
    public int getMasteredCardPercentage(){return calculateCardPercentage(getAllMasteredCards().size());}

    /**
     * Returns the percentage of cards that are bing learned (not new nor mastered)
     * @return
     */
    public int getLearningCardPercentage(){return calculateCardPercentage(getAllLearningCards().size());}

    public int getNumberOfMasteredCards(){return getAllMasteredCards().size();}

    public int getNumberOfLearningCards(){return getAllLearningCards().size();}

    /**
     * Returns the number of cards in this StudyDeck.
     * Shorthand method for getStudyCards().size()
     * @return the number of studycards in this deck
     */
    public int getNumberOfCards(){
        return mStudyCards.size();
    }

    protected int calculateCardPercentage(int number){
        if(number == 0) return 0;
        return (int)(100.0f * (float)number/getStudyCards().size());
    }

    protected RealmQuery<StudyCard> getCardsForLevelQuery(int level){
        return mStudyCards.where().equalTo(StudyCardFields.LEARNING_STATE.STUDY_LEVEL, level);
    }
}
