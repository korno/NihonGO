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
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import uk.me.mikemike.nihongo.utils.DateUtils;

/**
 * @author mike
 * Represents a learning state for something, implements the supermemo 2 algorithym
 */
public class LearningState extends RealmObject {


    public static final int NEW_LEVEL =0;
    public static final int STUDYING_LEVEL = 1;
    public static final int MASTERED_LEVEL = 2;
    public static float STARTING_E_VALUE = 2.5f;
    public static float MINIMUM_E_VALUE = 1.3f;
    public static float MASTERED_E_VALUE = 2.48f;
    public static int MASTERED_MIN_TRIES = 5;

    @PrimaryKey
    @Required
    protected String mCardLearningStateID = UUID.randomUUID().toString();
    protected Date mNextDueDate;
    protected float mEasiness;
    protected int mReps;
    protected float mInterval;
    protected int mTotalTries;
    protected int mStudyLevel;


    public String getCardLearningStateID(){return mCardLearningStateID;}
    public Date getNextDueDate(){return mNextDueDate;}
    public float getEasyness(){return mEasiness;}
    public int getReps(){return mReps;}
    public float getInterval(){return mInterval;}
    public int getTotalTries(){return mTotalTries;}
    public int getStudyLevel(){return mStudyLevel;}



    public void setEasiness(float easiness){
        if(easiness < MINIMUM_E_VALUE) throw new IllegalArgumentException("easiness value is less than MINIMUM_E_VALUE");
        mEasiness = easiness;
        changeLevel();
    }

    public void setTotalTries(int tries){
        if(tries < 0) throw new IllegalArgumentException("Total tries cannot be less than 0");
        mTotalTries=tries;
        changeLevel();
    }





    /* required by realm */
    public LearningState(){
        mNextDueDate = new Date();
        mEasiness = STARTING_E_VALUE;
        mReps = 0;
        mInterval = 0;
        mNextDueDate = new Date();
        mStudyLevel=NEW_LEVEL;
    }

    /**
     *
     * @param nextDueDate The next date the test will be due
     * @param easiness The starting easiness value. Must be greater or equal to  {@value #MINIMUM_E_VALUE }
     * @param consecutiveCorrectAnswers The number of consecutive correct answers. Must be greate than or equal to zero
     * @param interval The  interval to use.
     */
    public LearningState(Date nextDueDate, float easiness, int consecutiveCorrectAnswers,
                         float interval){

        if(nextDueDate == null) throw new IllegalArgumentException("next due date must not be null");
        if(easiness < MINIMUM_E_VALUE) throw new IllegalArgumentException("the easiness value is less than the minimum value");
        if(interval < 0) throw new IllegalArgumentException("the interval must be 0 or greater");
        if(consecutiveCorrectAnswers < 0) throw new IllegalArgumentException("the number of consecutive correct answers must be 0 or more");
        mNextDueDate = nextDueDate;
        mEasiness = easiness;
        mReps = consecutiveCorrectAnswers;
        mInterval = interval;
        mTotalTries=0;
    }

    /**
     * Sets the study state to represent a new study item. This will reset all previous data if the item
     * has been studied already
     * @param startDate The date of the first study
     */
    public void startLearning(Date startDate){
        if(startDate == null)throw new IllegalArgumentException("Start date must not be null");
        mNextDueDate = startDate;
        mInterval = 0;
        mEasiness = STARTING_E_VALUE;
        mReps = 0;
        mTotalTries=0;
        mStudyLevel=NEW_LEVEL;
    }

    /**
     * Performs the Supermemo 2 algorithim to generate the next study date
     * @param answerLevel The level or answer 0-5 where 5 is the best
     * @param date the date the test was performed
     */
    public void performSR(int answerLevel, Date date){

        if(answerLevel > 5) throw new IllegalArgumentException("answerLevel can not be greater than 5");
        if(answerLevel < 0) throw new IllegalArgumentException("answerLevel can not be less than 0");
        if(date == null) throw new IllegalArgumentException("the date can not be null");

        float oldE = mEasiness;
        if(answerLevel < 3){
            mInterval = 0;
            mReps = 0;
        }
        else {
            // newEF = oldEF + (0.1 - (5-grade)*(0.08+(5-grade)*0.02));
            mEasiness = Math.max(MINIMUM_E_VALUE, oldE + (0.1f - (5f-answerLevel)*(0.08f+(5f-answerLevel)*0.02f)));
            mReps++;
        }

        switch (mReps){
            case 0:
                mInterval=1;
                break;
            case 1:
                mInterval=1;
                break;
            case 2:
                mInterval=6;
                break;
            default:
                mInterval = mInterval * mEasiness;
        }

        mTotalTries++;
        mNextDueDate = DateUtils.addDaysToDate(date, (int)Math.ceil(mInterval));
        changeLevel();
    }


    protected void changeLevel(){

        if(mTotalTries == 0){
            mStudyLevel=NEW_LEVEL;
        }
        else if(mEasiness >= MASTERED_E_VALUE && mTotalTries >= MASTERED_MIN_TRIES){
            mStudyLevel = MASTERED_LEVEL;
        }

        else {
            mStudyLevel=STUDYING_LEVEL;
        }


    }

    public boolean isNew(){return mStudyLevel == 0;}

}
