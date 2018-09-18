package uk.me.mikemike.nihongo.viewmodels;

import android.app.Application;
import android.support.annotation.NonNull;
import java.util.Date;
import uk.me.mikemike.nihongo.model.StudyDeck;
import uk.me.mikemike.nihongo.model.StudySession;
import uk.me.mikemike.nihongo.utils.RealmObjectLiveData;

/**
 * Created by mike on 1/17/18.
 */

public class StudySessionViewModel extends BaseNihongoViewModel {

    protected RealmObjectLiveData<StudySession> mStudySession;

    public StudySessionViewModel(@NonNull Application application) {
        super(application);
        mStudySession = new RealmObjectLiveData<>(null);
    }

    public RealmObjectLiveData<StudySession> getCurrentSession(){
        return mStudySession;
    }

    public void createSession(String studyDeckID, Date date){
        StudyDeck sd = mRepos.getStudyDeckByID(studyDeckID);
        mStudySession.setValue(mRepos.createStudySession(sd, date));
    }


    public void loadSession(String studySessionID){
        mStudySession.setValue(mRepos.getStudySessionByID(studySessionID));
    }

    public boolean answerCurrentQuestion(String answer, boolean updateSessionState){
        return mRepos.answerStudySessionCurrentQuestion(answer, mStudySession.getValue(), updateSessionState);
    }


    public void finishCurrentSession(){
        mRepos.finishSession(mStudySession.getValue());
    }

    public void deleteCurrentSession(){
        if(mStudySession.getValue() != null) {
            mRepos.getConnectedRealm().beginTransaction();
            mStudySession.getValue().deleteFromRealm();
            mRepos.getConnectedRealm().commitTransaction();
            mStudySession.setValue(null);
        }
    }


}
