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
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Represents a single card being studied. Includes a link to the source card and the learning state
 * of this item.
 * Created by mike on 11/27/17.
 */
public class StudyCard extends RealmObject {

    protected String mStudyCardID = UUID.randomUUID().toString();
    protected Card mSourceCard;
    protected LearningState mLearningState;

    public String getStudyCardID(){return  mStudyCardID;}
    public Card getSourceCard() { return mSourceCard;}
    public LearningState getLearningState() { return mLearningState;}

    /* required by realm */
    public StudyCard(){}

    public StudyCard(Card card, LearningState state){
        if(card == null) throw new IllegalArgumentException("the source card cannot be null");
        if(state == null) throw new IllegalArgumentException("the learning state cannot be null");
        mSourceCard = card;
        mLearningState = state;
    }

    public static StudyCard StartStudying(final Card source, final Date firstStudyDate, Realm destination){
        if(source == null) throw new IllegalArgumentException("the source card cannot be null");
        if(firstStudyDate == null) throw new IllegalArgumentException("the first study date cannot be null");
        if(destination == null) throw new IllegalArgumentException("the destination realm cannot be null");
        if(source.isManaged() == false) throw new IllegalArgumentException("The source card is not managed by realm");
        if(source.isValid() == false) throw new IllegalArgumentException("the source card is not valid");

        final LearningState learningState = new LearningState();
        learningState.startLearning(firstStudyDate);
        final StudyCard studyCard = new StudyCard(source, learningState);

        destination.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(learningState);
                realm.insert(studyCard);
            }

        });

        return studyCard.isManaged() ? studyCard : null;
    }

}
