/**
 * NihonGO!
 * <p>
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Deck;

/**
 * Recycler view adapter for a list of decks
 */
public class ChooseDeckToStudyListAdapter extends RealmRecyclerViewAdapter<Deck, ChooseDeckToStudyListAdapter.DeckViewHolder> {

    protected Context mContext;
    protected ChooseDeckToStudyAdapterHandler mHandler;

    public ChooseDeckToStudyListAdapter(Context c, ChooseDeckToStudyAdapterHandler handler, @Nullable OrderedRealmCollection<Deck> data, boolean autoUpdate) {
        super(data, autoUpdate);
        mContext = c;
        mHandler = handler;
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reycleView = LayoutInflater.from(mContext).inflate(R.layout.item_chose_deck_to_study, null);
        return new DeckViewHolder(reycleView);
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        holder.bindToDeck(getItem(position));
    }

    public interface ChooseDeckToStudyAdapterHandler {
        void onDeckChosen(Deck d);
    }


    /**
     * Viewholder for the deck
     */
    public class DeckViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text_deck_name)
        protected TextView mTextTitle;
        @BindView(R.id.text_deck_description)
        protected TextView mTextDescription;

        public DeckViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.button_start_studying)
        protected void startStudyingButtonClick(){
            mHandler.onDeckChosen(getItem(getAdapterPosition()));
        }

        public void bindToDeck(Deck deck){
            mTextTitle.setText(deck.getName());
            mTextDescription.setText(deck.getDescription());
        }

    }
}
