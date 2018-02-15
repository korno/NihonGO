package uk.me.mikemike.nihongo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.me.mikemike.nihongo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudySessionResultsFragment extends Fragment {


    public StudySessionResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_session_results, container, false);
    }

}
