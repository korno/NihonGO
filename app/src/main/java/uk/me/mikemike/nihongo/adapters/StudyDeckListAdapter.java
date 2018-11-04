/**
 * NihonGO!
 *
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of NihonGo nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.me.mikemike.nihongo.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.utils.DateUtils;

/**
 * Adapter for displaying a list of study decks, displaying a button to allow interaction if the studydeck has reviews
 * waiting.
 */
public class StudyDeckListAdapter extends RealmRecyclerViewAdapter<StudyDeck, StudyDeckListAdapter.StudyDeckRecyclerView> {

    private Context mContext;
    private StudyDeckAdapterHandler mHandler;
    private Date mDate;
    private SimpleDateFormat mDateFormatter;
    private ArrayList<String> mExpandedDetails = new ArrayList<>();

    @BindDrawable(R.drawable.ic_expand_less_black_24dp)
    protected Drawable mShowLessIcon;
    @BindDrawable(R.drawable.ic_expand_more_black_48dp)
    protected Drawable mShowMoreIcon;

    public StudyDeckListAdapter(Context context, StudyDeckAdapterHandler handler, @Nullable OrderedRealmCollection<StudyDeck> data, boolean autoUpdate,
                                        Date date) {
        super(data, autoUpdate);
        mContext = context;
        mHandler = handler;
        mDate = date;
        mDateFormatter = new SimpleDateFormat("MMM d, ''yyyy");
        new StudyDeckListAdapter_ViewBinding(this, context);
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
        notifyDataSetChanged();
    }


    public interface StudyDeckAdapterHandler{
        void onReviewStudyDeckChosen(StudyDeck deck);
        void onStopStudyingDeckChosen(StudyDeck deck);
        void onReviewStudyDeckChose10(StudyDeck deck, int reviewCount);
    }

    public class StudyDeckRecyclerView extends RecyclerView.ViewHolder{

        @BindView(R.id.text_studydeck_name)
        protected TextView mNameTextView;
        @BindView(R.id.button_start_review_session)
        protected Button mStartStudyButton;
        @BindView(R.id.button_start_review_session_10_reviews)
        protected Button mStartQuickStudyButton;
        @BindView(R.id.text_studying_details)
        protected TextView mTextViewStudyDeckDetails;
        @BindString(R.string.format_number_of_reviews)
        protected String mNumberOfReviewsFormatString;
        @BindString(R.string.format_studycard_detals)
        protected String mStudyDeckDetailsFormatString;
        @BindView(R.id.text_review_count)
        protected TextView mReviewCount;
        @BindView(R.id.progress_bar_cards_seen)
        protected ProgressBar mCardsSeenProgressBar;
        @BindString(R.string.format_next_studydate_label_days)
        protected String mNextStudyDateFormatStringDays;
        @BindString(R.string.format_next_studydate_label_hours)
        protected String mNextStudyDateFormatStringHours;
        @BindString(R.string.format_next_studydate_label_minutes)
        protected String mNextStudyDateIFormatStringMinutes;
        @BindView(R.id.progress_bar_mastered_amount)
        protected ProgressBar mMasteredCardsProgressBar;
        @BindView(R.id.text_cards_see_amount)
        protected TextView mCardsSeenTextView;
        @BindString(R.string.format_generic_x_of_y)
        protected String mXofYFormatString;
        @BindView(R.id.progress_bar_learning)
        protected ProgressBar mLearningProgressBar;
        @BindView(R.id.text_learning_amount)
        protected TextView mLearningTextView;

        @BindView(R.id.progress_bar_mastered_2)
        protected ProgressBar tmMasteredCardsDetailProgressBar;
        @BindView(R.id.text_mastered_amount)
        protected TextView mMasteredTextView;

        @BindView(R.id.layout_details)
        protected View mDetailsView;

        @BindView(R.id.image_show_details)
        protected ImageView mShowDetailsButton;

        @BindView(R.id.text_review_count_banana)
        protected TextView mTextViewReviewsCount;

