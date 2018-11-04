package uk.me.mikemike.nihongo.viewmodels;

import android.app.Application;
import android.support.annotation.NonNull;
import java.util.Date;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.model.StudySessionMultiple;
import uk.me.mikemike.nihongo.utils.RealmObjectLiveData;

/**
 * Created by mike on 1/17/18.
 */

public class StudySessionViewModel extends BaseNihongoViewModel {

    protected RealmObjectLiveData<StudySessionMultiple> mStudySession;

    public StudySessionViewModel(@NonNull Application application) {
        super(application);
        mStudySession = new RealmObjectLiveData<>(null);
    }

    public RealmObjectLiveData<StudySessionMultiple> getCurrentSession(){
        return mStudySession;
    }

    public void createSession(String studyDeckID, Date date){
        // we can only ever have one active session
        deleteAllSessions();
        StudyDeck sd = mRepos.getStudyDeckByID(studyDeckID);
        mStudySession.setValue(mRepos.createMultipleStudySession(sd, date));
    }

    public void createSession(String studyDeckID, Date date, int maxReviews){
        deleteAllSessions();
        StudyDeck sd = mRepos.getStudyDeckByID(studyDeckID);
        mStudySession.setValue(mRepos.createMultipleStudySession(sd, date, maxReviews));
    }


    public void loadSession(String studySessionID){
        mStudySession.setValue(mRepos.getStudySessionMultipleByID(studySessionID));
    }

    public boolean answerCurrentQuestion(String answer, boolean updateSessionState){
        return mRepos.answerStudySessionCurrentQuestion(answer, mStudySession.getValue(), updateSessionState);
    }


    public void finishCurrentSession(){
        mRepos.finishSession(mStudySession.getValue());
    }

    public void deleteCurrentSession(){
        if(mStudySession.getValue() != null) {
            deleteAllSessions();
            mStudySession.setValue(null);
        }
    }




    public void deleteAllSessions(){
        mRepos.deleteAllStudySessions();
    }


    public int getNumberOfStudySessions(){
        return mRepos.getAllStudySessions().size();
    }


}
