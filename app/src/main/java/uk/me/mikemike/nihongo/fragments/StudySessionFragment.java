package uk.me.mikemike.nihongo.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
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
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

import static android.content.Context.TEXT_SERVICES_MANAGER_SERVICE;

public class StudySessionFragment extends Fragment implements Observer<StudySession>, SpellCheckerSession.SpellCheckerSessionListener {

    private StudySessionFragmentListener mListener;
    private StudySessionViewModel mModel;
    private WanaKanaJavaText mWanaKana;
    private TextServicesManager mTextServices = null;
    private SpellCheckerSession mSpellcheckerSession = null;
    private String mLastAnswer=null;
    private String[] mLastAnswerWords=null;
    private Toast mLastToast;

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
    protected Unbinder mUnbinder;

    @BindView(R.id.studySessionView)
    protected ViewSwitcher mViewSwitcher;

    @BindView(R.id.layout_question)
    protected View mReviewRoot;
    @BindView(R.id.layout_details)
    protected View mDetailsRoot;



    protected JapaneseDisplayMode mDisplayMode;

    public enum JapaneseDisplayMode { JAPANESE_DISPLAY, KANJI_DISPLAY, KANA};




    public StudySessionFragment() {
        // Required empty public constructor
    }


    public static StudySessionFragment newInstance() {
        StudySessionFragment fragment = new StudySessionFragment();
        return fragment;
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
        mDisplayMode = JapaneseDisplayMode.JAPANESE_DISPLAY;
        mTextServices =
                (TextServicesManager) activity.getSystemService(TEXT_SERVICES_MANAGER_SERVICE);

        // try to get the spell checker... this varies from device to device....
        mSpellcheckerSession =
                mTextServices.newSpellCheckerSession(null, null, this, true);
        if(mSpellcheckerSession == null){
            mSpellcheckerSession= mTextServices.newSpellCheckerSession(null, Locale.forLanguageTag("en"), this, false);
        }


        if(mSpellcheckerSession == null) {

            showToast(R.string.toast_cant_use_spellchecker);
        }

        showCurrentQuestion();
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
    public void onChanged(@Nullable StudySession studySession) {
        BindCurrentQuestion();
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfos) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {

        Log.w("nihongo", "received suggestions, checking");
        String currentTry="";
        SentenceSuggestionsInfo result = results[0];

        Log.w("nihongo", "number of results:" + String.valueOf(results.length));


        /*final StringBuffer sb = new StringBuffer("");
        for(SentenceSuggestionsInfo result:results){
            int n = result.getSuggestionsCount();
            for(int i=0; i < n; i++){
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();

                for(int k=0; k < m; k++) {
                    sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                            .append("\n");
                }
                sb.append("\n");
            }
        }*/

        //Log.w("spelling", sb.toString());


        if(loopThroughTries(result, 0, currentTry)){
            Log.w("Spelling", "Spellchecker found correct word or phrase");
            showToast(R.string.toast_correct_with_spelling_errors);
        }
        else{
            Log.w("Spelling", "Spellchecker didn't find correct word or phrase");
            showCurrentCardDetails();
            mModel.answerCurrentQuestion(mLastAnswer, true);
            showToast(R.string.toast_wrong_answer);
        }

    }


    protected void showCurrentCardDetails(){
        if(mViewSwitcher.getCurrentView() != mDetailsRoot){
            mViewSwitcher.showPrevious();
        }
    }


    protected void showCurrentQuestion(){
        if(mViewSwitcher.getCurrentView() != mReviewRoot){
            mViewSwitcher.showNext();
        }
    }


    protected boolean loopThroughTries(SentenceSuggestionsInfo info, int myIndex, String dest){

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
                    else{
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


    public interface StudySessionFragmentListener {
        void onTestFinished(StudySession session);
    }


    protected void BindCurrentQuestion(){
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
                    questionText = StudySessionFragment.getJapaneseDisplay(current.getCurrent().getSourceCard(), mDisplayMode);
                }
                mQuestionText.setFuriganaText(questionText);
                mAnswerEditText.setText("");
                mAnswerEditText.requestFocus();
            } else {
                mListener.onTestFinished(current);
            }

        }
    }

    // gets the correct japanese to display for the question based on the users preferences and
    // if the card actually contains that data
    protected  static  String getJapaneseDisplay(Card card, JapaneseDisplayMode mode){
        String japanese="";
        switch(mode){
            case KANA:
                japanese = card.getJapaneseHiragana();
                break;
            case KANJI_DISPLAY:
                japanese = card.hasKanji() ? card.getJapaneseKanji() : card.getJapaneseHiragana();
                break;
            case JAPANESE_DISPLAY:
                japanese = card.hasDisplayJapanese() ? card.getJapaneseDisplay() : card.getJapaneseHiragana();
        }
        return japanese;
    }

    protected void answerQuestion(String answer){
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



    protected void checkMainLanguageAnswer(String answer){
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
                showCurrentCardDetails();
                mModel.answerCurrentQuestion(answer, true);
                showToast(R.string.toast_wrong_answer);
            }
            else{
                Log.w("nihongo", "Answer was wrong testing for spelling errors");
                // yes so invoke it (this will call the onSentanceSugestions callback
                // see that method for handling the spellcheckers results )
                mLastAnswerWords= mLastAnswer.split(" ");
                TextInfo w[] = new TextInfo[mLastAnswerWords.length];
                for(int i=0; i<mLastAnswerWords.length; i++){
                    w[i] = new TextInfo((mLastAnswerWords[i]));
                }


                mSpellcheckerSession.getSentenceSuggestions(new  TextInfo[]{new TextInfo(answer)}, 5);
                //mSpellcheckerSession.getSentenceSuggestions(w, 10);

            }

        }

    }


    protected void checkJapaneseAnswer(String answer){
        mLastAnswer = answer;
        if(mModel.answerCurrentQuestion(answer, true)){
            showToast(R.string.toast_correct_answer);
        }
        else{
            showCurrentCardDetails();
            showToast(R.string.toast_wrong_answer);
        }
    }


    protected void showToast(int messageID){
        handleShowingToast(Toast.makeText(getActivity(), messageID, Toast.LENGTH_SHORT));
    }

    protected void showToast(String message){
        handleShowingToast(Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT));
    }

    protected void handleShowingToast(Toast toast){
        if(mLastToast != null){
            mLastToast.cancel();
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        mLastToast = toast;
    }


    public void finishSession(){
        mModel.finishCurrentSession();
    }




}
