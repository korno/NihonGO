/**
 * NihonGO!
 * <p>
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of NihonGO nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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
package uk.me.mikemike.nihongo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.fragments.ChooseDeckToStudyFragment;
import uk.me.mikemike.nihongo.fragments.CurrentlyStudyingListFragment;
import uk.me.mikemike.nihongo.model.StudyDeck;

/* The Landing activity */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ChooseDeckToStudyFragment.ChooseDeckToStudyFragmentListener {

    public static String CURRENTLY_CHOOSING_DECK_BUNDLE_ID = "CurrentlyChoosingDeck";

    protected ChooseDeckToStudyFragment mFragmentChooseDeckToStudy;
    protected CurrentlyStudyingListFragment mFragmentCurrentlyStudying;
    protected Fragment mCurrentFragment=null;
    @BindString(R.string.snackbar_studydeck_created_format_string)
    protected String mDeckAddedSnackbackFormatString;


    // UI components
    @BindView(R.id.fab)
    protected FloatingActionButton mFAB;
    @BindView(R.id.toolbar)
    protected  Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    protected NavigationView mNavView;


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(CURRENTLY_CHOOSING_DECK_BUNDLE_ID, mCurrentFragment ==  mFragmentChooseDeckToStudy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               swapMainFragment(mFragmentChooseDeckToStudy, false, R.id.nav_add_new_study);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        mFragmentChooseDeckToStudy = ChooseDeckToStudyFragment.newInstance();
        mFragmentCurrentlyStudying = CurrentlyStudyingListFragment.newInstance();


        boolean showChooseDeckFragment =savedInstanceState == null ? false : savedInstanceState.getBoolean(CURRENTLY_CHOOSING_DECK_BUNDLE_ID, false);
        swapMainFragment(showChooseDeckFragment  ? mFragmentChooseDeckToStudy : mFragmentCurrentlyStudying, true,
                showChooseDeckFragment ? R.id.nav_add_new_study : R.id.nav_view_currently_studying);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if(mCurrentFragment != mFragmentCurrentlyStudying){
                swapMainFragment(mFragmentCurrentlyStudying, true, R.id.nav_view_currently_studying);
            }
            else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // we dont want the swapMainFragment to call the selectItem on the navigation drawer in this
        // case as the navigation drawer will handle it itself.
        if(id == R.id.nav_add_new_study){
           swapMainFragment(mFragmentChooseDeckToStudy, false, 0);
        }
        else if(id == R.id.nav_view_currently_studying){
            swapMainFragment(mFragmentCurrentlyStudying, true, 0);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected  void swapMainFragment(Fragment newFragment, boolean showFAB, int navigationDrawerID){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.layout_content_main, newFragment)
                .commit();
        mCurrentFragment = newFragment;
        mFAB.setVisibility(showFAB ? View.VISIBLE : View.GONE);
        if(navigationDrawerID != 0){
            mNavView.setCheckedItem(navigationDrawerID);
        }
    }

    @Override
    public void onStudyDeckCreated(final StudyDeck d) {
        Snackbar studyAddedBar = Snackbar.make(findViewById(android.R.id.content), String.format(mDeckAddedSnackbackFormatString, d.getName()), Snackbar.LENGTH_LONG);
        studyAddedBar.setAction(R.string.snackbar_studydeck_created_study_action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent studyIntent = StudySessionActivity.createIntent(MainActivity.this, d);
                startActivity(studyIntent);
            }
        });
        studyAddedBar.show();
    }
}
