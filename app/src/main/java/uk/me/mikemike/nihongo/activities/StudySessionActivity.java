/*
  NihonGO!
  <p>
  Copyright (c) 2018 Michael Hall <the.guitar.dude@gmail.com>
  All rights reserved.
  <p>
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  <p>
  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
  3. Neither the name of NihonGO nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.
  <p>
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
 */
package uk.me.mikemike.nihongo.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.fragments.StudySessionFragment;
import uk.me.mikemike.nihongo.fragments.StudySessionResultsFragment;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

/**
 * Activity for studying a set of reviews
 */
public final class StudySessionActivity extends AppCompatActivity implements StudySessionFragment.StudySessionFragmentListener {

    public final static  String ARG_STUDY_DECK_ID = "study_deck_id";
    private final static String ARG_STUDY_SESSION_ID = "study_session_id";
    private StudySessionViewModel mModel;
    private StudySessionFragment mStudySessionFragment;
    private StudySessionResultsFragment mResultsFragment;


    public static Intent createIntent(Context context, StudyDeck target){
        Intent i = new Intent(context, StudySessionActivity.class);
        i.putExtra(ARG_STUDY_DECK_ID, target.getStudyDeckID());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mStudySessionFragment = StudySessionFragment.newInstance();
        mResultsFragment = StudySessionResultsFragment.newInstance();
        mModel = ViewModelProviders.of(this).get(StudySessionViewModel.class);

        // we can load the session in one of three ways, in order of priority
        // 1) The session still exists in our StudySessionViewModel
        // 2) There is a session saved after the process was killed
        // 3) There is a studyDeck id parameter that has been passed to us
        //****
        // 1) will occur if the activity has been killed due to a screen rotation or something similar
        // 2) will occur if the app was put to the back and then destroyed by android to get some memory
        // 3) will occur if the activity is started for the first time

        String sessionID=null;

        // are we being restored?
        if(savedInstanceState != null){
            sessionID = savedInstanceState.getString(ARG_STUDY_SESSION_ID, null);

        }

        log("saved session id:" + sessionID);

        // grab the study deck id
        String studyDeckID = getIntent().getStringExtra(ARG_STUDY_DECK_ID);

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
                log("Loading saved sessiong from realm");
            } else {
                // check for 3, if this fails kill the activity as we have nothing to study
                if(studyDeckID == null){
                    finish();
                }
                else {
                    // (create session will delete any unfinished saved sessions on disc)
                    mModel.createSession(studyDeckID, new Date());
                    log("creating new session");
                }
            }
        }
        else{
            log("session present in existing view model");
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_session);

        if(mModel.getCurrentSession().getValue().isFinished()){
            displayResults();
        }
        else{
            displaySession();
        }

       log("Study Session count: " + mModel.getNumberOfStudySessions());
    }

    @Override
    public void onBackPressed() {
        // if the test is finished we dont care just let them go back
        if(mModel.getCurrentSession().getValue().isFinished()){
            super.onBackPressed();
        }
        else{
            // the test hasnt finished so we need to confirm their choice
            AlertDialog dialog = new
                    AlertDialog.Builder(this).setTitle(R.string.dialog_stop_study_session_title)
                    .setPositiveButton(R.string.dialog_stop_study_session_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mStudySessionFragment.finishSession();
                        }
                    })
                    .setNegativeButton(R.string.dialog_stop_study_session_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setMessage(R.string.dialog_stop_study_session_message).create();
            dialog.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        log("Saving state");
        outState.putString(ARG_STUDY_SESSION_ID, mModel.getCurrentSession().getValue().getStudySessionID());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTestFinished(StudySession session) {
        // no point in showing the results if there were no attempts
        if(session.getNumberOfAttempts() > 0) {
            displayResults();
        }
        else{
            finish();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            // only when the user actually means to leave the activity do we kill the session
           log("Deleting Session");
            mModel.deleteCurrentSession();
        }
    }

    private void displayResults(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.study_activity_layout_main, mResultsFragment)
                .commit();
    }

    private void displaySession(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.study_activity_layout_main, mStudySessionFragment)
                .commit();
    }


    private void log(String message){
        Log.w("StudySessionActivity", message);
    }

}
