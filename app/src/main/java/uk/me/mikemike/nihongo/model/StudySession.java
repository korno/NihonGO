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
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


/**
 * Represents a single study session. This class when created will copy all the outstanding reviews
 * for the StudyDeck given and allow them to be reviewed one at a time. The class will handle automatically
 * call the spaced repetition methods and stores the correct and wrong answers.
 */

public class StudySession extends RealmObject {


    @Required
    @PrimaryKey
    protected  String mStudySessionID = UUID.randomUUID().toString();
    protected RealmList<StudyCard> mSessionCards = new RealmList<>();
    protected RealmList<StudyCard> mCorrectCards = new RealmList<>();
    protected RealmList<StudyCard> mWrongCards = new RealmList<>();

    protected Date mSessionDate;
    protected StudyCard mCurrentQuestion;
    protected int mTotalStudyCards;

    protected boolean mIsFinished=false;


    public int getRemainingStudyCardsCount() { return mSessionCards.size();}
    public int getTotalStudyCardsCount() { return  mTotalStudyCards; }
    public boolean isFinished() { return mIsFinished;}
    public RealmList<StudyCard> getWrongCards() { return  mWrongCards;}
    public RealmList<StudyCard> getCorrectCards() { return mCorrectCards;}



    public StudySession(StudyDeck source, Date sessionDate){

        if(source == null) throw new IllegalArgumentException("Source deck must not be null");
        if(sessionDate == null) throw new IllegalArgumentException("Session date must not be null");

        mSessionCards.addAll(source.getCardsWithNextReviewDateOlderThan(sessionDate));

        mSessionDate = sessionDate;
        mTotalStudyCards = mSessionCards.size();
        mCurrentQuestion = mSessionCards.first(null);
        if(mCurrentQuestion == null){
            mIsFinished = true;
        }

    }

    /* required by realm */
    public StudySession(){}

    public StudyCard getCurrent(){
        if(mIsFinished == true) throw new RuntimeException("The test is finished getCurrent must not be called");
        return mCurrentQuestion;
    }

    protected void moveToNextQuestion(){
        mSessionCards.remove(0);
        mCurrentQuestion = mSessionCards.first(null);
        if(mCurrentQuestion == null){
            mIsFinished = true;
        }
    }

    /**
     * Answers the current question by checking the japanese (hiragana) to the answer provided. After checking the
     * session will move on to the next question (or end the study if there isnt one)
     * @param answer The answer to use
     * @return true if correct, false if not
     */
    public boolean answerJapanese(String answer){
        if(answer == null) throw new IllegalArgumentException("answer must not be null");
        if(mIsFinished) throw new RuntimeException("The test is finished so answerJapanese calls are not allowed");
        boolean result;
        result = mCurrentQuestion.getSourceCard().getJapaneseHiragana().equalsIgnoreCase(answer);
        handleAnswer(result);
        return result;
    }

    /**
     * Answers the current question by checking the main language to the answer provided. After checking the
     * session will move on to the next question (or end the study if there isnt one)
     * @param answer The answer to use
     * @return true if correct, false if not
     */
    public boolean answerMainLanguage(String answer){
        if(answer == null) throw new IllegalArgumentException("answer must not be null");
        if(mIsFinished) throw new RuntimeException("The test is finished so answerJapanese calls are not allowed");
        boolean result;
        result = mCurrentQuestion.getSourceCard().getMainLanguage().equalsIgnoreCase(answer);
        handleAnswer(result);
        return result;
    }

    protected void handleAnswer(boolean result){
        if(result){
            mCorrectCards.add(mCurrentQuestion);
        }
        else{
            mWrongCards.add(mCurrentQuestion);
        }
        mCurrentQuestion.getLearningState().performSR(result ? 4 : 1, mSessionDate);
        moveToNextQuestion();
    }

}
