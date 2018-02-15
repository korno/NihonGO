package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.StudyDeck;

/**
 * Created by mike on 12/25/17.
 */
public class StudyDeckListAdapter extends RealmRecyclerViewAdapter<StudyDeck, StudyDeckListAdapter.StudyDeckRecyclerView> {

    protected Context mContext;
    protected StudyDeckAdapterHandler mHandler;
    protected Date mDate;


    public StudyDeckListAdapter(Context context, StudyDeckAdapterHandler handler, @Nullable OrderedRealmCollection<StudyDeck> data, boolean autoUpdate,
                                        Date date) {
        super(data, autoUpdate);
        mContext = context;
        mHandler = handler;
        mDate = date;
    }

    @Override
    public StudyDeckRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_current_studydeck, null, false);
        return new StudyDeckRecyclerView(v);
    }

    @Override
    public void onBindViewHolder(StudyDeckRecyclerView holder, int position) {
        holder.bindToStudyDeck(getItem(position));
    }


    public void setStudyDate(Date date){
        mDate = date;
    }


    public interface StudyDeckAdapterHandler{
        void onReviewStudyDeckChosen(StudyDeck deck);
    }

    public class StudyDeckRecyclerView extends RecyclerView.ViewHolder{

        @BindView(R.id.text_studydeck_name)
        protected TextView mNameTextView;
        @BindView(R.id.button_start_review_session)
        protected Button mStartStudyButton;
        @BindString(R.string.format_number_of_reviews)
        protected String mNumberOfReviewsFormatString;

        public StudyDeckRecyclerView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindToStudyDeck(StudyDeck deck){
            mNameTextView.setText(deck.getName());
            if(deck.hasReviewsWaiting(mDate)){
                mStartStudyButton.setVisibility(View.VISIBLE);
                mStartStudyButton.setText(String.format(mNumberOfReviewsFormatString, deck.getCardsWithNextReviewDateOlderThan(mDate).size()));
            }
            else{
                mStartStudyButton.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.button_start_review_session)
        protected void onReviewButtonClicked(){
            mHandler.onReviewStudyDeckChosen(getItem(getAdapterPosition()));
        }
    }
}
