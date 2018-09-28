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
package uk.me.mikemike.nihongo.data;

import java.util.Date;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.model.DeckFields;
import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudyDeckFields;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.model.StudySessionFields;
import uk.me.mikemike.nihongo.utils.DateUtils;

/**
 * The data access class for Nihongo, provides queries and methods to query the nihongo realm data store.
 * Created by mike on 11/16/17.
 */
public class NihongoRepository {

    protected Realm mRealm;
    protected RealmResults<Deck> mAllDecks = null;
    protected RealmResults<StudyDeck> mAllStudyDecks = null;
    protected RealmResults<StudySession> mAllStudySessions = null;
    protected RealmResults<Deck> mDecksNotBeingStudied = null;


    public Realm getConnectedRealm() {
        return mRealm;
    }


    public NihongoRepository(Realm realm) {
        if (realm == null) throw new IllegalArgumentException("realm must not be null");
        mRealm = realm;
    }

    /**
     * Returns all the decks in the repository. This includes decks that are being studied and
     * decks that are not being studied.
     *
     * @return A RealmResults that contains all the decks.
     */
    public RealmResults<Deck> getAllDecks() {
        if (mAllDecks == null) {
            mAllDecks = mRealm.where(Deck.class).findAll();
        }
        return mAllDecks;
    }


    public RealmResults<StudySession> getAllStudySessions(){
        if(mAllStudySessions == null){
            mAllStudySessions = mRealm.where(StudySession.class).findAll();
        }
        return mAllStudySessions;
    }

    /**
     * Gets the number of decks present in the realm
     *
     * @return the number of decks present in the realm
     */
    public int getTotalNumberOfDecks() {
        return getAllDecks().size();
    }


    /**
     * Gets all of the StudyDecks in the repository
     *
     * @return A RealmResults that contains all the study decks
     */
    public RealmResults<StudyDeck> getAllStudyDecks() {
        if (mAllStudyDecks == null) {
            mAllStudyDecks = mRealm.where(StudyDeck.class).findAll();
        }
        return mAllStudyDecks;
    }

    /**
     * Gets the number of study decks in the realm
     *
     * @return the number of study decks in the realm
     */
    public int getTotalNumberOfStudyDecks() {
        return getAllStudyDecks().size();
    }


    /**
     * Gets a StudyDeck by its ID
     *
     * @param id The Id to use must not be null
     * @return The StudyDeck or null if not found
     */
    public StudyDeck getStudyDeckByID(String id) {
        if (id == null) throw new IllegalArgumentException("The ID must not be null");
        return mRealm.where(StudyDeck.class).equalTo(StudyDeckFields.STUDY_DECK_ID, id).findFirst();
    }


    /**
     * Gets all study decks that have a StudyCard where the review date is older than the
     * date passed
     *
     * @param date the date that any study carss next review date must be older than
     * @return A RealmResults that contains all study decks with outstanding study decks
     */
    public RealmResults<StudyDeck> getStudyDecksWithReviewsWaiting(Date date) {
        if (date == null) throw new IllegalArgumentException("the date must not be null");
        RealmResults<StudyDeck> results;
        results = mRealm.where(StudyDeck.class).lessThanOrEqualTo("mStudyCards.mLearningState.mNextDueDate", date).findAll();
        return results;
    }


