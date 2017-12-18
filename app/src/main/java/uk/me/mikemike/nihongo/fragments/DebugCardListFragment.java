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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmList;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.adapters.DebugCardListAdapter;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.viewmodels.DebugCardListViewModel;


public class DebugCardListFragment extends Fragment implements Observer<RealmList<Card>>,DebugCardListAdapter.DebugCardListListener {

    private static final String ARG_DECKID = "deck_id";

    protected DebugCardListViewModel mModel;
    protected DebugCardListAdapter mAdapter;
    protected String mDeckID;

    @BindView(R.id.recycler_view_all_cards)
    protected RecyclerView mCardList;
    @BindString(R.string.item_debug_card_synonym_seperator)
    protected String mSynonymSeperator;
    protected Unbinder mUnbinder;

    public DebugCardListFragment() {
        // Required empty public constructor
    }

    public static DebugCardListFragment newInstance(String deckID) {
        DebugCardListFragment fragment = new DebugCardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DECKID, deckID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeckID = getArguments().getString(ARG_DECKID);
        }
        mModel = ViewModelProviders.of(getActivity()).get(DebugCardListViewModel.class);

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        //mModel.setSourceDeck(mDeckID);
        mModel.getSourceDeckCardsAsLiveData().observe(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_debug_card_list, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onChanged(@Nullable RealmList<Card> cards) {
        if(mAdapter == null){
            mAdapter = new DebugCardListAdapter(getContext(), mSynonymSeperator, this, cards, true);
            mCardList.setAdapter(mAdapter);
            mCardList.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    @Override
    public void onCardClicked(Card c) {

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
