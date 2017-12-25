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
 * 3. Neither the name of mosquitto nor the names of its
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
package uk.me.mikemike.nihongo.debug;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import io.realm.Realm;
import uk.me.mikemike.nihongo.data.NihongoRealmConfiguration;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.data.XmlImporter;
import uk.me.mikemike.nihongo.viewmodels.NihongoViewModel;

/**
 * DebugActivity used to list all decks present in the realm and some debug information on them
 */
public class DebugDeckListActivity extends AppCompatActivity {

    protected NihongoViewModel mModel;
    protected Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mModel = ViewModelProviders.of(this).get(NihongoViewModel.class);
        NihongoRealmConfiguration.configureNihongoRealm(this);
        mRealm = Realm.getDefaultInstance();
        mModel.init();
        XmlImporter importer = new XmlImporter(mRealm,getResources().getXml(R.xml.testxml),
                "", "", 0);
        importer.importData();
        mModel.startStudying(mModel.getAllDecks().getValue().last());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_deck_list);
    }

    @Override
    protected  void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }
}
