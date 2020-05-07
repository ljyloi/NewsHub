package com.java.qdlljy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.java.qdlljy.MyFragments.HistoryFragment;
import com.java.qdlljy.MyFragments.MainFragment;
import com.java.qdlljy.MyFragments.CollectionFragment;
import com.google.android.material.navigation.NavigationView;
import com.java.qdlljy.R;
import com.trs.channellib.channel.channel.helper.ChannelDataHelepr;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private List<Fragment> fragmentList;
    ChannelDataHelepr<MyChannel> dataHelepr;
    int prepos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
//
        toolbar.setTitle("NewsHub");
        toolbar.setSubtitle("All you need is HAND.");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

//        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));

        prepos = 0;
        fragmentList = new ArrayList<>();
        buildFragmentList();
        setDefaultFragment(prepos);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StateMentWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                swichFragment(0);
                break;
            case R.id.nav_history:
                swichFragment(1);
                break;
            case R.id.nav_collection:
                swichFragment(2);
                break;
//            case R.id.nav_culture:
//                break;
            default:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                MyApplication.newsOperator.DeleteAllReadNews();
                break;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDefaultFragment(int pos) {
        Fragment now = fragmentList.get(pos);
        if (!now.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, fragmentList.get(prepos))
                    .commit();
        }
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(now)
                    .commit();
        }
    }

    private void buildFragmentList() {
        MainFragment mainFragment = new MainFragment();
        HistoryFragment historyFragment = new HistoryFragment();
        CollectionFragment collectionFragment = new CollectionFragment();
        fragmentList.add(mainFragment);
        fragmentList.add(historyFragment);
        fragmentList.add(collectionFragment);
    }

    private void swichFragment(int pos) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment from  = fragmentList.get(prepos);
        Fragment to = fragmentList.get(pos);
        if (pos == prepos) return;
        if (!to.isAdded()) {
            ft.hide(from)
                    .add(R.id.main_frame, fragmentList.get(pos))
                    .commit();
        }
        else {
            ft.hide(from)
                    .show(to)
                    .commit();
        }
        prepos = pos;

    }
}
