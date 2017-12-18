package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.LearningState;
import uk.me.mikemike.nihongo.model.StudyCard;

/**
 * Created by mike on 12/18/17.
 */

public class DebugStudyCardListAdapter  extends RealmRecyclerViewAdapter<StudyCard, DebugStudyCardListAdapter.DebugStudyCardListReyclerView> {

    protected Context mContext;

    @BindString(R.string.format_next_study_date)
    protected String mDateFormat;
    protected SimpleDateFormat mFormatter;
    
    public DebugStudyCardListAdapter(Context context, String dateFormat, @Nullable OrderedRealmCollection<StudyCard> data, boolean autoUpdate) {
        super(data, autoUpdate);
        mContext = context;
        mFormatter = new SimpleDateFormat(dateFormat);

    }

    @Override
    public DebugStudyCardListReyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View reycleView = LayoutInflater.from(mContext).inflate(R.layout.item_study_card_debug, null);
        return new DebugStudyCardListReyclerView(reycleView);
    }

    @Override
    public void onBindViewHolder(DebugStudyCardListReyclerView holder, int position) {
        holder.bindStudyCard(getItem(position));
    }

    public class DebugStudyCardListReyclerView extends RecyclerView.ViewHolder{

        @BindView(R.id.text_e_number)
        protected TextView mTextENumber;
        @BindView(R.id.text_next_study_date)
        protected TextView mTextNextStudyDate;
        @BindView(R.id.text_japanese_display)
        protected TextView mTextJapanese;
        @BindView(R.id.text_main_language)
        protected TextView mTextMainLanguage;
        @BindView(R.id.text_japanese_hiragana)
        protected TextView mTextJapaneseHiragana;
        @BindView(R.id.text_current_reps)
        protected TextView mTextReps;
        @BindView(R.id.text_interval)
        protected TextView mTextInterval;


        public DebugStudyCardListReyclerView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindStudyCard(StudyCard s){

            LearningState state = s.getLearningState();
            mTextENumber.setText(String.valueOf(state.getEasyness()));
            mTextNextStudyDate.setText(mFormatter.format(state.getNextDueDate()));
            mTextReps.setText(String.valueOf(state.getReps()));
            mTextInterval.setText(String.valueOf(state.getInterval()));

            Card sc = s.getSourceCard();
            mTextJapanese.setText(sc.getJapaneseDisplayIfPresentKanjiIfNot());
            mTextJapaneseHiragana.setText(sc.getJapaneseHiragana());
            mTextMainLanguage.setText(sc.getMainLanguage());

        }
    }
}
