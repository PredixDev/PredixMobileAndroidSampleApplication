package predix.ge.com.referenceapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.ge.predix.mobile.core.AndroidMobileManager;
import com.ge.predix.mobile.core.AuthHandler;
import com.ge.predix.mobile.core.PredixMobileConfiguration;
import com.ge.predix.mobile.core.ServiceRouterWebViewClient;
import com.ge.predix.mobile.core.VersionAddendumProvider;
import com.ge.predix.mobile.core.VersionInfo;
import com.ge.predix.mobile.core.ViewInterface;
import com.ge.predix.mobile.exceptions.InitializationException;
import com.ge.predix.mobile.logging.LoggingLevel;
import com.ge.predix.mobile.logging.PredixSDKLogger;
import com.ge.predix.mobile.platform.PlatformContext;
import com.ge.predix.mobile.platform.WindowView;
import com.ge.predix.mobile.sdk.android.context.PredixAndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.ge.predix.mobile.platform.PredixConstants.VersionInfoKeys.CONTAINER_VERSION_CODE;
import static com.ge.predix.mobile.platform.PredixConstants.VersionInfoKeys.CONTAINER_VERSION_NAME;


public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        initiatePredixMobile("android_asset", this.webView);
    }

    /**
     * Initializes the Predix-Mobile SDK.
     * @param baseAssetFolderPath
     * @param webView
     */
    private void initiatePredixMobile(String baseAssetFolderPath, WebView webView) {
        PredixMobileConfiguration.baseAssetFolderPath = baseAssetFolderPath;
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        try {
            PredixSDKLogger.setLoggingLevel(LoggingLevel.trace);
            AndroidMobileManager instance = AndroidMobileManager.instance;
            if (instance.isRunning()) {
                instance.stop();
            }
            else {
                CookieManager.getInstance().removeAllCookies(null);
            }

//            registerSensorServices();
            instance.initialize(this, buildViewInterface());
            updateAppVersionInfo();
            instance.start();
            webView.setWebViewClient(new ServiceRouterWebViewClient());
        } catch (InitializationException e) {
            PredixSDKLogger.error(this, "could not initialize application", e);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidMobileManager.instance.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AndroidMobileManager.instance.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AndroidMobileManager.instance.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidMobileManager.instance.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidMobileManager.instance.onDestroy();
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
        AndroidMobileManager.instance.onStop();
    }

    private ViewInterface buildViewInterface() {
        final MainActivity activity = this;
        final Properties defaultPreferences = new Properties();
        try {
            defaultPreferences.load(this.getAssets().open("preference_defaults.properties"));
        } catch (IOException e) {
            PredixSDKLogger.debug(this, "Exception loading preference_defaults.properties", e);
        }
        return new ViewInterface() {
            @Override
            public PlatformContext getContext() {
                return new PredixAndroidContext(MainActivity.this);
            }

            @Override
            public Map<String, Object> getDefaultPreferences() {
                return (Map) defaultPreferences;
            }

            @Override
            public AuthHandler getAuthHandler() {
                return new AuthenticationHandler(activity);
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

    private void updateAppVersionInfo() {
        PredixSDKLogger.debug(this, "Adding app container version information.");
        VersionInfo.addVersionAddendumProvider(new VersionAddendumProvider() {
            @Override
            public Map<String, String> getVersionAddendum() {
                Map<String, String> toReturn = new HashMap<>();
                toReturn.put(CONTAINER_VERSION_CODE, String.valueOf(BuildConfig.VERSION_CODE));
                toReturn.put(CONTAINER_VERSION_NAME, BuildConfig.VERSION_NAME);
                return toReturn;
            }
        });
    }

    /* Uncomment me for adding push filter.
    private void createPushFilter() {
        PushReplicationFilter filter = new PushReplicationFilter() {
            @Override
            public boolean filterFunction(Map<String, Object> document, Map<String, Object> parameters) {
                String docTypeParam = (String) parameters.get("type");
//              simply returns true to allow the document to be replicated, or false to prevent it from being replicated.
                return docTypeParam != null && docTypeParam.equals(document.get("type"));
            }
        };
        String filterName = "typeFilter";
        PredixMobileConfiguration.addNewFilter(filterName, filter);
        PredixMobileConfiguration.defaultPushReplicationFilterName = filterName;
    }*/

    /*
    * uncomment me for registering sensor services.
    private void registerSensorServices() {
        PredixMobileConfiguration.additionalBootServicesToRegister = new ArrayList<>();
        PredixMobileConfiguration.additionalBootServicesToRegister.add(ProximitySensorService.class);
        PredixMobileConfiguration.additionalBootServicesToRegister.add(OrientationDetectionService.class);
    }*/
}
