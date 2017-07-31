package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

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
    private AuthHandlerCallback authHandlerCallback;
    public static final int AUTHENTICATION_FINISHED = 1;

    public AuthenticationHandler(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void showAuthenticationUI(String url, AuthHandlerCallback authHandlerCallback) {
        Intent intent = new Intent(parentActivity, AuthenticationActivity.class);
        intent.putExtra("url", url);
        parentActivity.startActivityForResult(intent, 999);
        this.authHandlerCallback = authHandlerCallback;
    }

    @Override
    public void hideAuthenticationUI() {
        parentActivity.setResult(AUTHENTICATION_FINISHED);
        parentActivity.finishActivity(999);
    }

    public void authenticationResult(int resultCode, String resultStringData) {
        Log.d(getClass().getName(), "Authentication result " + resultCode + ": " + resultStringData);
        if (resultCode!=AUTHENTICATION_FINISHED) {
            authHandlerCallback.authenticationEncounteredError("Incomplete authentication");
        }
    }
}
