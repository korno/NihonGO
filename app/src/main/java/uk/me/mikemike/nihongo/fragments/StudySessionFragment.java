package uk.me.mikemike.nihongo.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.viewmodels.StudySessionViewModel;

public class StudySessionFragment extends Fragment implements Observer<StudySession> {

    private StudySessionFragmentListener mListener;
    private StudySessionViewModel mModel;


    @BindView(R.id.text_current_question)
    protected TextView mQuestionText;
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
        mModel = ViewModelProviders.of(getActivity()).get(StudySessionViewModel.class);
        mModel.getCurrentSession().removeObserver(this);
        mModel.getCurrentSession().observe(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_study_session, container, false);
        mUnbinder = ButterKnife.bind(this, v);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface StudySessionFragmentListener {
        // TODO: Update argument type and name
        void onTestFinished(StudySession session);
    }


    protected void BindCurrentQuestion(){
        Log.d("StudySessionFragment", "BindCurrentQuestionCalled");
        StudySession current = mModel.getCurrentSession().getValue();
        if(current != null) {
            mRemainingQuestionsText.setText(String.format(mRemainingQuestionsFormatString,current.getRemainingStudyCardsCount()));
            if (current.isFinished() == false) {
                mQuestionText.setText(current.getCurrent().getSourceCard().getMainLanguage());
                mAnswerEditText.setText("");
            } else {
                mListener.onTestFinished(current);
            }
        }
    }

    @OnClick(R.id.button_answer)
    protected void answerQuestion(){
        String answer = mAnswerEditText.getText().toString().trim();
        boolean correct = mModel.answerCurrentQuestion(answer);
        if(correct){
            Toast.makeText(getActivity(),"Correct!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(),"Wrong!", Toast.LENGTH_SHORT).show();
        }
        //BindCurrentQuestion();
    }
}
