package ru.jehy.rutracker_free;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import java.io.IOException;

import static ru.jehy.rutracker_free.RutrackerApplication.onionProxyManager;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public ShareActionProvider mShareActionProvider;

    private Drawer drawer;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //this.invalidateOptionsMenu();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
//        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_dialog_alert);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withToolbar((Toolbar) findViewById(R.id.toolbar))
                .withDrawerLayout(R.layout.material_drawer)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
                )
                .build();


//        drawer.getActionBarDrawerToggle().

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        RutrackerWebView myWebView = (RutrackerWebView) MainActivity.this.findViewById(R.id.myWebView);

        try {
            //TODO: onionProxyManager.isRunning is a surprisingly heavy operation and should not be done on main thread...
            if (!onionProxyManager.isRunning()) {
                new TorProgressTask(MainActivity.this).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String loaded = myWebView.getOriginalUrl();
        RutrackerApplication appState = ((RutrackerApplication) getApplicationContext());
        try {
            if (loaded == null && onionProxyManager.isRunning()) {
                myWebView.loadUrl(appState.currentUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        drawer.getActionBarDrawerToggle().syncState();
    }


    public void setShareIntent(final Intent shareIntent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(shareIntent);
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//                    WebView myWebView = (WebView) findViewById(R.id.myWebView);
//                    assert myWebView != null;
//                    if (myWebView.canGoBack()) {
//                        myWebView.goBack();
//                    } else {
//                        finish();
//                    }
//                    return true;
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}

