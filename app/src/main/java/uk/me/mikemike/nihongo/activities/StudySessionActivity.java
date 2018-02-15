package uk.me.mikemike.nihongo.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.fragments.StudySessionFragment;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

public class StudySessionActivity extends AppCompatActivity implements StudySessionFragment.StudySessionFragmentListener {

    public final static  String ARG_STUDY_DECK_ID = "study_deck_id";
    public final static String ARG_STUDY_SESSION_ID = "study_session_id";

    protected StudySessionViewModel mModel;

    protected StudySessionFragment mStudySessionFragment;




    public static Intent createIntent(Context context, StudyDeck target){
        Intent i = new Intent(context, StudySessionActivity.class);
        i.putExtra(ARG_STUDY_DECK_ID, target.getStudyDeckID());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // we can load the session in one of three ways, in order of priority

        // 1) The session still exists in our StudySessionViewModel
        // 2) There is a session saved after the process was killed
        // 3) There is a studyDeck id parameter that has been passed to us


        // 1) will occur if the activity has been killed due to a screen rotation or something similar
        // 2) will occur if the app was put to the back and then destroyed by android to get some memory
        // 3) will occur if the activity is started for the first time

        String sessionID=null;

        // are we being restored?
        if(savedInstanceState != null){
            sessionID = savedInstanceState.getString(ARG_STUDY_SESSION_ID, null);
        }

        // grab the study deck it
        String studyDeckID = getIntent().getStringExtra(ARG_STUDY_DECK_ID);


        mModel = ViewModelProviders.of(this).get(StudySessionViewModel.class);

        // check for 1
        if(mModel.getCurrentSession().getValue() == null) {
            // check for 2
            if (sessionID != null) {
                // there was something saved, lets try to reload it
                mModel.loadSession(sessionID);
                if(mModel.getCurrentSession().getValue() == null){
                    // it wasnt saved properly so quit the activity
                    finish();
                }
            } else {
                // check for 3, if this fails kill the activity as we have nothing to study
                if(studyDeckID == null){
                    finish();
                }
                else {
                    mModel.createSession(studyDeckID, new Date());
                }
            }
        }

        // now we have the

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_session);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_STUDY_SESSION_ID, mModel.getCurrentSession().getValue().getStudySessionID());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTestFinished(StudySession session) {
        Toast.makeText(this, "Study Session finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            // only when the user actually means to leave the activity do we kill the session
            mModel.deleteCurrentSession();
        }
    }
}
