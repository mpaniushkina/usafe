package com.covrsecurity.io.app;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Spannable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.covrsecurity.io.R;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.event.CovrEventBus;
import com.covrsecurity.io.event.EventProvider;
import com.covrsecurity.io.event.TimerTimeoutEvent;
import com.covrsecurity.io.manager.SettingsManager;
import com.covrsecurity.io.model.Push;
import com.covrsecurity.io.network.ConnectionStateMonitor;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.AppAdapterUtils;
import com.covrsecurity.io.utils.NotificationUtils;
import com.instacart.library.truetime.TrueTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;
import timber.log.Timber;

public class AppAdapter {

    private static Context appContext;
    private static Resources resources;
    private static SettingsManager settings;
    private static EventProvider eventProvider;

    @NonNull
    private static List<TransactionEntity> mConsumerRequests = new ArrayList<>();
    // currently only for the first page
    private static List<TransactionEntity> mHistoryCache = new LinkedList<>();
    private static List<MerchantEntity> mPartnerships = new ArrayList<>();

    private static ConnectionStateMonitor sConnectionStateMonitor;

    public static synchronized void initAdapter(Context appContext) {
        AppAdapter.appContext = appContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sConnectionStateMonitor = new ConnectionStateMonitor();
            new Thread(() -> sConnectionStateMonitor.enable(appContext)).start();
        }
    }

    public static Context context() {
        checkInit();
        return appContext;
    }

    public static Resources resources() {
        if (resources == null) {
            checkInit();
            resources = appContext.getResources();
        }
        return resources;
    }

    public static SettingsManager settings() {
        if (settings == null) {
            checkInit();
            settings = new SettingsManager(appContext);
        }
        return settings;
    }

    public static EventProvider bus() {
        if (eventProvider == null) {
            checkInit();
            eventProvider = new CovrEventBus();
        }
        return eventProvider;
    }

    private static void checkInit() {
        if (appContext == null) throw new IllegalStateException("AppAdapter was not initialized");
    }

    public static void notifyNoInternet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sConnectionStateMonitor.notifyNoInternet();
        }
    }

    /* ****************** Pending requests management ******************* */
    private static class TimeoutTimer extends Timer {
        public String mId;

        public TimeoutTimer(String id) {
            mId = id;
        }
    }

    private static ArrayList<TimeoutTimer> mTimers;

    public static void cleanConsumerRequests() {
        mConsumerRequests.clear();
        cancelAllTimers();
    }

    public static void addConsumerRequests(List<TransactionEntity> consumerRequests) {
        mConsumerRequests.addAll(AppAdapterUtils.getNewConsumerRequests(mConsumerRequests, consumerRequests));
        addTimers(consumerRequests);
    }

    public static void removeConsumerRequest(final TransactionEntity pendingTransaction) {
        if (!mConsumerRequests.isEmpty()) {
            ListIterator<TransactionEntity> listIterator = mConsumerRequests.listIterator();
            if (listIterator.hasNext()) {
                while (listIterator.hasNext()) {
                    TransactionEntity temp = listIterator.next();
                    if (temp.getId().equals(pendingTransaction.getId())) {
                        listIterator.remove();
                    }
                }
            }
        }
        updateIconLauncherBadge();
    }

    public static List<TransactionEntity> getConsumerRequests() {
        return mConsumerRequests;
    }

    public static List<TransactionEntity> getPendingConsumerRequests() {
        ArrayList<TransactionEntity> requests = new ArrayList<>();
        for (TransactionEntity r : mConsumerRequests) {
            if (!(TrueTime.now().getTime() >= r.getValidTo())) {
                if (StatusEntity.ACTIVE.equals(r.getStatus())) {
                    requests.add(r);
                }
            }
        }
        cancelAllTimers();
        addTimers(requests);
        return requests;
    }

    private static void cancelAllTimers() {
        if (mTimers != null) {
            for (TimeoutTimer timer : mTimers) {
                timer.cancel();
            }
            mTimers.clear();
        }
        mTimers = new ArrayList<>();
    }

    private static void addTimers(List<TransactionEntity> requests) {
        for (TransactionEntity r : requests) {
            addTimer(r);
        }
    }

    private static void addTimer(TransactionEntity r) {
        TimeoutTimer t = new TimeoutTimer(r.getId());
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ActivityUtils.getLastFragmentName().equals(StandingByFragment.class.getName())) {
                    Timber.d("Play TimeoutTimer for id = %s:    ---> Timeout sound", t.mId);
                    //SoundUtils.playTimeoutSound();
                }
            }
        }, new Date(r.getValidTo()));
        mTimers.add(t);
    }

    public static void cancelTimerSound(TransactionEntity pendingTransaction) {
        // replace with list?
        Timer temp = null;
        for (TimeoutTimer timer : mTimers) {
            if (timer.mId.equals(pendingTransaction.getId())) {
                timer.cancel();
                temp = timer;
            }
        }
        if (temp != null) {
            mTimers.remove(temp);
            AppAdapter.bus().post(new TimerTimeoutEvent(mTimers.size()));
        }
    }

    /* ****************** Partnerships management ******************* */
    public static List<MerchantEntity> getPartnerships() {
        return mPartnerships;
    }

    public static void setPartnerships(List<MerchantEntity> partnerships) {
        if (mPartnerships == null) {
            mPartnerships = new ArrayList<>();
        }
        mPartnerships.addAll(AppAdapterUtils.getNewPartnerships(mPartnerships, partnerships));
        //TODO: UA-32: unviewed partnership are no longer marked with badge, by client request
        //AppAdapterUtils.notifyUnviewedPartnershipsCount(mPartnerships);
    }


    public static void markPartnershipAsViewed(final String id) {
        if (mPartnerships != null && !mPartnerships.isEmpty()) {
            for (MerchantEntity partnership : mPartnerships) {
                if (partnership.getId().equals(id)) {
                    partnership.getCompany().setViewed(true);
                }
            }
        }
        //TODO: UA-32: unviewed partnership are no longer marked with badge, by client request
        //AppAdapterUtils.notifyUnviewedPartnershipsCount(mPartnerships);
    }

    public static void cleanPartnerships() {
        mPartnerships.clear();
    }

    public static void cleanAllCache() {
        cleanConsumerRequests();
        cleanPartnerships();
    }

    /* ******************* HISTORY ********************* */
    public static synchronized List<TransactionEntity> getHistoryCache() {
        if (mHistoryCache == null) {
            mHistoryCache = new ArrayList<>();
        }
        return mHistoryCache;
    }

    public static synchronized List<TransactionEntity> updateHistoryCache(List<TransactionEntity> newHistoryEntries) {
        if (newHistoryEntries != null && newHistoryEntries.size() > 0) {
            if (mHistoryCache == null) {
                mHistoryCache = new ArrayList<>();
            } else {
                mHistoryCache.clear();
            }
            mHistoryCache.addAll(newHistoryEntries);
        }
        return mHistoryCache;
    }

    public static synchronized void setHistoryCache(List<TransactionEntity> newHistoryEntries) {
        if (mHistoryCache == null) {
            mHistoryCache = new ArrayList<>();
        }
        mHistoryCache.clear();
        if (newHistoryEntries != null) {
            mHistoryCache.addAll(0, newHistoryEntries);
        }
    }

    public static synchronized void updateIconLauncherBadge() {
        int requests = getConsumerRequests().size();
        int history = settings().getHistoryBadge();
        int badgeCount = requests + history;
        settings().setCurrentBadgeCount(badgeCount);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resetBadgeCount();
        }
        ShortcutBadger.applyCount(context(), requests + history);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void resetBadgeCount() {
        int badgeCount = AppAdapter.settings().getCurrentBadgeCount();
        NotificationUtils.clearNotification(context());
        if (badgeCount > 0) {
            NotificationUtils.buildSilentNotification(context(), new Push(
                    context().getResources().getString(R.string.badge_count_message), badgeCount));
        }
    }
}
