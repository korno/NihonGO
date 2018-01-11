package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    public StudyDeckListAdapter(Context context, StudyDeckAdapterHandler handler, @Nullable OrderedRealmCollection<StudyDeck> data, boolean autoUpdate) {
        super(data, autoUpdate);
        mContext = context;
        mHandler = handler;
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


    public interface StudyDeckAdapterHandler{
        void onReviewStudyDeckChosen(StudyDeck deck);
    }

    public class StudyDeckRecyclerView extends RecyclerView.ViewHolder{

        @BindView(R.id.text_studydeck_name)
        protected TextView mNameTextView;

        public StudyDeckRecyclerView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindToStudyDeck(StudyDeck deck){
            mNameTextView.setText(deck.getName());
        }

        @OnClick(R.id.button_start_review_session)
        protected void onReviewButtonClicked(){
            mHandler.onReviewStudyDeckChosen(getItem(getAdapterPosition()));
        }
    }
}
