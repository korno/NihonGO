package uk.me.mikemike.nihongo.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.realm.Realm;
import uk.me.mikemike.nihongo.NihongoRealmConfiguration;
import uk.me.mikemike.nihongo.data.NihongoRepository;

/**
 * Created by mike on 12/18/17.
 */

public class BaseNihongoViewModel extends AndroidViewModel {

    protected Realm mRealm;
    protected NihongoRepository mRepos;

    public BaseNihongoViewModel(@NonNull Application application) {
        super(application);
        NihongoRealmConfiguration.configureNihongoRealm(this.getApplication());
        mRealm = Realm.getDefaultInstance();
        mRepos = new NihongoRepository(mRealm);
    }

    @Override
    public void onCleared() {
        mRealm.close();
    }
}
