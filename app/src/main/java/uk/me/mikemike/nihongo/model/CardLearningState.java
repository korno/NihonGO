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
 * Represents the learning state of a single card.
 */
public class CardLearningState extends RealmObject {
    @PrimaryKey
    @Required
    protected String mCardLearningStateID = UUID.randomUUID().toString();
    protected Card mSourceCard;
    protected Date mNextDueDate;
    protected float mEasiness;
    protected int mReps;
    protected float mInterval;


    public String getCardLearningStateID(){return mCardLearningStateID;}
    public Card getSourceCard(){return mSourceCard;}
    public Date getNextDueData(){return mNextDueDate;}
    public float getEasyness(){return mEasiness;}
    public int getReps(){return mReps;}
    public float getInterval(){return mInterval;}


    public static float STARTING_E_VALUE = 2.5f;

    /* required by realm */
    public CardLearningState(){}

    public CardLearningState(Card card, Date nextDueDate, float easiness, int consecutiveCorrectAnswers,
                                float interval){
        if(card == null)throw new IllegalArgumentException("the source card must not be null");
        mSourceCard = card;
        mNextDueDate = nextDueDate;
        mEasiness = easiness;
        mReps = consecutiveCorrectAnswers;
        mInterval = interval;
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
            mReps = 1;
        }
        else {
            // newEF = oldEF + (0.1 - (5-grade)*(0.08+(5-grade)*0.02));
            mEasiness = Math.max(1.3f, oldE + (0.1f - (5f-answerLevel)*(0.08f+(5f-answerLevel)*0.02f)));
            mReps++;
        }

        switch (mReps){
            case 1:
                mInterval=1;
                break;
            case 2:
                mInterval=6;
                break;
            default:
                mInterval = mInterval * mEasiness;
        }

        mNextDueDate = DateUtils.addDaysToDate(date, (int)Math.ceil(mInterval));

    }

}
