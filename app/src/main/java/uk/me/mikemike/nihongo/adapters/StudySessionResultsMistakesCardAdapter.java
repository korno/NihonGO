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
 * 3. Neither the name of NihonGo nor the names of its
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
package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import se.fekete.furiganatextview.furiganaview.FuriganaTextView;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.StudyCard;


/* Adapter that displays all the cards that have a mistake after a study session */
public class StudySessionResultsMistakesCardAdapter extends RealmRecyclerViewAdapter<StudyCard, StudySessionResultsMistakesCardAdapter.MistakesCardAdapterViewHolder> {

    Context mContext;

    public StudySessionResultsMistakesCardAdapter(@Nullable OrderedRealmCollection<StudyCard> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate);
        mContext = context;
    }

    @Override
    public MistakesCardAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_studysession_mistake, null);
        return new MistakesCardAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MistakesCardAdapterViewHolder holder, int position) {
        holder.bindToCard(getItem(position).getSourceCard());
    }

    public class MistakesCardAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_japanese)
        FuriganaTextView mJapaneseTextView;
        @BindView(R.id.text_main_language)
        TextView mMainLanguageTextView;

        public void bindToCard(Card card){
            mJapaneseTextView.setFuriganaText(card.getJapaneseDisplayIfPresentKanjiIfNot());
            mMainLanguageTextView.setText(card.getMainLanguage());
        }

        public MistakesCardAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
