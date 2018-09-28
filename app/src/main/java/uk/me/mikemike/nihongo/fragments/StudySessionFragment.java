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
package uk.me.mikemike.nihongo.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.wanakanajava.WanaKanaJavaText;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import se.fekete.furiganatextview.furiganaview.FuriganaTextView;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.Card;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.utils.NihonGOUtils;
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

import static android.content.Context.TEXT_SERVICES_MANAGER_SERVICE;
import static uk.me.mikemike.nihongo.utils.NihonGOUtils.getJapaneseDisplay;

/**
 * Fragment that allows the user to study a session of cards
 */
public final class StudySessionFragment extends Fragment implements Observer<StudySession>, SpellCheckerSession.SpellCheckerSessionListener {


    public interface StudySessionFragmentListener {
        void onTestFinished(StudySession session);
    }


    private static String SHOWING_QUESION_BUNDLE_ID = "showing_question";

    private StudySessionFragmentListener mListener;
    private StudySessionViewModel mModel;
    private WanaKanaJavaText mWanaKana;
    private SpellCheckerSession mSpellcheckerSession = null;
    private String mLastAnswer=null;
    private String[] mLastAnswerWords=null;
    private Toast mLastToast;
    private NihonGOUtils.JapaneseDisplayMode mDisplayMode;
    private boolean mCurrentlyShowingQuestion=true;
    private Unbinder mUnbinder;


    @BindView(R.id.text_current_question)
    protected FuriganaTextView mQuestionText;
    @BindView(R.id.edittext_answer_input)
    protected EditText mAnswerEditText;
    @BindView(R.id.text_remaining_questions)
    protected TextView mRemainingQuestionsText;
    @BindString(R.string.format_remaining_questions)
    protected String mRemainingQuestionsFormatString;
    @BindView(R.id.button_answer)
    protected Button mAnswerButton;
    @BindView(R.id.studySessionView)
    protected ViewSwitcher mViewSwitcher;
    @BindView(R.id.layout_question)
    protected View mReviewRoot;
    @BindView(R.id.layout_details)
    protected View mDetailsRoot;
    @BindView(R.id.text_main_language)
    protected TextView mMainLanguageText;
    @BindView(R.id.text_japanese)
    protected FuriganaTextView mJapaneseText;


    public StudySessionFragment() {
        // Required empty public constructor
    }


    public static StudySessionFragment newInstance() {
        return new StudySessionFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);

        Activity activity = getActivity();
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mModel = ViewModelProviders.of(getActivity()).get(StudySessionViewModel.class);
        mModel.getCurrentSession().removeObserver(this);
        mModel.getCurrentSession().observe(this, this);
        mDisplayMode = NihonGOUtils.JapaneseDisplayMode.JAPANESE_DISPLAY;
        TextServicesManager textServicesManager = (TextServicesManager) activity.getSystemService(TEXT_SERVICES_MANAGER_SERVICE);

        // try to get the spell checker... this varies from device to device....
        mSpellcheckerSession =
                textServicesManager.newSpellCheckerSession(null, null, this, true);
        if(mSpellcheckerSession == null){
            mSpellcheckerSession= textServicesManager.newSpellCheckerSession(null, Locale.forLanguageTag("en"), this, false);
        }


        if(mSpellcheckerSession == null) {

            showToast(R.string.toast_cant_use_spellchecker);
        }

        if(savedInstance != null){
            mCurrentlyShowingQuestion = savedInstance.getBoolean(SHOWING_QUESION_BUNDLE_ID, true);
        }

        if(mCurrentlyShowingQuestion) {
            showCurrentQuestion();
        }
        else{
            showCurrentCardDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_study_session, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mAnswerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    answerQuestion(mAnswerEditText.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        mAnswerEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mWanaKana = new WanaKanaJavaText(mAnswerEditText,false);
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StudySessionFragmentListener) {
            mListener = (StudySessionFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SHOWING_QUESION_BUNDLE_ID, mCurrentlyShowingQuestion);
    }

    @Override
    public void onChanged(@Nullable StudySession studySession) {
        BindCurrentQuestion();
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfos) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        String currentTry="";
        SentenceSuggestionsInfo result = results[0];
        if(loopThroughTries(result, 0, currentTry)){
            Log.w("Spelling", "Spellchecker found correct word or phrase");
            showToast(R.string.toast_correct_with_spelling_errors);
        }
        else{
            Log.w("Spelling", "Spellchecker didn't find correct word or phrase");
            mModel.answerCurrentQuestion(mLastAnswer, true);
            showCurrentCardDetails();
            showToast(R.string.toast_wrong_answer);
        }

    }

    @OnClick(R.id.button_ok)
    protected void okButtonClick(){
        showCurrentQuestion();
    }

    @OnClick(R.id.button_answer)
    protected void answerQuestionClick(){
        String answer = mAnswerEditText.getText().toString();
        answerQuestion(answer);
        //BindCurrentQuestion();
    }


    private void showCurrentCardDetails() {
        Card current = getCurrentSessionFromModel().getPrevious().getSourceCard();
        if (current != null) {
            mMainLanguageText.setText(current.getMainLanguage());
            mJapaneseText.setFuriganaText(getJapaneseDisplay(current, mDisplayMode));
            if (mViewSwitcher.getCurrentView() != mDetailsRoot) {
                mViewSwitcher.showPrevious();
                mCurrentlyShowingQuestion = false;
            }
        }
    }


