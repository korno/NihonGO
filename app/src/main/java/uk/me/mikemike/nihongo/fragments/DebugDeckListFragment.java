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
package uk.me.mikemike.nihongo.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmResults;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.activities.DebugCardListActivity;
import uk.me.mikemike.nihongo.activities.DebugDeckStudyStateActivity;
import uk.me.mikemike.nihongo.adapters.DebugDeckListAdapter;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.viewmodels.NihongoViewModel;


/**
 * Fragment that presents a debug view of all decks in the realm
 */
public class DebugDeckListFragment extends Fragment implements Observer<RealmResults<Deck>>,DebugDeckListAdapter.DebugDeckListListener {

    @BindView(R.id.recycler_view_all_decks)
    RecyclerView mDeckList;
    NihongoViewModel mModel;
    DebugDeckListAdapter mAdapter;
    protected Unbinder mUnbinder;

    public DebugDeckListFragment() {
        // Required empty public constructor
    }

    public static DebugDeckListFragment newInstance() {
        DebugDeckListFragment fragment = new DebugDeckListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_debug_deck_list, container, false);
        mModel = ViewModelProviders.of(getActivity()).get(NihongoViewModel.class);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v, savedInstanceState);
        mModel.getAllDecks().observe(this, this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void onChanged(@Nullable RealmResults<Deck> decks) {
        if(mAdapter == null){
            mAdapter = new DebugDeckListAdapter(getActivity(), this,  decks, true);
            mDeckList.setAdapter(mAdapter);
            mDeckList.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    @Override
    public void onDeckClicked(Deck d) {
        Intent i = new Intent(getActivity(), DebugCardListActivity.class);
        i.putExtra(DebugCardListActivity.ARG_DECK_ID, d.getDeckID());
        startActivity(i);
    }

    @Override
    public void onDeckViewStudyInformationSelected(Deck d) {
        Intent deckStateIntent = DebugDeckStudyStateActivity.createIntent(getActivity(), d.getStudyDecksUsingThisDeck().first().getStudyDeckID());
        startActivity(deckStateIntent);
    }
}
