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

/**
 * @author mike
 * Represents a single logical deck, or collection, of Cards for study.
 * For example a deck might be a vocabulary set of body parts etc
 */
public class Deck extends RealmObject {

    @PrimaryKey
    @Required
    protected String mDeckID = UUID.randomUUID().toString();
    protected String mName;
    protected String mDescription;
    protected String mAuthor;
    protected RealmList<Card> mCards;

    public String getName(){return mName;}
    public String getDescription(){return mDescription;}
    public String getAuthor(){return mAuthor;}
    public RealmList<Card> getCards(){return mCards;}
    public String getDeckID(){return mDeckID;}


    public void setName(String value){mName = value;}
    public void setDescription(String value){mDescription = value;}
    public void setAuthor(String value){mAuthor=value;}
    public void setCards(RealmList<Card> value){mCards=value;}


    public Deck(String name, String description, String author, RealmList<Card> cards) {
        this.mName = name;
        this.mDescription = description;
        this.mAuthor = author;
        this.mCards = cards;
    }

    /* Required by realm */
    public Deck(){

    }



}
