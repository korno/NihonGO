package uk.me.mikemike.nihongo.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.StudyDeck;

public class StudySessionActivity extends AppCompatActivity {

    public final static  String ARG_STUDY_DECK_ID = "study_deck_id";

    public static Intent createIntent(Context context, StudyDeck target){
        Intent i = new Intent(context, StudySessionActivity.class);
        i.putExtra(ARG_STUDY_DECK_ID, target.getStudyDeckID());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_session);

    }
}
