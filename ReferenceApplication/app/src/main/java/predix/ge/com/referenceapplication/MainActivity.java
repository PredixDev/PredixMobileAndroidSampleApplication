package predix.ge.com.referenceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.ge.predix.mobile.core.AndroidAuthHandler;
import com.ge.predix.mobile.core.AndroidMobileManager;
import com.ge.predix.mobile.core.AuthHandler;
import com.ge.predix.mobile.core.PredixMobileConfiguration;
import com.ge.predix.mobile.core.ServiceRouterWebViewClient;
import com.ge.predix.mobile.core.ViewInterface;
import com.ge.predix.mobile.exceptions.InitializationException;
import com.ge.predix.mobile.logging.LoggingLevel;
import com.ge.predix.mobile.logging.PredixSDKLogger;
import com.ge.predix.mobile.platform.PlatformContext;
import com.ge.predix.mobile.platform.WindowView;
import com.ge.predix.mobile.sdk.android.context.PredixAndroidContext;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PredixMobileConfiguration.baseAssetFolderPath = "android_asset";

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);

        try {
            CookieManager.getInstance().removeAllCookies(null);
            PredixSDKLogger.setLoggingLevel(LoggingLevel.trace);
            AndroidMobileManager instance = AndroidMobileManager.instance;
            instance.initialize(this, buildViewInterface());
            instance.start();
            webView.setWebViewClient(new ServiceRouterWebViewClient(webView));
        } catch (InitializationException e) {
            PredixSDKLogger.error(this, "could not initialize application", e);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidMobileManager instance = AndroidMobileManager.instance(this, buildViewInterface());
        try {
            instance.stop();
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ViewInterface buildViewInterface() {
        final MainActivity activity = this;
        final Properties properties = new Properties();
        try {
            properties.load(this.getAssets().open("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ViewInterface() {

            private AndroidAuthHandler androidAuthHandler;

            @Override
            public PlatformContext getContext() {
                return new PredixAndroidContext(MainActivity.this);
            }

            @Override
            public Map<String, Object> getProperties() {
                return (Map) properties;
            }

            @Override
            public AuthHandler getAuthHandler() {
                if (null == androidAuthHandler) {
                    androidAuthHandler = new AndroidAuthHandler(new AuthenticationManager(activity));
                }
                return androidAuthHandler;
            }

            @Override
            public WindowView getWindowView() {
                return new MobileWebView(webView);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    // This method is called once the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // Launch settings activity
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }
}