    /**
     * Performs the spaced repetition algorithm on the learning state. This method just handles wraping the call in a realm
     * transaction and canceling the transaction if something fails.
     *
     * @param s     The learning state to invoke the performSR method on
     * @param score the score
     * @param date  the date to use
     */
    public void performSR(LearningState s, int score, Date date) {
        if (s == null) throw new IllegalArgumentException("the learning state must not be null");
        try {
            mRealm.beginTransaction();
            s.performSR(score, date);
            mRealm.commitTransaction();
        } catch (Exception e) {
            if (mRealm.isInTransaction()) {
                mRealm.cancelTransaction();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new StudyDeck and collection of StudyCards for the deck passed to it and all of its cards
     * The maxCardsPerDay and intervalBetweenCards allow all the cards first study date to spread over an interval
     *
     * @param deck                    The deck to use
     * @param date                    The start date for the first study
     * @param maxCardsPerDay          how many cards will be set for each day
     * @param intervalBetweenMaxCards the interval between each max cards collection. set to zero for all study cards to have the same start day
     * @return The managed StudyDeck representing the deck
     */
    public StudyDeck startStudying(Deck deck, Date date, int maxCardsPerDay, int intervalBetweenMaxCards) {

        if (deck == null) throw new IllegalArgumentException("the deck must not be null");
        if (date == null) throw new IllegalArgumentException("the date must not be null");
        if (maxCardsPerDay <= 0)
            throw new IllegalArgumentException("the max cards per day must be a positive integer");
        if (intervalBetweenMaxCards < 0)
            throw new IllegalArgumentException("the interval between max cards must be > 0");

        try {
            mRealm.beginTransaction();
            Date current = date;
            int cardCount = 0;
            StudyDeck d = new StudyDeck(deck.getName(), new RealmList<StudyCard>(), deck);
            for (Card c : deck.getCards()) {
                StudyCard sc = new StudyCard(c, new LearningState(current, LearningState.STARTING_E_VALUE, 0, 0));
                d.getStudyCards().add(sc);
                cardCount++;
                if (cardCount == maxCardsPerDay) {
                    cardCount = 0;
                    current = DateUtils.addDaysToDate(current, intervalBetweenMaxCards);
                }
            }

            StudyDeck managedDeck = mRealm.copyToRealmOrUpdate(d);
            mRealm.commitTransaction();
            return managedDeck;
        } catch (Exception e) {
            if (mRealm.isInTransaction()) {
                mRealm.cancelTransaction();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all the decks in the realm that are not being studied (i.e there are no StudyDeck objects that
     * reference them)
     *
     * @return a realm results containing the decks
     */
    public RealmResults<Deck> getAllDecksNotBeingStudied() {
        if (mDecksNotBeingStudied == null) {
            mDecksNotBeingStudied = mRealm.where(Deck.class).isEmpty(DeckFields.STUDY_DECKS).findAll();
        }
        return mDecksNotBeingStudied;
    }


    /**
     * Returns the number of decks that are not being studied (i.e. don't have a StudyDeck that references them)
     *
     * @return
     */
    public int getNumberOfDecksNotBeingStudied() {
        return getAllDecksNotBeingStudied().size();
    }

    /**
     * Creates a new study session (inserted into realm) from the studydeck with cards with review dates old than date
     * @param deck The Studydeck to use
     * @param date The date to use
     * @return a managed studydeck
     */
    public StudySession createStudySession(StudyDeck deck, Date date) {
        StudySession s = new StudySession(deck, date);
        mRealm.beginTransaction();
        StudySession mss = mRealm.copyToRealmOrUpdate(s);
        mRealm.commitTransaction();
        return mss;
    }

    /**
     * Answers a study sessions question. This method just wraps the StudySession.answerJapanese
     * in a realm transaction.
     * @param session The session to use
     * @param answer The answer
     * @return true if correct, false otherwise
     */
    public boolean answerStudySessionJapaneseAnswer(StudySession session, String answer, boolean updateSessionState){
        boolean result;
        try{
            mRealm.beginTransaction();
            result = session.answerJapanese(answer, updateSessionState);
            mRealm.commitTransaction();
            return result;
        }
        catch (Exception e){
            if(mRealm.isInTransaction()) {
                mRealm.cancelTransaction();
            }
            throw new RuntimeException(e);
        }

    }


    /**
     * Returns the deck with the id that matches
     * @param id the id to use
     * @return Deck of null if none present
     */
    public Deck getDeckByID(String id){
        if(id == null) throw new IllegalArgumentException("id must not be null");
        return mRealm.where(Deck.class).equalTo(DeckFields.DECK_ID, id).findFirst();
    }

    /**
     * Deletes everything from realm
     */
    public void deleteEverything(){
        mRealm.beginTransaction();
        mRealm.where(StudySession.class).findAll().deleteAllFromRealm();
        mRealm.where(StudyCard.class).findAll().deleteAllFromRealm();
        mRealm.where(StudyDeck.class).findAll().deleteAllFromRealm();
        mRealm.where(Card.class).findAll().deleteAllFromRealm();
        mRealm.where(LearningState.class).findAll().deleteAllFromRealm();
        mRealm.where(Deck.class).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * Deletes all study sessions only
     */
    public void deleteAllStudySessions(){
        mRealm.beginTransaction();
        mRealm.where(StudySession.class).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * Returns the study session with the correct id
     * @param id the id to search for
     * @return study or null if it cannot be found
     */
    public StudySession getStudySessionByID(String id){
        if(id == null) throw new IllegalArgumentException("id must not be null");
        return mRealm.where(StudySession.class).equalTo(StudySessionFields.STUDY_SESSION_ID, id).findFirst();
    }


    /**
     * Wraps a StudySession.answerCurrentQuestion in a realm transaction
     * @param answer The answer to use
     * @param session the session that will be invoked, must not be null
     * @return true if the answer was correct, false otherwise
     */
    public boolean answerStudySessionCurrentQuestion(String answer, StudySession session, boolean updateSessionState){
        if(session == null) throw new IllegalArgumentException("session must not be null");
        boolean result;
        mRealm.beginTransaction();
        result = session.answerCurrentQuestion(answer, updateSessionState);
        mRealm.commitTransaction();
        return result;
    }


    /**
     * Wraps the StudySession.finishTest method in a realm transaction
     * @param session the session to use
     */
    public void finishSession(StudySession session){
        if(session == null ) throw new IllegalArgumentException("session must not be null");
        mRealm.beginTransaction();
        session.finishSession();
        mRealm.commitTransaction();
    }


    /**
     * Stops studying the passed study deck. This deletes all study related data from the realm
     * @param studyDeck The study deck to delete, must not be null
     */
    public void stopStudying(StudyDeck studyDeck){
        if(studyDeck == null) throw new IllegalArgumentException("The study deck must not be null");
        mRealm.beginTransaction();
        // delete all the learning states first
        for (StudyCard card: studyDeck.getStudyCards()
             ) {
            card.getLearningState().deleteFromRealm();
        }
        // delete the list of study cards
        studyDeck.getStudyCards().deleteAllFromRealm();
        // delete the study deck itself
        studyDeck.deleteFromRealm();
        mRealm.commitTransaction();
    }

}

