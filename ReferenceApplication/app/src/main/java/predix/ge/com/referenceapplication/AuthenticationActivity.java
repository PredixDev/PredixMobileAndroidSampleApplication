package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.ge.predix.mobile.core.AndroidMobileManager;
import com.ge.predix.mobile.core.ServiceRouterWebViewClient;

import java.io.ByteArrayInputStream;

/**
 * AuthenticationActivity
 * ReferenceApplication
 * <p>
 * Created by jeremyosterhoudt on 11/1/16.
 * Copyright © 2016 GE. All rights reserved.
 */
public class AuthenticationActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.authentication_activity);
        WebView webView = (WebView) findViewById(R.id.authenticationWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new ServiceRouterWebViewClient(webView) {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!AndroidMobileManager.urlIsAuthenticateRedirect(request.getUrl().toString())) {
                    return super.shouldInterceptRequest(view, request);
                } else {
                    return new WebResourceResponse("text/html","UTF-8", new ByteArrayInputStream("".getBytes()));
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !AndroidMobileManager.urlIsAuthenticateRedirect(url) && super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