        StudyDeckRecyclerView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindToStudyDeck(StudyDeck deck){
            mNameTextView.setText(deck.getName());
            int reviewsWaiting = deck.howManyReviewsWaiting(mDate);
            int numberOfStudyCards = deck.getNumberOfCards();
            int newCards = deck.getAllNewCards().size();
            Date nextStudyDate = deck.getNextStudyDate();
            long nextStudyInDays = DateUtils.getDayDifference(mDate, nextStudyDate);
            int masteredCardsPrecentage = deck.getMasteredCardPercentage();

            mTextViewReviewsCount.setText(String.valueOf(reviewsWaiting));

            // if we have reviews display how many, else display when the next review is due
            if(reviewsWaiting > 0) {
                //mReviewCount.setText(String.format(mNumberOfReviewsFormatString, reviewsWaiting));
                mReviewCount.setVisibility(View.GONE);
            }
            else{
                if(nextStudyInDays >= 1) {
                    mReviewCount.setText(String.format(mNextStudyDateFormatStringDays, nextStudyInDays));
                }
                else{

                    long diff = DateUtils.getHourDifference(mDate, nextStudyDate);
                    if(diff < 1){
                        diff = DateUtils.getMinuteDifference(mDate, nextStudyDate);
                        mReviewCount.setText(String.format(mNextStudyDateIFormatStringMinutes, diff));
                    }
                    else{
                        mReviewCount.setText(String.format(mNextStudyDateFormatStringHours, diff));
                    }


                }
            }
            mTextViewStudyDeckDetails.setText(String.format(mStudyDeckDetailsFormatString, numberOfStudyCards, mDateFormatter.format(deck.getStartedStudyDate())));

            mStartStudyButton.setEnabled(reviewsWaiting > 0);
            mStartQuickStudyButton.setEnabled(reviewsWaiting > 0 && reviewsWaiting > 9);

            mCardsSeenProgressBar.setProgress(100 - deck.getNewCardPercentage());
            mMasteredCardsProgressBar.setProgress(masteredCardsPrecentage);
            mCardsSeenTextView.setText(String.format(mXofYFormatString, numberOfStudyCards - newCards, numberOfStudyCards));
            mLearningProgressBar.setProgress(deck.getLearningCardPercentage());
            mLearningTextView.setText(String.format(mXofYFormatString,  deck.getNumberOfLearningCards(), numberOfStudyCards));
            mMasteredTextView.setText(String.format(mXofYFormatString, deck.getNumberOfMasteredCards(), numberOfStudyCards));
            tmMasteredCardsDetailProgressBar.setProgress(masteredCardsPrecentage);
            if(mExpandedDetails.contains(deck.getStudyDeckID())){
                mDetailsView.setVisibility(View.VISIBLE);
                mShowDetailsButton.setImageDrawable(mShowLessIcon);
            }
            else{
                mDetailsView.setVisibility(View.GONE);
                mShowDetailsButton.setImageDrawable(mShowMoreIcon);
            }
        }


        @OnClick({R.id.text_details, R.id.image_show_details })
        protected void expandDetails(){
            if(mDetailsView.getVisibility() == View.GONE){
                mDetailsView.setVisibility(View.VISIBLE);
                mShowDetailsButton.setImageDrawable(mShowLessIcon);
                mExpandedDetails.add(getItem(getAdapterPosition()).getStudyDeckID());
            }
            else{
                mDetailsView.setVisibility(View.GONE);
                mShowDetailsButton.setImageDrawable(mShowMoreIcon);
                mExpandedDetails.remove(getItem(getAdapterPosition()).getStudyDeckID());
            }
            notifyItemChanged(getAdapterPosition());
        }

        @OnClick(R.id.button_remove_study)
        protected void onStopStudyingButtonClicked(){
            mHandler.onStopStudyingDeckChosen(getItem(getAdapterPosition()));
        }

        @OnClick(R.id.button_start_review_session)
        protected void onReviewButtonClicked(){
            mHandler.onReviewStudyDeckChosen(getItem(getAdapterPosition()));
        }

        @OnClick(R.id.button_start_review_session_10_reviews)
        protected void onReviewButton10Clicked(){
            mHandler.onReviewStudyDeckChose10(getItem(getAdapterPosition()), 10);
        }
    }
}
