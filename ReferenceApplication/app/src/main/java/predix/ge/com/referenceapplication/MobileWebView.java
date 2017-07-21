package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.ge.predix.mobile.enums.WaitState;
import com.ge.predix.mobile.logging.PredixSDKLogger;
import com.ge.predix.mobile.platform.CustomSchemeHandler;
import com.ge.predix.mobile.platform.WaitStateModel;
import com.ge.predix.mobile.platform.WindowView;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 212572548 on 9/16/16.
 */
public class MobileWebView implements WindowView {
    private final WebView webView;
    private ProgressDialog spinner;
    private WaitStateModel waitStateModel;
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private final AtomicBoolean isWebViewProcessing = new AtomicBoolean(false);
    private boolean spinnerIsShowing = false;

    public MobileWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void loadURL(final String url, Map map) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                hideSpinner();
                webView.loadUrl(url);
            }
        });
    }

    @Override
    public void receiveAppNotification(final String s) {
        Runnable runnable = new Runnable() {
            public void run() {
                webView.evaluateJavascript(s, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String response) {
                        PredixSDKLogger.trace(this, "evaluateJavascript response: "+response);
                        Runnable task = queue.poll();
                        if (null != task) {
                            PredixSDKLogger.info(this, "Starting another task, pending are: "+queue.size());
                            webView.post(task);
                        }
                        else {
                            isWebViewProcessing.set(false);
                            PredixSDKLogger.info(this, "No more tasks for webView :-) ");
                        }

                    }
                });
            }
        };
        PredixSDKLogger.info(this, "Added a task to queue for webView processing.");
        queue.offer(runnable);

//        webView.post(runnable);
        if (!isWebViewProcessing.get()) {
            isWebViewProcessing.set(true);
            PredixSDKLogger.info(this, "WebView started processing a task from queue, pending are: "+queue.size());
            webView.post(queue.poll());
        }
    }

    @Override
    public void showDialog(String s, String s1, String s2) {
        Activity context = (Activity) webView.getContext();
        Intent intent = new Intent(context, SeriousErrorActivity.class);
        intent.putExtra("message1", s);
        intent.putExtra("message2", s1);
        intent.putExtra("message3", s2);
        context.startActivity(intent);
    }

    @Override
    public WaitStateModel waitState() {
        return waitStateModel;
    }

    @Override
    public void updateWaitState(WaitStateModel waitStateModel) {
        this.waitStateModel = waitStateModel;
        if (waitStateModel.waitState.equals(WaitState.Waiting)) {
            showProgressSpinner(waitStateModel.message);
        } else {
            hideSpinner();
        }
    }

    @Override
    public CustomSchemeHandler customSchemeHandler() {
        return new CustomSchemeHandler() {
            @Override
            public boolean canHandleURLScheme(URI uri) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri.toString()));
                List<ResolveInfo> resolveInfoList =   webView.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!resolveInfoList.isEmpty()) {
                    return true;
                }
                return false;
            }

            @Override
            public void openURLScheme(URI uri) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri.toString()));
                webView.getContext().startActivity(intent);
            }
        };
    }

    private void hideSpinner() {
        if (!spinnerIsShowing) {
            return;
        } else {
            spinnerIsShowing = false;
        }

        webView.post(new Runnable() {
            @Override
            public void run() {
                if (spinner!=null) spinner.dismiss();
                spinner = null;
            }
        });
    }

    private void showProgressSpinner(final String message) {
        if (spinnerIsShowing) {
            hideSpinner();
        }
        spinnerIsShowing = true;
        
        webView.post(new Runnable() {
            @Override
            public void run() {
                if(spinner!=null){
                    spinner.dismiss();
                    spinner = null;
                }
                spinner = ProgressDialog.show(webView.getContext(), message, "Please Wait...", true);
            }
        });
    }
}