    private void showCurrentQuestion(){
        if(mViewSwitcher.getCurrentView() != mReviewRoot){
            mViewSwitcher.showNext();
            mCurrentlyShowingQuestion=true;
        }
    }



    private StudySession getCurrentSessionFromModel(){
        return mModel.getCurrentSession().getValue();
    }

    private boolean loopThroughTries(SentenceSuggestionsInfo info, int myIndex, String dest){

        SuggestionsInfo mine = info.getSuggestionsInfoAt(myIndex);
        String destCopy = dest;
        Log.w("spelling", "Suggestions:" + String.valueOf(mine.getSuggestionsCount()));
        if(mine.getSuggestionsCount() <= 0){
            destCopy += " " + mLastAnswerWords[myIndex];
            if(myIndex == info.getSuggestionsCount()-1){

                Log.w("Spelling", "Trying " + destCopy.trim());
                if(mModel.answerCurrentQuestion(destCopy.trim(), false))
               {

                   mModel.answerCurrentQuestion(destCopy.trim(), true);
                   return true;
               }
               else
               {
                   return false;
               }
            }
            else {
                return loopThroughTries(info, myIndex + 1, destCopy );
            }
        }
        else {
            for (int i = 0; i < mine.getSuggestionsCount(); i++) {
                String current = mine.getSuggestionAt(i);
                destCopy = dest + " " + current;
                if (myIndex == info.getSuggestionsCount()-1) {
                    Log.w("Spelling", "Trying " + destCopy.trim());
                    if(mModel.answerCurrentQuestion(destCopy.trim(), false))
                    {
                        mModel.answerCurrentQuestion(destCopy.trim(), true);
                        return true;
                    }
                } else {
                    if(loopThroughTries(info, myIndex + 1, destCopy)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void BindCurrentQuestion(){
        StudySession current = mModel.getCurrentSession().getValue();
        if(current != null) {
            mRemainingQuestionsText.setText(String.format(mRemainingQuestionsFormatString,current.getRemainingStudyCardsCount()));
            if (current.isFinished() == false) {
                String questionText;
                if(current.isCurrentQuestionJapaneseAnswer()){
                    mWanaKana.bind();
                    questionText = current.getCurrent().getSourceCard().getMainLanguage();
                }
                else
                {
                    mWanaKana.unbind();
                    questionText = getJapaneseDisplay(current.getCurrent().getSourceCard(), mDisplayMode);
                }
                mQuestionText.setFuriganaText(questionText);
                mAnswerEditText.setText("");
                mAnswerEditText.requestFocus();
            } else {
                showToast(R.string.toast_review_session_finished);
                mListener.onTestFinished(current);
            }

        }
    }

    private void answerQuestion(String answer){
        mLastAnswer = answer.replaceAll("\\s+", " ").trim();
        Log.w("spelling", "Answer after trim:" + mLastAnswer);
        if(!mLastAnswer.isEmpty()) {


            if (mModel.getCurrentSession().getValue().isCurrentQuestionJapaneseAnswer()) {
                checkJapaneseAnswer(mLastAnswer);
            } else {
                checkMainLanguageAnswer(mLastAnswer);
            }
        }
        else{
            showToast("no input");
        }
    }


    private void checkMainLanguageAnswer(String answer){
       // checking the main language answer is a bit more difficult as we need to invoke the spellchecker
        // to check for misspellings
        // first lets peek at the answer by telling the study session to not update itself
        // after checking the answer
        boolean correct = mModel.answerCurrentQuestion(answer, false);
        if(correct){
            // we are correct  just answer again this time telling the session to update
            mModel.answerCurrentQuestion(answer, true);
            showToast(R.string.toast_correct_answer);
        }
        else{
            // ok the answer was wrong but it could be due to a misspelling
            // lets try the spellchecker

            // do we have a spellchecker ?
            if(mSpellcheckerSession == null){
                // nope so sorry.. we just have to mark the answer as wrong
                mModel.answerCurrentQuestion(answer, true);
                showCurrentCardDetails();
                showToast(R.string.toast_wrong_answer);
            }
            else{
                Log.w("nihongo", "Answer was wrong testing for spelling errors");
                // yes so invoke it (this will call the onSentanceSugestions callback
                // see that method for handling the spellcheckers results )
                mLastAnswerWords= mLastAnswer.split(" ");
                mSpellcheckerSession.getSentenceSuggestions(new  TextInfo[]{new TextInfo(answer)}, 3);
                //mSpellcheckerSession.getSentenceSuggestions(w, 10);

            }

        }

    }

    private void checkJapaneseAnswer(String answer){
        mLastAnswer = answer;
        if(mModel.answerCurrentQuestion(answer, true)){
            showToast(R.string.toast_correct_answer);
        }
        else{
            showCurrentCardDetails();
            showToast(R.string.toast_wrong_answer);
        }
    }

    private void showToast(int messageID){
        handleShowingToast(Toast.makeText(getActivity(), messageID, Toast.LENGTH_SHORT));
    }

    private void showToast(String message){
        handleShowingToast(Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT));
    }

    private void handleShowingToast(Toast toast){
        if(mLastToast != null){
            mLastToast.cancel();
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        mLastToast = toast;
    }

    /**
     * Finishes the current test early
     */
    public void finishSession(){
        showToast(R.string.toast_review_session_canceled);
        mModel.finishCurrentSession();
    }




}
