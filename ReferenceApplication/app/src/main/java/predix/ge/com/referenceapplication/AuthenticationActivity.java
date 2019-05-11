/*
 * AuthenticationActivity
 * ReferenceApplication
 * <p>
 * Created by jeremyosterhoudt on 11/1/16.
 * Copyright © 2016 GE. All rights reserved.
 */
package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.ge.predix.mobile.core.AndroidMobileManager;
import com.ge.predix.mobile.core.ServiceRouterWebViewClient;


public class AuthenticationActivity extends Activity {


    public static final int AUTHENTICATION_DECLINED = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);
        WebView webView = (WebView) findViewById(R.id.authenticationWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setWebViewClient(new ServiceRouterWebViewClient());
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public void onBackPressed() {
        setResult(AUTHENTICATION_DECLINED, this.getIntent());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebView authWebView = (WebView) findViewById(R.id.authenticationWebView);
        if (authWebView != null) {
            authWebView.setWebViewClient(null);
            authWebView.removeAllViews();
            authWebView.destroy();
        }
    }
}
