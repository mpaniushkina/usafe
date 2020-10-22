package com.covrsecurity.io.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by alex on 16.5.16.
 */
public final class ConnectivityUtils {

    private static final String GOOGLE_PUBLIC_DNS = "8.8.8.8";
    private static final int GOOGLE_PUBLIC_DNS_PORT = 53;
    private static final int SOCKET_TIMEOUT = 2000;

    public static boolean isConnected(Context context) {
        boolean ifConnected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            for (NetworkInfo connection : info) {
                if (connection.getState().equals(NetworkInfo.State.CONNECTED)) {
                    ifConnected = true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return ifConnected;
    }

    public static boolean hasNetworkConnection() {
        boolean exists = false;
        try {
            SocketAddress socketAddress = new InetSocketAddress(GOOGLE_PUBLIC_DNS, GOOGLE_PUBLIC_DNS_PORT);
            // Create an unbound socket
            Socket sock = new Socket();
            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            sock.connect(socketAddress, SOCKET_TIMEOUT);
            exists = true;
        } catch (IOException ignored) {
        }
        return exists;
    }
}
