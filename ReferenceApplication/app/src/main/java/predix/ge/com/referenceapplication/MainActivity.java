package predix.ge.com.referenceapplication;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.ge.predix.mobile.core.PredixLocalNotificationManagerImpl.DISPLAY_ACTION;
import static com.ge.predix.mobile.platform.PredixConstants.VersionInfoKeys.CONTAINER_VERSION_CODE;
import static com.ge.predix.mobile.platform.PredixConstants.VersionInfoKeys.CONTAINER_VERSION_NAME;


public class MainActivity extends AppCompatActivity implements ComponentCallbacks2{

    private WebView webView;
    private NotificationAtTimeBroadcastReceiver broadcastReceiver;
    private AuthenticationHandler authenticationHandler;
    private boolean incompleteAuthentication;

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
            AndroidMobileManager instance = AndroidMobileManager.getInstance();
            if (instance.isRunning()) {
                instance.stop();
            }
            else {
                CookieManager.getInstance().removeAllCookies(null);
            }

//            registerSensorServices();
            instance.initialize(this.getApplicationContext(), buildViewInterface());
            updateAppVersionInfo();
            webView.setWebViewClient(new ServiceRouterWebViewClient());
        } catch (InitializationException e) {
            PredixSDKLogger.error(this, "could not initialize application", e);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidMobileManager.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DISPLAY_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastReceiver = new NotificationAtTimeBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
        AndroidMobileManager.getInstance().onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AndroidMobileManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidMobileManager.getInstance().onResume();
        try {
            if (!incompleteAuthentication) {
                AndroidMobileManager.getInstance().start();
            } else {
                finish();
            }
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidMobileManager.getInstance().onDestroy();
        AndroidMobileManager instance = AndroidMobileManager.getInstance();
        try {
            instance.stop();
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.removeAllViews();
            webView.destroy();
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
        AndroidMobileManager.getInstance().onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==AuthenticationActivity.AUTHENTICATION_DECLINED) this.incompleteAuthentication = true;
    }

    private ViewInterface buildViewInterface() {
        MobileWebView mobileWebView = new MobileWebView(webView);
        Properties defaultPreferences = new Properties();
        try {
            defaultPreferences.load(this.getAssets().open("preference_defaults.properties"));
        } catch (IOException e) {
            PredixSDKLogger.debug(this, "Exception loading preference_defaults.properties", e);
        }
        ViewInterfaceImpl viewInterface = new ViewInterfaceImpl(this, mobileWebView, (Map) defaultPreferences);
        authenticationHandler = (AuthenticationHandler) viewInterface.getAuthHandler();
        return viewInterface;
    }

    private static class ViewInterfaceImpl implements ViewInterface {
        private final WeakReference<MainActivity> mActivity;
        private PredixAndroidContext predixAndroidContext;
        private AuthHandler handler;
        private MobileWebView mobileWebView;
        private Map<String, Object> preferences;
        public ViewInterfaceImpl(MainActivity activity, MobileWebView mobileWebView, Map<String, Object> preferences) {
            mActivity = new WeakReference<>(activity);
            MainActivity mainActivity = this.mActivity.get();
            this.mobileWebView = mobileWebView;
            this.preferences = preferences;

            this.predixAndroidContext = new PredixAndroidContext(mainActivity.getApplicationContext());
            this.handler = new AuthenticationHandler(mainActivity);
        }

        @Override
        public PlatformContext getContext() {
            return predixAndroidContext;
        }

        @Override
        public Map<String, Object> getDefaultPreferences() {
            return preferences;
        }

        @Override
        public AuthHandler getAuthHandler() {
            return handler;
        }

        @Override
        public WindowView getWindowView() {
            return mobileWebView;
        }
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


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AndroidMobileManager.getInstance().onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        AndroidMobileManager.getInstance().onTrimMemory(level);
    }

    /**
     * Updates the Predix Mobile sdk with the container's version name and code present in app's build.gradle file.
     */
    private void updateAppVersionInfo() {
        PredixSDKLogger.debug(this, "Adding app container version information.");
//      Similarly any other version related information can be added to the VersionInfo by add a VersionAddendumProvider.
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
