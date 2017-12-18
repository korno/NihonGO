package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Card;

/**
 * Created by mike on 12/11/17.
 */

public class DebugCardListAdapter extends RealmRecyclerViewAdapter<Card, DebugCardListAdapter.DebugCardListRecyclerView> {

    protected Context mContext;
    protected DebugCardListListener mListener;
    protected String mSynonymSeperator;

    public DebugCardListAdapter(Context c, String synonymSeperator, DebugCardListListener listener, @Nullable OrderedRealmCollection<Card> data, boolean autoUpdate) {
        super(data, autoUpdate);
        mContext=c;
        mListener=listener;
        mSynonymSeperator = synonymSeperator;
    }

    @Override
    public DebugCardListAdapter.DebugCardListRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View reycleView = LayoutInflater.from(mContext).inflate(R.layout.item_card_debug, null);
        return new DebugCardListRecyclerView(reycleView);

    }

    @Override
    public void onBindViewHolder(DebugCardListAdapter.DebugCardListRecyclerView holder, int position) {
        Card c= getItem(position);
        holder.bindCard(c);
    }



    public interface DebugCardListListener{
        void onCardClicked(Card c);
    }


    public class DebugCardListRecyclerView extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_main_language)
        protected TextView mTextMainLanguage;
        @BindView(R.id.text_japanese)
        protected TextView mTextJapanese;
        @BindView(R.id.text_japanese_hiragana)
        protected TextView mTextHiragana;
        @BindView(R.id.text_synonyms)
        protected TextView mTextSynonyms;



        public DebugCardListRecyclerView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardClicked(getItem(getAdapterPosition()));
        }

        public void bindCard(Card c){
            mTextJapanese.setText(c.getJapaneseKanji());
            mTextMainLanguage.setText(c.getMainLanguage());
            mTextHiragana.setText(c.getJapaneseHiragana());
            mTextSynonyms.setText(c.createSynonymsString(mSynonymSeperator));
        }
    }
}
