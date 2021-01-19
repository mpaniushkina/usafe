package com.covrsecurity.io.ui.fragment.unauthorized;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentPrivacyPolicyBinding;
import com.covrsecurity.io.ui.component.CovrWebViewClient;

import org.jetbrains.annotations.NotNull;

import static android.webkit.WebView.RENDERER_PRIORITY_BOUND;

public class PrivacyPolicyFragment extends BaseUnauthorizedFragment<FragmentPrivacyPolicyBinding> {

    private static final String INFO_URL_EXTRA = "INFO_URL_EXTRA";
    private static final String PAGE_TITLE_EXTRA = "PAGE_TITLE_EXTRA";
    private static final String SHOW_TOOLBAR_EXTRA = "SHOW_TOOLBAR_EXTRA";
    private static final String HTTP_SCHEME = "http";

    @NonNull
    private String url;
    @Nullable
    private String pageTitle;
    private boolean showToolbar;
    @Nullable
    private String host;

    public static Fragment newInstance(String url, String pageTitle, boolean showToolbar) {
        Fragment fragment = new PrivacyPolicyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INFO_URL_EXTRA, url);
        bundle.putString(PAGE_TITLE_EXTRA, pageTitle);
        bundle.putBoolean(SHOW_TOOLBAR_EXTRA, showToolbar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_privacy_policy;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            url = arguments.getString(INFO_URL_EXTRA);
            pageTitle = arguments.getString(PAGE_TITLE_EXTRA);
            showToolbar = arguments.getBoolean(SHOW_TOOLBAR_EXTRA);
            host = getHost(url);
        }
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        improveWebViewPerformance();
        mBinding.toolBar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
        mBinding.wvInfo.loadUrl(url);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(pageTitle);
        LinearLayout backButton = view.findViewById(R.id.tool_left_button);
        backButton.setOnClickListener((v) -> goBack());
    }

    @Nullable
    private String getHost(String url) {
        Uri uri = Uri.parse(url);
        if (uri.getScheme() != null && uri.getScheme().startsWith(HTTP_SCHEME)) {
            String host = Uri.parse(url).getHost();
            return !TextUtils.isEmpty(host) ? host : null;
        }
        return null;
    }

    private void improveWebViewPerformance() {
        WebSettings webSettings = mBinding.wvInfo.getSettings();
        mBinding.wvInfo.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mBinding.wvInfo.setBackgroundColor(getResources().getColor(R.color.blue_bg));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBinding.wvInfo.setRendererPriorityPolicy(RENDERER_PRIORITY_BOUND, true);
        }
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);
        mBinding.wvInfo.setWebViewClient(new CovrWebViewClient(host));
    }

    @Override
    public boolean onBackButton() {
        if (mBinding.wvInfo.canGoBack()) {
            mBinding.wvInfo.goBack();
            return true;
        } else {
            return false;
        }
    }

    private void goBack() {
        while (mBinding.wvInfo.canGoBack()) {
            mBinding.wvInfo.goBack();
        }
        getActivity().onBackPressed();
    }
}
