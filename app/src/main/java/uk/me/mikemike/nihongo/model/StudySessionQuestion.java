package uk.me.mikemike.nihongo.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class StudySessionQuestion extends RealmObject {

    @Required
    @PrimaryKey
    protected  String mStudySessionQuestionID = UUID.randomUUID().toString();

    protected StudyCard mSourceCard;

    public int mMainLanguageScore=0;
    public int mJapaneseScore=0;
    public int mMainLanguageFirstAttemptScore=0;
    public int mJapaneseLanguageFirstAttemptScore=0;
    public boolean mHasShownNewCard;


    public boolean mIsNew() {return !(hasAnsweredMainLanguage() || hasAnsweredJapanese()) && mSourceCard.isNewCard();}


    public String getStudySessionQuestionID(){return mStudySessionQuestionID;}
    public boolean hasAnsweredMainLanguage(){return mMainLanguageScore != 0;}
    public boolean hasAnsweredJapanese(){return  mJapaneseScore != 0;}
    public boolean hasAnswered(){return hasAnsweredJapanese() && hasAnsweredMainLanguage();}
    public StudyCard getSourceCard(){return mSourceCard;}

    public boolean isCorrect(){ return mJapaneseScore ==  1  && mMainLanguageScore == 1;}
    public boolean isMainLanguageCorrect(){ return mMainLanguageScore == 1;}
    public boolean isJapaneseCorrect(){ return mJapaneseScore == 1;}


    public StudySessionQuestion(StudyCard source){
        mSourceCard = source;
    }

    public StudySessionQuestion(){

    }

    public boolean answerMainLanguage(String answer){
        boolean correct;
        if(mSourceCard.getSourceCard().isMainLanguageEqual(answer, true)){
            mMainLanguageScore=1;
            correct=true;
        }
        else{
            mMainLanguageScore=-1;
           correct=false;
        }
        if(mMainLanguageFirstAttemptScore == 0){
            mMainLanguageFirstAttemptScore = mMainLanguageScore;
        }
        return correct;
    }


    public boolean wasCorrectFirstTime(){
        return mMainLanguageFirstAttemptScore == 1 && mJapaneseLanguageFirstAttemptScore == 1;
    }

    public boolean wasJapaneseJapaneseCorrectFirstTime(){ return mJapaneseLanguageFirstAttemptScore == 1;}
    public boolean wasMainLanguageCorrectFirstTime(){return mMainLanguageFirstAttemptScore == 1;}

    public boolean answerJapanese(String answer){
        boolean correct;
        if(mSourceCard.getSourceCard().isHiraganaEqual(answer)){
            mJapaneseScore=1;
            correct=true;
        }
        else{
            mJapaneseScore=-1;
            correct=false;
        }
        if(mJapaneseLanguageFirstAttemptScore == 0){
            mJapaneseLanguageFirstAttemptScore=mJapaneseScore;
        }
        return correct;
    }

    public void showNewCard(){
        mHasShownNewCard=true;
    }

}
