package predix.ge.com.referenceapplication;

import android.support.annotation.NonNull;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ge.predix.mobile.enums.WaitState;
import com.ge.predix.mobile.platform.CustomSchemeHandler;
import com.ge.predix.mobile.platform.WaitStateModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 212572548 on 9/19/16.
 */
@RunWith(AndroidJUnit4.class)
public class MobileWebViewTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class);

    private WebView webView;
    private MobileWebView mobileWebView;
    private Map temporaryMap;
    private MainActivity activity;
    private static final String TEST_URL = "file:///android_asset/www/mobile_web_view_test.html";

    @Before
    public void setUp() throws InterruptedException {
        activity = activityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                webView = new WebView(activity);
                webView.getSettings().setJavaScriptEnabled(true);
                mobileWebView = new MobileWebView(webView);
                temporaryMap = Collections.emptyMap();
                activity.setContentView(webView);
                synchronized (MobileWebViewTest.this) { MobileWebViewTest.this.notify(); }
            }
        });
        synchronized (this) { this.wait(); }
    }

    @After
    public void tearDown(){
        webView =null;
        mobileWebView =null;
        temporaryMap =null;
        activity=null;

    }

    @Test
    public void shouldLoadTheGivenURLWithMobileWebView() throws Exception {
        mobileWebView.loadURL(TEST_URL, temporaryMap);
        onWebView().withElement(findElement(Locator.ID, "web_view")).check(webMatches(getText(), equalTo("hello world!")));
    }

    @Test
    public void shouldCreateAToastWhenReceiveAppNotificationIsCalled() throws Exception {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                mobileWebView.receiveAppNotification("this is a notification");
                synchronized(MobileWebViewTest.this) {MobileWebViewTest.this.notify();}
            }
        });
        synchronized(this) {this.wait();}
        onView(withText("this is a notification"))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldAssertThatTheWaitStateModelPassedToUpdateWaitStateIsTheRightOne() throws Exception {
        mobileWebView.updateWaitState(makeNonWaitingState());
        assertEquals(mobileWebView.waitState().waitState, WaitState.NotWaiting);
        mobileWebView.updateWaitState(makeWaitingState());
        assertEquals(mobileWebView.waitState().waitState, WaitState.Waiting);
    }

    @Ignore
    public void shouldShowSpinnerOnScreenWhenWaitStateIsWaiting() {
        mobileWebView.updateWaitState(makeWaitingState());
        onView(isAssignableFrom(ProgressBar.class)).check(matches(isDisplayed()));
    }

    @Ignore
    public void shouldRemoveSpinnerWhenWaitStateIsChangedFromWaitingToNotWaiting() {
        mobileWebView.updateWaitState(makeWaitingState());
        mobileWebView.updateWaitState(makeNonWaitingState());
        onView(isAssignableFrom(ProgressBar.class)).check(doesNotExist());
    }

    @Test
    public void shouldShowDialogWithGivenMessagesWhenShowDialogIsCalled() {
        init();
        mobileWebView.showDialog("message1", "message2", "message3");
        intended(hasComponent(SeriousErrorActivity.class.getName()));
        onView(withText("message1")).check(matches(isDisplayed()));
        onView(withText("message2")).check(matches(isDisplayed()));
        onView(withText("message3")).check(matches(isDisplayed()));
        release();
    }
    @Test
    public void customSchemeHandlerShouldOpenWebBrowserPageWithACorrectWebBrowserURL() throws URISyntaxException {
       CustomSchemeHandler  customSchemeHandler= mobileWebView.customSchemeHandler();
         assertTrue(customSchemeHandler.canHandleURLScheme(new URI("http://google.com")));
    }

    @Test
    public void customSchemeHandlerShouldNotOpenWebBrowserPageWithIncorrectWebBrowserURL() throws URISyntaxException {
        CustomSchemeHandler  customSchemeHandler= mobileWebView.customSchemeHandler();
        assertFalse(customSchemeHandler.canHandleURLScheme(new URI("htp://google.com")));
    }

    @Test
    public void customSchemeHandlerShouldInitiatedAPhoneCallWithACorrectTelProtocol() throws URISyntaxException {
        CustomSchemeHandler customSchemeHandler =mobileWebView.customSchemeHandler();
        assertTrue(customSchemeHandler.canHandleURLScheme(new URI("tel:1-800-555-1212")));
    }

    @Test
    public void customSchemeHandlerShouldNotInitiatedAPhoneCallWithAnIncorrectTelProtocol() throws URISyntaxException {
        CustomSchemeHandler customSchemeHandler =mobileWebView.customSchemeHandler();
        assertFalse(customSchemeHandler.canHandleURLScheme(new URI("tell:1-800-555-1212")));
    }

    @Test
    public void customSchemeHandlerShouldOpenMailAppWithACorrectMailProtocol() throws URISyntaxException {
        CustomSchemeHandler customSchemeHandler =mobileWebView.customSchemeHandler();
        assertTrue(customSchemeHandler.canHandleURLScheme(new URI("mailto:someone@example.com?Subject=Hello%20again")));
    }


    @Test
    public void customSchemeHandlerShouldNotOpenMailAppWithAnIncorrectMailProtocol() throws URISyntaxException {
        CustomSchemeHandler customSchemeHandler =mobileWebView.customSchemeHandler();
        assertFalse(customSchemeHandler.canHandleURLScheme(new URI("mail:someone@example.com?Subject=Hello%20again")));
    }


    @NonNull
    private WaitStateModel makeNonWaitingState() {
        return new WaitStateModel(WaitState.NotWaiting, "a wait state model");
    }

    @NonNull
    private WaitStateModel makeWaitingState() {
        return new WaitStateModel(WaitState.Waiting, "a wait state model");
    }
}







































