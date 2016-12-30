package ru.beetlesoft.drawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.HashMap;

import ru.jehy.rutracker_free.R;

/**
 * Created by alond on 27.12.16.
 */

public class RutrackerDrawer {

    private static final long PROFILE_LOGIN = 10001;
    private static final long PROFILE_REGISTER = 10002;
    private static final long PROFILE_RESTORE = 10003;
    private static final long PROFILE_LOGOUT = 10004;
    private static final long PROFILE_ACCOUNT = 10005;
    private static final long PROFILE_PRIVATE_MESSAGES = 10006;
    private static final long PROFILE_MESSAGES = 10007;
    private static final long PROFILE_DISTRIBUTION = 10008;
    private static RutrackerDrawer instance = null;

    private Drawer drawer;
    private AccountHeader accountHeader = null;
    private WebView webView = null;
    private Activity activity = null;

    private ProfileSettingDrawerItem login;
    private ProfileSettingDrawerItem registration;
    private ProfileSettingDrawerItem restore;
    private ProfileSettingDrawerItem logout;

    private ProfileInfo profileInfo;
    private boolean isLogin;
    private ProfileSettingDrawerItem account;
    private ProfileSettingDrawerItem privateMessages;
    private ProfileSettingDrawerItem messages;
    private ProfileSettingDrawerItem distribution;

    private SparseArray<String> mainProfilesIds;

    private RutrackerDrawer(final Activity activity, Bundle savedInstanceState, final WebView webView) {
        this.mainProfilesIds = new SparseArray<>();
        this.webView = webView;
        this.activity = activity;

// Create the AccountHeader
        this.login = new ProfileSettingDrawerItem().withName("Вход")
                .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_user_circle_o).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_LOGIN);
        this.registration = new ProfileSettingDrawerItem().withName("Регистрация")
                .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_user_plus).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_REGISTER);
        this.restore = new ProfileSettingDrawerItem().withName("Забыли имя или пароль")
                .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_question).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_RESTORE);

        this.accountHeader = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(activity.getString(R.string.guest)).withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_user_circle_o).colorRes(R.color.material_drawer_dark_primary_icon)).withEmail("Войдите или зарегистрируйтесь"),
                        this.login,
                        this.registration,
                        this.restore
                )
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (profile != null && profile.getIdentifier() == PROFILE_REGISTER){
                            webView.loadUrl("http://rutracker.org/forum/profile.php?mode=register",null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_RESTORE){
                            webView.loadUrl("http://rutracker.org/forum/profile.php?mode=sendpassword",null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_LOGOUT){
                            webView.loadUrl("http://rutracker.org/logout.php",null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_LOGIN){
                            webView.loadUrl("http://rutracker.org/forum/login.php",null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_ACCOUNT){
                            webView.loadUrl(profileInfo.profileUrl,null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_PRIVATE_MESSAGES){
                            webView.loadUrl("http://rutracker.org/forum/privmsg.php?folder=inbox",null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_MESSAGES){
                            webView.loadUrl("http://rutracker.org/forum/search.php?uid="+profileInfo.getId(),null);
                        }
                        if (profile != null && profile.getIdentifier() == PROFILE_MESSAGES){
                            webView.loadUrl("http://rutracker.org/forum/tracker.php?rid="+profileInfo.getId(),null);
                        }
                        return false;
                    }
                })
                .withCurrentProfileHiddenInList(true)
                .build();

        this.drawer = new DrawerBuilder()
                .withActivity(activity)
                .withHeader(R.layout.guest_drawer_header)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withAccountHeader(accountHeader)
                .withToolbar((Toolbar) activity.findViewById(R.id.toolbar))
                .withDrawerLayout(R.layout.material_drawer)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        long id = drawerItem.getIdentifier();
                        String url = mainProfilesIds.get((int)id);
                        if (url.contains(".php")) {
                            url = "http://rutracker.org/forum/"+url;
                        }
                        webView.loadUrl(url, null);
                        return false;
                    }
                })
                .build();

        this.isLogin = false;
    }

    public static RutrackerDrawer getInstance(Activity activity, Bundle savedInstanceState, WebView webView) {
        if (instance == null) {
            instance = new RutrackerDrawer(activity, savedInstanceState, webView);
        }
        return instance;
    }
    public static RutrackerDrawer getInstance() {
        return instance;
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public void updateUserHeader(final ProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
        if (!isLogin) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logout = new ProfileSettingDrawerItem().withName("Выход")
                            .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_user_times).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_LOGOUT);
                    account = new ProfileSettingDrawerItem().withName("Профиль")
                            .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_user).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_ACCOUNT);
                    privateMessages = new ProfileSettingDrawerItem().withName("Личные сообщения")
                            .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_eye).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_PRIVATE_MESSAGES);
                    messages = new ProfileSettingDrawerItem().withName("Мои сообщения")
                            .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_envelope).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_MESSAGES);
                    distribution = new ProfileSettingDrawerItem().withName("Мои раздачи")
                            .withIcon(new IconicsDrawable(activity, FontAwesome.Icon.faw_envelope).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_DISTRIBUTION);


                    accountHeader.addProfiles(
                            account,
                            privateMessages,
                            messages,
                            distribution,
                            logout
                    );

                    accountHeader.removeProfile(login);
                    accountHeader.removeProfile(registration);
                    accountHeader.removeProfile(restore);

                    IProfile profile = accountHeader.getActiveProfile();
                    profile.withName(profileInfo.name);
                    accountHeader.updateProfile(profile);
                }
            });
            this.isLogin = true;
        }
    }
    public void updateGuestHeader() {
        if (isLogin) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    accountHeader.addProfiles(
                            login,
                            registration,
                            restore
                    );

                    accountHeader.removeProfile(account);
                    accountHeader.removeProfile(privateMessages);
                    accountHeader.removeProfile(messages);
                    accountHeader.removeProfile(distribution);
                    accountHeader.removeProfile(logout);

                    IProfile profile = accountHeader.getActiveProfile();
                    profile.withName(activity.getString(R.string.guest));
                    accountHeader.updateProfile(profile);
                }
            });
            this.isLogin = false;
        }
    }

    public void addItem(final String title, String url) {
        if (mainProfilesIds.indexOfValue(url) < 0) {
            final int id = mainProfilesIds.size() + 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawer.addItem(new ProfileDrawerItem().withName(title).withIdentifier(id));
                }
            });

            mainProfilesIds.append(id,url);
        }

    }

}
