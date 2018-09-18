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
 * 3. Neither the name of NihonGO nor the names of its
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
import android.content.Context;
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
import uk.me.mikemike.nihongo.adapters.ChooseDeckToStudyListAdapter;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.viewmodels.NihongoViewModel;

/**
 * Fragment that displays all list of all decks that are not being studied and
 * allows the user to select one to start studying. Once a deck has been selected
 * this fragment will invoke the methods to create a studydeck for that deck
 * and offer the user a choice to start studying straight away
 *
 */
public class ChooseDeckToStudyFragment extends Fragment implements Observer<RealmResults<Deck>>,ChooseDeckToStudyListAdapter.ChooseDeckToStudyAdapterHandler {


    public interface ChooseDeckToStudyFragmentListener {
        public void onStudyDeckCreated(StudyDeck d);
    }


    protected NihongoViewModel mViewModel;
    protected ChooseDeckToStudyListAdapter mAdapter;
    @BindView(R.id.recycler_view_decks_list)
    protected RecyclerView mListDeck;
    protected Unbinder mUnbinder;
    protected View mRootView;
    protected ChooseDeckToStudyFragmentListener mListener;


    public ChooseDeckToStudyFragment() {

    }

    public static ChooseDeckToStudyFragment newInstance(){
        return new ChooseDeckToStudyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_select_new_deck_to_study, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mListDeck.setLayoutManager(new LinearLayoutManager(getActivity()));
        // this will occur if we have been created before, and are just being reattached
        // and the view is just being recreated
        if(mAdapter != null){
            mListDeck.setAdapter(mAdapter);

        }
        return mRootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(NihongoViewModel.class);
        mViewModel.getAllDecksNotBeingStudied().observe(this, this);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseDeckToStudyFragmentListener) {
            mListener = (ChooseDeckToStudyFragmentListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onChanged(@Nullable RealmResults<Deck> decks) {
        // this might look weird but we only need to handle this the first time
        // after that, the adapter itself will listen to data changes
        if(mAdapter == null){
            mAdapter = new ChooseDeckToStudyListAdapter(getContext(), this ,decks,true);
            mListDeck.setAdapter(mAdapter);
        }

    }

    /* The ChooseDeckToStudyListAdapter will invoke this */
    @Override
    public void onDeckChosen(Deck d) {
        final StudyDeck studyDeck = mViewModel.startStudying(d);
        mListener.onStudyDeckCreated(studyDeck);

    }
}
