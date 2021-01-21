package com.covrsecurity.io.app;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.covrsecurity.io.BuildConfig;
import com.covrsecurity.io.R;
import com.covrsecurity.io.common.utils.FileUtils;
import com.covrsecurity.io.greendao.model.database.DaoSession;
import com.covrsecurity.io.inject.component.DaggerAppComponent;
import com.covrsecurity.io.manager.Analytics;
import com.covrsecurity.io.model.Fields;
import com.covrsecurity.io.sdk.CovrNewMainInterface;
import com.covrsecurity.io.utils.DatabaseOperationsWrapper;
import com.covrsecurity.io.utils.LogUtil;
import com.covrsecurity.io.utils.PRNGFixesUtils;
//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.zendesk.logger.Logger;

import java.io.File;

import javax.inject.Inject;

import co.hyperverge.hypersnapsdk.HyperSnapSDK;
import co.hyperverge.hypersnapsdk.objects.HyperSnapParams;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerApplication;
//import io.fabric.sdk.android.Fabric;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import timber.log.Timber;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

public class IamApp extends DaggerApplication implements
        UnsafeLifecycleCallbacks.OnApplicationMinimizedListener {

    private static IamApp instance = null;

    public static IamApp getInstance() {
        return instance;
    }

    private static final String HYPER_SNAP_APP_ID = "9a6cc4";
    private static final String HYPER_SNAP_APP_KEY = "15df1efdb58328becfac";

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    public boolean isNotificationHubInitialized;

    private UnsafeLifecycleCallbacks mLyfecycleCallbacks;

    private boolean mInitialVersionCheckDone;
    private boolean mIsAppBlocked;
    private boolean mIsForceUpgrade;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        try {
            PRNGFixesUtils.apply();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }

        CovrNewMainInterface.INITIALIZER_INSTANCE.init(Fields.COVR_SDK_ID, Fields.COVR_SDK_SECRET, Fields.COVR_SDK_QRCODE_ID, Fields.COVR_SDK_QRCODE_SECRET, this);
        LogUtil.initLogging();
        AppAdapter.initAdapter(this);
        mLyfecycleCallbacks = new UnsafeLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(mLyfecycleCallbacks);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(getString(R.string.cfg_font_poppins_regular))
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        Analytics.logEvent(this.getApplicationContext(), Analytics.EVENT_APP_LAUNCH);
//        Fabric.with(this, new Crashlytics());
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }

        initZendesk();

        HyperSnapSDK.init(getApplicationContext(), HYPER_SNAP_APP_ID, HYPER_SNAP_APP_KEY, HyperSnapParams.Region.India);
    }

    @Deprecated // TODO !!!
    @Override
    public void onApplicationMinimized() {
        try {
            AppAdapter.cleanAllCache();
        } catch (Throwable t) {
//            Crashlytics.logException(t);
            Timber.e(t);
        }
    }

    @Deprecated
    public DaoSession getDaoSession() {
        return null;
    }

    @Deprecated
    public DatabaseOperationsWrapper getDatabaseOperationsWrapper() {
        return null;
    }

    /**
     * Not used at the moment but may be of help later. Let it be here for a while
     */
    public boolean isAppInForeground() {
        boolean isVisible = mLyfecycleCallbacks.mStarted > mLyfecycleCallbacks.mStopped;
        boolean isForeground = mLyfecycleCallbacks.mResumed > mLyfecycleCallbacks.mPaused;
        return isVisible;
    }

    public boolean isApplicationWasMinimized() {
        return mLyfecycleCallbacks.isApplicationWasMinimized();
    }

    public void setApplicationWasMinimized(boolean applicationWasMinimized) {
        mLyfecycleCallbacks.setApplicationWasMinimized(applicationWasMinimized);
    }

    public boolean isInitialVersionCheckDone() {
        return mInitialVersionCheckDone;
    }

    public void setInitialVersionCheckDone() {
        mInitialVersionCheckDone = true;
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib") && !fileName.equals("shared_prefs")) {
                    FileUtils.deleteAllFilesInDirectory(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public void setForceUpgrade() {
        mIsForceUpgrade = true;
    }

    public boolean isForceUpgrade() {
        return mIsForceUpgrade;
    }

    public void setAppIsBlocked() {
        mIsAppBlocked = true;
    }

    public boolean isAppBlocked() {
        return mIsAppBlocked;
    }

    private void initZendesk() {
        Logger.setLoggable(BuildConfig.DEBUG);
        Zendesk.INSTANCE.init(
                this,
                getString(R.string.zendesk_url),
                getString(R.string.zendesk_app_id),
                getString(R.string.zendesk_oauth_client_id)
        );
        Support.INSTANCE.init(Zendesk.INSTANCE);
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
