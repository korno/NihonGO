package uk.me.mikemike.nihongo.model;

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


import java.util.Date;
import java.util.Random;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


/**
 * Represents a single study session where both the main language and japanese must be answered. This class when created will copy all the outstanding reviews
 * for the StudyDeck given and allow them to be reviewed one at a time. The class will handle automatically
 * call the spaced repetition methods and stores the correct and wrong answers.
 */

public class StudySessionMultiple extends RealmObject {

    @Required
    @PrimaryKey
    protected  String mStudySessionID = UUID.randomUUID().toString();
    protected RealmList<StudySessionQuestion> mSessionCards = new RealmList<>();
    protected RealmList<StudySessionQuestion> mCorrectCards = new RealmList<>();
    protected RealmList<StudySessionQuestion> mWrongCards = new RealmList<>();
    protected RealmList<StudyCard> mWrongCardsCards = new RealmList<>();

    protected Date mSessionDate;
    protected StudySessionQuestion mCurrentQuestion;
    protected StudySessionQuestion mPreviousQuesion;
    protected int mTotalStudyCards;
    protected int mAttempts;
    protected boolean mIsFinished=false;
    protected boolean mCurrentQuestionIsJapaneseAnswer=false;


    public String getStudySessionID(){return mStudySessionID;}
    public int getRemainingStudyCardsCount() { return mSessionCards.size();}
    public int getTotalStudyCardsCount() { return  mTotalStudyCards; }
    public boolean isFinished() { return mIsFinished;}
    public RealmList<StudyCard> getWrongCards() { return  mWrongCardsCards;}
    public RealmList<StudySessionQuestion> getCorrectCards() { return mCorrectCards;}
    public RealmList<StudySessionQuestion> getQuestions() { return mSessionCards;}
    public int getNumberOfAttempts(){return  mAttempts;}
    public boolean isCurrentQuestionJapaneseAnswer(){return mCurrentQuestionIsJapaneseAnswer;}


    @Ignore
    private Random mRandom;

    public StudySessionMultiple(StudyDeck source, Date sessionDate){

        if(source == null) throw new IllegalArgumentException("Source deck must not be null");
        if(sessionDate == null) throw new IllegalArgumentException("Session date must not be null");

        for(StudyCard c: source.getCardsWithNextReviewDateOlderThan(sessionDate)){
            StudySessionQuestion question = new StudySessionQuestion(c);
            mSessionCards.add(question);
        }


        mSessionDate = sessionDate;
        setDefaults();


    }

    public StudySessionMultiple(StudyDeck source, Date sessionDate, int maxReviews){
        if(source == null) throw new IllegalArgumentException("Source deck must not be null");
        if(sessionDate == null) throw new IllegalArgumentException("Session date must not be null");
        if(maxReviews < 1) throw new IllegalArgumentException("Mass reviews must not be less than 1");
        mSessionDate = sessionDate;
        int count=0;
        for(StudyCard c: source.getCardsWithNextReviewDateOlderThan(sessionDate)){
            StudySessionQuestion question = new StudySessionQuestion(c);
            mSessionCards.add(question);
            count++;
            if(count == maxReviews){
                break;
            }
        }
        setDefaults();
    }


    private void setDefaults(){
        mAttempts = 0;
        mTotalStudyCards = mSessionCards.size();
        mCurrentQuestion = mSessionCards.first(null);
        mPreviousQuesion = null;
        mRandom = new Random();
        mCurrentQuestionIsJapaneseAnswer = mRandom.nextInt(2) == 1;
        if(mCurrentQuestion == null){
            mIsFinished = true;
        }
    }

    /* required by realm */
    public StudySessionMultiple(){
        mRandom = new Random();
    }

    /**
     * Gets the current StudyCard to be studied
     * @return
     */
    public StudyCard getCurrent(){
        if(mIsFinished) throw new RuntimeException("The test is finished getCurrent must not be called");
        return mCurrentQuestion.getSourceCard();
    }

    public boolean isCurrentNew(){
        return mCurrentQuestion.mIsNew();
    }

    public void unsetNew(){
        mCurrentQuestion.showNewCard();
    }

    /**
     * Returns true if  attempts have been made on this study sesion
     * @return
     */
    public boolean hasAnsweredAQuestion() {
        return mAttempts != 0;
    }


    /**
     * Gets the previously studied question.
     * @return
     */
    public StudyCard getPrevious(){
        if(mIsFinished) throw new RuntimeException("The test is finished so getPrevious must not be called");
        return mPreviousQuesion.getSourceCard();
    }

