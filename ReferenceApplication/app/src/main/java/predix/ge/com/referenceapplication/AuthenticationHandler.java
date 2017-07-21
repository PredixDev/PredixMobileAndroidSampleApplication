package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.content.Intent;

import com.ge.predix.mobile.core.AuthHandler;
import com.ge.predix.mobile.core.AuthHandlerCallback;

/**
 * AuthenticationManager
 * ReferenceApplication
 * <p>
 * Created by jeremyosterhoudt on 11/1/16.
 * Copyright © 2016 GE. All rights reserved.
 */
public class AuthenticationHandler implements AuthHandler {

    private final Activity parentActivity;

    public AuthenticationHandler(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void showAuthenticationUI(String url, AuthHandlerCallback authHandlerCallback) {
        Intent intent = new Intent(parentActivity, AuthenticationActivity.class);
        intent.putExtra("url", url);
        parentActivity.startActivityForResult(intent, 999);
    }

    @Override
    public void hideAuthenticationUI() {
        parentActivity.finishActivity(999);
    }
}
