package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.content.Intent;

import com.ge.predix.mobile.core.AuthenticationHandler;

/**
 * AuthenticationManager
 * ReferenceApplication
 * <p>
 * Created by jeremyosterhoudt on 11/1/16.
 * Copyright © 2016 GE. All rights reserved.
 */
public class AuthenticationManager implements AuthenticationHandler {

    private final Activity parentActivity;

    public AuthenticationManager(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void showAuthenticationUI(String url) {
        Intent intent = new Intent(parentActivity, AuthenticationActivity.class);
        intent.putExtra("url", url);
        parentActivity.startActivityForResult(intent, 999);
    }

    @Override
    public void hideAuthenticationUI() {
        parentActivity.finishActivity(999);
    }
}
