package uk.me.mikemike.nihongo.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindString;
import butterknife.ButterKnife;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Deck;
import uk.me.mikemike.nihongo.viewmodels.DebugCardListViewModel;

public class DebugCardListActivity extends AppCompatActivity implements Observer<Deck> {

    public final static  String ARG_DECK_ID = "deck_id";
    protected DebugCardListViewModel mModel;
    @BindString(R.string.activity_debug_card_list_title)
    protected String mTitleFormatString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String cardID = getIntent().getExtras().getString(ARG_DECK_ID);
        ButterKnife.bind(this);
        mModel = ViewModelProviders.of(this).get(DebugCardListViewModel.class);
        mModel.setSourceDeck(cardID);
        mModel.getSourceDeck().observe(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_card_list);
    }

    @Override
    public void onChanged(@Nullable Deck deck) {
        setTitle(String.format(mTitleFormatString,deck.getName()));
    }
}
