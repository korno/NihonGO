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
package uk.me.mikemike.nihongo.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.adapters.StudySessionResultsMistakesCardAdapter;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

/**
 * Fragment that displays the wrong answers from a StudySession class.
 * Expects the contaiing activity to have created a StudySessionViewModel
 * with a study session. Will do nothing if this is not present.
 */
public class StudySessionResultsFragment extends Fragment implements Observer<StudySession> {

    Unbinder mUnbinder;
    StudySessionViewModel mModel;
    StudySessionResultsMistakesCardAdapter mMistakesCardAdapter;
    @BindView(R.id.recycler_view_mistakes)
    RecyclerView mListMistakes;

    public static StudySessionResultsFragment newInstance(){
        return new StudySessionResultsFragment();
    }


    public StudySessionResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        Log.w("Results Fragment", "onActivityCreated");
        mModel = ViewModelProviders.of(getActivity()).get(StudySessionViewModel.class);
        mModel.getCurrentSession().removeObserver(this);
        mModel.getCurrentSession().observe(this, this);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.w("Results Fragment", "onCreateView");
        View v = inflater.inflate(R.layout.fragment_study_session_results, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mListMistakes.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(mMistakesCardAdapter != null){
            mListMistakes.setAdapter(mMistakesCardAdapter);
        }
        return v;
    }

    @Override
    public void onChanged(@Nullable StudySession studySession) {
        if(studySession != null){
            if(mMistakesCardAdapter == null){
                mMistakesCardAdapter = new StudySessionResultsMistakesCardAdapter(studySession.getWrongCards(), true, getContext());
                mListMistakes.setAdapter(mMistakesCardAdapter);
            }
        }
    }
}
