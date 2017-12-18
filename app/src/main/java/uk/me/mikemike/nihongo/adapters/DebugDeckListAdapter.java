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
package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Deck;

/**
 * List Adapter for the DebugDeckListFragment
 */
public class DebugDeckListAdapter extends RealmRecyclerViewAdapter<Deck, DebugDeckListAdapter.DebugDeckListRecyclerView> {

    protected Context mContext;
    protected String mWordCountFormatString;
    protected String mAuthorFormatString;
    protected DebugDeckListListener mListener;

    public DebugDeckListAdapter(Context c, DebugDeckListListener listener,  @Nullable OrderedRealmCollection data, boolean autoUpdate) {
        super(data, autoUpdate);
        mListener = listener;
        mContext = c;
        Resources res = c.getResources();
        mWordCountFormatString = res.getString(R.string.item_debug_deck_list_card_count);
        mAuthorFormatString = res.getString(R.string.item_debug_deck_list_author);
    }

    @Override
    public DebugDeckListRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View reycleView = LayoutInflater.from(mContext).inflate(R.layout.item_deck_debug, null);
        DebugDeckListRecyclerView h = new DebugDeckListRecyclerView(reycleView);
        return h;
    }

    @Override
    public void onBindViewHolder(DebugDeckListRecyclerView holder, int position) {
        Deck deck = getItem(position);
        holder.bindToDeck(deck);
    }



    public interface DebugDeckListListener{
        void onDeckClicked(Deck d);
        void onDeckViewStudyInformationSelected(Deck d);
    }


    public class DebugDeckListRecyclerView extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_deck_name)
        protected TextView mTextName;
        @BindView(R.id.text_deck_description)
        protected TextView mTextDescription;
        @BindView(R.id.text_card_count)
        protected TextView mTextCardCount;
        @BindView(R.id.text_author)
        protected TextView mTextAuthor;
        @BindView(R.id.text_study_state)
        protected TextView mTextStudyState;
        @BindView(R.id.button_view_study_state)
        protected Button mViewStudyInformation;

        protected View mRoot;

        public DebugDeckListRecyclerView(View itemView) {
            super(itemView);
            mRoot= itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bindToDeck(Deck deck){
            mTextName.setText(deck.getName());
            mTextDescription.setText(deck.getDescription());
            mTextCardCount.setText(String.format(mWordCountFormatString, deck.getCards().size()));
            mTextAuthor.setText(String.format(mAuthorFormatString, deck.getAuthor()));
            if(deck.isBeingStudied()){
                mViewStudyInformation.setVisibility(View.VISIBLE);
                mTextStudyState.setText(R.string.item_debug_deck_list_is_being_studied);
            }
            else{
                mViewStudyInformation.setVisibility(View.GONE);
                mTextStudyState.setText(R.string.item_debug_deck_list_is_not_being_studied);
            }
            mRoot.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onDeckClicked(getItem(getAdapterPosition()));
        }

        @OnClick(R.id.button_view_study_state)
        public void onViewStudyStateClick(){
            mListener.onDeckViewStudyInformationSelected(getItem(getAdapterPosition()));
        }
    }
}
