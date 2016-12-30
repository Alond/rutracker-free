package ru.beetlesoft.drawer;

import android.net.Uri;

/**
 * Created by alond on 28.12.16.
 */

public class ProfileInfo {
    public String name;
    public String profileUrl;
    private String id = null;

    public String getId() {
        if (id == null) {
            Uri url = Uri.parse(profileUrl);
            id = url.getQueryParameter("u");
        }
        return id;
    }
}
