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
 * 3. Neither the name of NihonGo nor the names of its
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
package uk.me.mikemike.nihongo.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmResults;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.activities.StudySessionActivity;
import uk.me.mikemike.nihongo.adapters.StudyDeckListAdapter;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.viewmodels.NihongoViewModel;

/**
 * Fragment that displays the list of StudyDecks the user is currently studying
 * and provides a way to start reviewing when clicked.
 */
public class CurrentlyStudyingListFragment extends Fragment implements Observer<RealmResults<StudyDeck>>,StudyDeckListAdapter.StudyDeckAdapterHandler {

    protected NihongoViewModel mModel;
    protected StudyDeckListAdapter mAdapter;
    @BindView(R.id.recycler_view_currently_studying)
    protected RecyclerView mListStudyDecks;
    protected Unbinder mUnbinder;

    public static CurrentlyStudyingListFragment newInstance(){
        return new CurrentlyStudyingListFragment();
    }

    public CurrentlyStudyingListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mModel = ViewModelProviders.of(getActivity()).get(NihongoViewModel.class);
        mModel.init();
        mModel.getAllStudyDecks().observe(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_currently_studying_list, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mListStudyDecks.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(mAdapter != null){
            mListStudyDecks.setAdapter(mAdapter);
        }
        return v;
    }

    @Override
    public void onChanged(@Nullable RealmResults<StudyDeck> studyDecks) {
        if(mAdapter == null){
            mAdapter = new StudyDeckListAdapter(getActivity(),this,studyDecks, true);
            mListStudyDecks.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onReviewStudyDeckChosen(StudyDeck deck) {
        startActivity(StudySessionActivity.createIntent(getActivity(), deck));
    }
}
