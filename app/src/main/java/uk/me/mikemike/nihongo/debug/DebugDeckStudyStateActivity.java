package uk.me.mikemike.nihongo.debug;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.viewmodels.DebugStudyStateViewModel;

public class DebugDeckStudyStateActivity extends AppCompatActivity {

    public final static  String ARG_STUDY_DECK_ID = "study_deck_id";
    protected DebugStudyStateViewModel mModel;


    public static Intent createIntent(Context c, String id){
        Intent t = new Intent(c, DebugDeckStudyStateActivity.class);
        t.putExtra(ARG_STUDY_DECK_ID, id);
        return t;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String id = getIntent().getExtras().getString(ARG_STUDY_DECK_ID);
        mModel = ViewModelProviders.of(this).get(DebugStudyStateViewModel.class);
        mModel.setSourceStudyDeck(id);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_deck_study_state);
    }
}
