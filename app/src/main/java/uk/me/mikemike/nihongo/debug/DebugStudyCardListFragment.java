package uk.me.mikemike.nihongo.debug;


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
import uk.me.mikemike.nihongo.debug.DebugStudyCardListAdapter;
import uk.me.mikemike.nihongo.model.StudyCard;
import uk.me.mikemike.nihongo.viewmodels.DebugStudyStateViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugStudyCardListFragment extends Fragment implements Observer<RealmList<StudyCard>> {

    @BindView(R.id.recycler_view_all_study_cards)
    protected RecyclerView mStudyCardsList;
    protected  Unbinder mUnbinder;
    protected DebugStudyStateViewModel mModel;
    protected DebugStudyCardListAdapter mAdapter;

    @BindString(R.string.format_next_study_date)
    protected String mDateFormat;

    public DebugStudyCardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_debug_study_card_list, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mModel = ViewModelProviders.of(getActivity()).get(DebugStudyStateViewModel.class);
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mModel.getStudyCard().observe(this, this);
    }

    @Override
    public void onChanged(@Nullable RealmList<StudyCard> studyCards) {
        if(mAdapter == null){
            mAdapter = new DebugStudyCardListAdapter(getActivity(), mDateFormat, studyCards, true);
            mStudyCardsList.setAdapter(mAdapter);
            mStudyCardsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}
