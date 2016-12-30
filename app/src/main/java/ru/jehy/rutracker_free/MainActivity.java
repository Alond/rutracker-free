package ru.jehy.rutracker_free;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.io.IOException;

import ru.beetlesoft.drawer.RutrackerDrawer;

import static ru.jehy.rutracker_free.RutrackerApplication.onionProxyManager;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public ShareActionProvider mShareActionProvider;

    private WebView webView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //this.invalidateOptionsMenu();

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String js = "try{ effroi.mouse.paste($('#search-text-guest')[0],'"+query+"');" +
                        "effroi.mouse.click($('#cse-search-btn-top')[0]);}catch(e){" +
                        "effroi.mouse.paste($('#search-text')[0],'"+query+"');" +
                        "$('#search-text').parent().submit();}";
                webView.loadUrl("javascript:"+js);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

        webView = (WebView) findViewById(R.id.webView);
        RutrackerDrawer.getInstance(this, savedInstanceState,webView);

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        RutrackerWebView myWebView = (RutrackerWebView) MainActivity.this.findViewById(R.id.webView);

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
        outState = RutrackerDrawer.getInstance().getDrawer().saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                RutrackerDrawer.getInstance().getDrawer().openDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (RutrackerDrawer.getInstance().getDrawer().isDrawerOpen()) {
            RutrackerDrawer.getInstance().getDrawer().closeDrawer();
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}

