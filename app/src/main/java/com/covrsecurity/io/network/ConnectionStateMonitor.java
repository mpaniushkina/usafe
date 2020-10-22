package com.covrsecurity.io.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import androidx.annotation.RequiresApi;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.event.InternetLostEvent;
import com.covrsecurity.io.event.InternetRegainedEvent;
import com.covrsecurity.io.utils.ConnectivityUtils;

@RequiresApi(21)
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    private final NetworkRequest networkRequest;

    private boolean hasInternetConnection;

    public ConnectionStateMonitor() {
        networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
    }

    public void enable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest , this);
        }

        hasInternetConnection = ConnectivityUtils.hasNetworkConnection();
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        notifyInternetConnectionChanges();
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        notifyInternetConnectionChanges();
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        notifyInternetConnectionChanges();
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        notifyInternetConnectionChanges();
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        notifyInternetConnectionChanges();
    }

    public void notifyNoInternet() {
        hasInternetConnection = false;
    }

    private void notifyInternetConnectionChanges() {
        if (ConnectivityUtils.hasNetworkConnection()) {
            if (!hasInternetConnection) {
                AppAdapter.bus().postSticky(new InternetRegainedEvent());
                hasInternetConnection = true;
            }
        } else {
            if (hasInternetConnection) {
                AppAdapter.bus().postSticky(new InternetLostEvent());
                hasInternetConnection = false;
            }
        }
    }
}