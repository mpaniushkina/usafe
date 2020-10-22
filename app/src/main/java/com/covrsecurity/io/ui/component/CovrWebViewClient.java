package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.covrsecurity.io.app.AppAdapter;

public class CovrWebViewClient extends WebViewClient {

    private final String host;

    public CovrWebViewClient(String host) {
        this.host = host;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!TextUtils.isEmpty(host) && host.equals(Uri.parse(url).getHost())) {
            return false;
        }
        startNewActivity(AppAdapter.context(), Uri.parse(url));
        return true;
    }

    private void startNewActivity(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