    /**
     * Answers the current question by checking the japanese (hiragana) to the answer provided. After checking the
     * session will move on to the next question (or end the study if there isnt one)
     * @param answer The answer to use
     * @return true if correct, false if not
     */
    public boolean answerJapanese(String answer, boolean updateState){
        if(answer == null) throw new IllegalArgumentException("answer must not be null");
        if(mIsFinished) throw new RuntimeException("The test is finished so answerJapanese calls are not allowed");
        boolean result;
        if(updateState){
            result = mCurrentQuestion.answerJapanese(answer);
            handleAnswer(result);
        }
        else{
            result = mCurrentQuestion.getSourceCard().getSourceCard().isHiraganaEqual(answer);
        }
        return result;
    }

    public boolean answerJapanese(String answer){
        return answerJapanese(answer, true);
    }


    /**
     * Answers the current question by checking the main language to the answer provided. After checking the
     * session will move on to the next question (or end the study if there isnt one)
     * @param answer The answer to use
     * @param updateState If the session should update its state after checking the answer (moving to the next question etc)
     * @return true if correct, false if not
     */
    public boolean answerMainLanguage(String answer, boolean updateState){
        if(answer == null) throw new IllegalArgumentException("answer must not be null");
        if(mIsFinished) throw new RuntimeException("The test is finished so answerJapanese calls are not allowed");
        boolean result;

        if(updateState){
            result = mCurrentQuestion.answerMainLanguage(answer);
            handleAnswer(result);
        }
        else{
            result = mCurrentQuestion.getSourceCard().getSourceCard().isMainLanguageEqual(answer, true);
        }
        return result;
    }

    public boolean answerMainLanguage(String answer){
        return answerMainLanguage(answer, true);
    }

    protected void handleAnswer(boolean result){
        mSessionCards.remove(0);
        // are we finished (both parts answered correcrly)
        // this just means the user has got both parts of the question correct
        if(mCurrentQuestion.isCorrect()){
            mAttempts++;
            // we we correct first time?
            if(mCurrentQuestion.wasCorrectFirstTime()){
                mCorrectCards.add(mCurrentQuestion);
            }
            else{
                mWrongCards.add(mCurrentQuestion);
                // this is stupid but it means we dont have to break the StudyResultsFragment too much
                mWrongCardsCards.add(mCurrentQuestion.getSourceCard());
            }

            int srsScore = 3 + ( mCurrentQuestion.wasJapaneseJapaneseCorrectFirstTime() ? 1 : -1) +
                    (mCurrentQuestion.wasMainLanguageCorrectFirstTime() ? 1 : -1 );
            mCurrentQuestion.getSourceCard().getLearningState().performSR(srsScore, mSessionDate);
        }
        else{
            mSessionCards.add(mRandom.nextInt(mSessionCards.size()+1), mCurrentQuestion);
        }
        moveToNextQuestion();
    }



    protected boolean doesCorrectAnswersContainCard(StudySessionQuestion card){
        if(isManaged()){
            return mCorrectCards.where().equalTo(StudySessionQuestionFields.STUDY_SESSION_QUESTION_ID, card.getStudySessionQuestionID()).count() == 1;
        }
        else{
            return mCorrectCards.contains(card);
        }
    }

    protected boolean doesWrongAnswersContainCard(StudySessionQuestion card){
        if(isManaged()){
            return mWrongCards.where().equalTo(StudySessionQuestionFields.STUDY_SESSION_QUESTION_ID, card.getStudySessionQuestionID()).count() == 1;
        }
        else{
            return mWrongCards.contains(card);
        }
    }

    public boolean answerCurrentQuestion(String answer, boolean updateState){
        if(mIsFinished) throw new RuntimeException("The test is finished so answerCurrentQuestion calls are not allowed");
        if(mCurrentQuestionIsJapaneseAnswer){
            return answerJapanese(answer, updateState);
        }
        return answerMainLanguage(answer, updateState);
    }

    public boolean answerCurrentQuestion(String answer){
        return answerCurrentQuestion(answer, true);
    }


    /**
     * Forces the test to finish early
     */
    public void finishSession(){
        if(mIsFinished) throw new IllegalStateException("The test is finished so calling finishTest is not allowed");
        mCurrentQuestion=null;
        mIsFinished=true;
        mSessionCards.clear();
    }

    protected void moveToNextQuestion(){
        mPreviousQuesion = mCurrentQuestion;
        mCurrentQuestion = mSessionCards.first(null);
        if(mCurrentQuestion == null){
            mIsFinished = true;
            return;
        }
        // if it is the first time randomise which we show first
        if(! mCurrentQuestion.hasAnsweredMainLanguage() && !mCurrentQuestion.hasAnsweredJapanese()){
            mCurrentQuestionIsJapaneseAnswer = mRandom.nextInt(2) == 1;
        }
        else{
            // else show the japanese bit first if they have answered both
            mCurrentQuestionIsJapaneseAnswer = !mCurrentQuestion.isJapaneseCorrect();
        }

    }

}
