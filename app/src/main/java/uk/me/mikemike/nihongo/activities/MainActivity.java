package uk.me.mikemike.nihongo.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.me.mikemike.nihongo.R;
import uk.me.mikemike.nihongo.fragments.ChooseDeckToStudyFragment;
import uk.me.mikemike.nihongo.fragments.CurrentlyStudyingListFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected ChooseDeckToStudyFragment mFragmentChooseDeckToStudy;
    protected CurrentlyStudyingListFragment mFragmentCurrentlyStudying;

    protected Fragment mCurrentFragment=null;

    @BindView(R.id.fab)
    protected FloatingActionButton mFAB;
    @BindView(R.id.toolbar)
    protected  Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavView;


    protected  void swapMainFragment(Fragment newFragment, boolean showFAB){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.layout_content_main, newFragment)
                .commit();
        mCurrentFragment = newFragment;
        mFAB.setVisibility(showFAB == true ? View.VISIBLE : View.GONE);
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
               swapMainFragment(mFragmentChooseDeckToStudy, false);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        mFragmentChooseDeckToStudy = ChooseDeckToStudyFragment.newInstance();
        mFragmentCurrentlyStudying = CurrentlyStudyingListFragment.newInstance();
        swapMainFragment(mFragmentCurrentlyStudying, true);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if(mCurrentFragment != mFragmentCurrentlyStudying){
                swapMainFragment(mFragmentCurrentlyStudying, true);
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
        if(id == R.id.nav_add_new_study){
           swapMainFragment(mFragmentChooseDeckToStudy, false);
        }
        else if(id == R.id.nav_view_currently_studying){
            swapMainFragment(mFragmentCurrentlyStudying, true);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
