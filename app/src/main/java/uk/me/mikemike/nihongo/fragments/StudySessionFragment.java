package uk.me.mikemike.nihongo.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
    private Toast mLastToast;



    @BindView(R.id.text_current_question)
    protected FuriganaTextView mQuestionText;
    @BindView(R.id.edittext_answer_input)
    protected EditText mAnswerEditText;
    @BindView(R.id.text_remaining_questions)
    protected TextView mRemainingQuestionsText;
    @BindString(R.string.format_remaining_questions)
    protected String mRemainingQuestionsFormatString;
    protected Unbinder mUnbinder;

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
        mModel = ViewModelProviders.of(getActivity()).get(StudySessionViewModel.class);
        mModel.getCurrentSession().removeObserver(this);
        mModel.getCurrentSession().observe(this, this);

        mTextServices =
                (TextServicesManager) activity.getSystemService(TEXT_SERVICES_MANAGER_SERVICE);

        mSpellcheckerSession =
                mTextServices.newSpellCheckerSession(null, Locale.ENGLISH, this, false);
        if(mSpellcheckerSession == null){
            Toast.makeText(getActivity(),"Unable to load spellchecker. Be careful with your spelling!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_study_session, container, false);
        mUnbinder = ButterKnife.bind(this, v);
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
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfos) {
        //TODO: Implement dicttionary checking
        mModel.answerCurrentQuestion(mLastAnswer, true);
        showToast("Dictionary check here;");
        //Toast.makeText(getActivity(),"TODO - Implement spellchecker, now just marking as wrong answer", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_answer)
    protected void answerQuestionClick(){
        String answer = mAnswerEditText.getText().toString().trim();
        if(answer.isEmpty()){
            showToast("no input");
            return;
        }
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
                    questionText = getJapaneseDisplay(current.getCurrent().getSourceCard());
                }
                mQuestionText.setFuriganaText(questionText);
                mAnswerEditText.setText("");
            } else {
                mListener.onTestFinished(current);
            }
        }
    }

    // gets the correct japanese to display for the question based on the users preferences and
    // if the card actually contains that data
    protected String getJapaneseDisplay(Card card){
        if(card.hasDisplayJapanese()){
            return card.getJapaneseDisplay();
        }
        else{
            return card.getJapaneseHiragana();
        }
    }

    protected void answerQuestion(String answer){
        mLastAnswer = answer;
        if(mModel.getCurrentSession().getValue().isCurrentQuestionJapaneseAnswer()){
            checkJapaneseAnswer(answer);
        }
        else{
            checkMainLanguageAnswer(answer);
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
                mModel.answerCurrentQuestion(answer, true);
                showToast(R.string.toast_wrong_answer);
            }
            else{
                // yes so invoke it (this will call the onSentanceSugestions callback
                // see that method for handling the spellcheckers results )
                mSpellcheckerSession.getSentenceSuggestions(new TextInfo[]{new TextInfo(answer)}, 3);
            }

        }

    }


    protected void checkJapaneseAnswer(String answer){
        mLastAnswer = answer;
        if(mModel.answerCurrentQuestion(answer, true)){
            showToast(R.string.toast_correct_answer);
        }
        else{
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
        toast.show();
        mLastToast = toast;
    }




}
