package com.smartarmenia.dotnetcoresignalrclientjava;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.BaseSocketProvider;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketCallbacks;
import com.smartarmenia.dotnetcoresignalrclientjava.provider.SignalRWebSocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketHubConnectionP2 implements HubConnection {
    private static String SPECIAL_SYMBOL = "\u001E";
    private static String TAG = "WebSockets";

    private SignalRWebSocketClient client;
    private List<HubConnectionListener> listeners = new ArrayList<>();
    private Map<String, List<HubEventListener>> eventListeners = new HashMap<>();
    private URL parsedUrl;
    private Gson gson = new Gson();
    private ExecutorService mConnectionExecutor;
    private ExecutorService mCommandExecutor;
    private volatile boolean obtainingConnectionId;

    private String connectionId;
    private String authHeader;
    private boolean logEnabled;

    private BaseSocketProvider baseSocketCreator;

    public <T extends BaseSocketProvider> WebSocketHubConnectionP2(URL hubUrl, String authHeader, T baseSocketCreator) {
        this(hubUrl, authHeader, baseSocketCreator, true);
    }

    public <T extends BaseSocketProvider> WebSocketHubConnectionP2(URL hubUrl, String authHeader, T baseSocketCreator, boolean logEnabled) {
        this.authHeader = authHeader;
        this.parsedUrl = hubUrl;
        this.baseSocketCreator = baseSocketCreator;
        this.logEnabled = logEnabled;
        mCommandExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public synchronized void connect() {
        if (client != null && (client.isOpen() || client.isConnecting()))
            return;

        obtainingConnectionId = true;

        Runnable runnable;
        if (connectionId == null) {
            runnable = new Runnable() {
                public void run() {
                    getConnectionId();
                }
            };
        } else {
            runnable = new Runnable() {
                public void run() {
                    connectClient();
                }
            };
        }
        mConnectionExecutor = Executors.newSingleThreadExecutor(new ConnectionThreadFactory());
        mConnectionExecutor.execute(runnable);
    }

    private void getConnectionId() {
        if (!(parsedUrl.getProtocol().equals("http") || parsedUrl.getProtocol().equals("https")))
            throw new RuntimeException("URL must start with http or https");

        try {
            String negotiateUri = UriUtils.appendSegment(parsedUrl, "negotiate").toString();
            HttpURLConnection connection = (HttpURLConnection) new URL(negotiateUri).openConnection();
            if (authHeader != null && !authHeader.isEmpty()) {
                connection.addRequestProperty("Authorization", authHeader);
            }

            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("POST");

            if (mConnectionExecutor.isShutdown()) {
                return;
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                String result = InputStreamConverter.convert(connection.getInputStream());
                JsonElement jsonElement = gson.fromJson(result, JsonElement.class);
                String connectionId = jsonElement.getAsJsonObject().get("connectionId").getAsString();
                JsonElement availableTransportsElements = jsonElement.getAsJsonObject().get("availableTransports");
                List<JsonElement> availableTransports = Arrays.asList(gson.fromJson(availableTransportsElements, JsonElement[].class));
                boolean webSocketAvailable = false;
                for (JsonElement element : availableTransports) {
                    if (element.getAsJsonObject().get("transport").getAsString().equals("WebSockets")) {
                        webSocketAvailable = true;
                        break;
                    }
                }
                if (!webSocketAvailable) {
                    throw new RuntimeException("The server does not support WebSockets transport");
                }
                this.connectionId = connectionId;
                if (!mConnectionExecutor.isShutdown()) {
                    connectClient();
                }
            } else if (responseCode == 401) {
                throw new RuntimeException("Unauthorized request");
            } else {
                throw new RuntimeException("Server error");
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void connectClient() {
        Map<String, String> headers = new HashMap<>();
        if (authHeader != null && !authHeader.isEmpty()) {
            headers.put("Authorization", authHeader);
        }
        try {
            URL newUrl = UriUtils.addQueryParameter(parsedUrl.toString(), "id", connectionId);
            client = baseSocketCreator.createSocketClient(UriUtils.switchHttpToWs(newUrl), headers, 15000, new SignalRWebSocketCallbacks() {
                @Override
                public void onOpen() {
                    log("Opened");
                    for (HubConnectionListener listener : listeners) {
                        listener.onConnected();
                    }
                    client.send("{\"protocol\":\"json\",\"version\":1}" + SPECIAL_SYMBOL);
                }

                @Override
                public void onMessage(String message) {
                    log(message);
                    String[] messages = message.split(SPECIAL_SYMBOL);
                    for (String m : messages) {
                        SignalRMessage element = gson.fromJson(m, SignalRMessage.class);
                        Integer type = element.getType();
                        if (type != null && type == 1) {
                            HubMessage hubMessage = new HubMessage(element.getInvocationId(), element.getTarget(), element.getArguments());
                            for (HubConnectionListener listener : listeners) {
                                listener.onMessage(hubMessage);
                            }

                            List<HubEventListener> hubEventListeners = eventListeners.get(hubMessage.getTarget());
                            if (hubEventListeners != null) {
                                for (HubEventListener listener : hubEventListeners) {
                                    listener.onEventMessage(hubMessage);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onClosing(int code, String reason) {
                    log(String.format("Closed. Code: %s, Reason: %s", code, reason));
                }

                @Override
                public void onClose(int code, String reason) {
                    log(String.format("Closed. Code: %s, Reason: %s", code, reason));
                    for (HubConnectionListener listener : listeners) {
                        listener.onDisconnected();
                    }
                    connectionId = null;
                }

                @Override
                public void onError(Throwable ex) {
                    log("Error " + ex.getMessage());
                    error(ex);
                }
            });
            client.connect();
            obtainingConnectionId = false;
        } catch (Exception e) {
            error(e);
        }
        log("Connecting...");
    }

    private void error(Throwable ex) {
        for (HubConnectionListener listener : listeners) {
            listener.onError(ex);
        }
    }

    @Override
    public void disconnect() {
        Runnable runnable = new Runnable() {
            public void run() {
                if (client != null && !client.isClosing() && !client.isClosed()) {
                    client.close();
                }
                mConnectionExecutor.shutdown();
            }
        };
        mCommandExecutor.execute(runnable);
    }

    @Override
    public boolean isObtainingConnectionId() {
        return obtainingConnectionId;
    }

    @Override
    public synchronized boolean isConnecting() {
        return client != null && client.isConnecting();
    }

    @Override
    public synchronized boolean isConnected() {
        return client != null && client.isOpen();
    }

    @Override
    public synchronized boolean isClosing() {
        return client != null && client.isClosing();
    }

    @Override
    public synchronized boolean isClosed() {
        return client != null && client.isClosed();
    }

    @Override
    public void addListener(HubConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HubConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void subscribeToEvent(String eventName, HubEventListener eventListener) {
        List<HubEventListener> eventMap;
        if (eventListeners.containsKey(eventName)) {
            eventMap = eventListeners.get(eventName);
        } else {
            eventMap = new ArrayList<>();
            eventListeners.put(eventName, eventMap);
        }
        eventMap.add(eventListener);
    }

    @Override
    public void unSubscribeFromEvent(String eventName, HubEventListener eventListener) {
        if (eventListeners.containsKey(eventName)) {
            List<HubEventListener> eventMap = eventListeners.get(eventName);
            eventMap.remove(eventListener);
            if (eventMap.isEmpty()) {
                eventListeners.remove(eventName);
            }
        }
    }

    @Override
    public void invoke(String event, Object... parameters) {
        if (client == null || !client.isOpen()) {
            throw new RuntimeException("Not connected");
        }
        final Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("invocationId", UUID.randomUUID());
        map.put("target", event);
        map.put("arguments", parameters);
        map.put("nonblocking", false);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    client.send(gson.toJson(map) + SPECIAL_SYMBOL);
                } catch (Exception e) {
                    error(e);
                }
            }
        };
        mCommandExecutor.execute(runnable);
    }

    private void log(String message) {
        if (logEnabled) {
            System.out.println("[" + TAG + "]" + message);
        }
    }

    private static class InputStreamConverter {
        private static char RETURN_SYMBOL = '\n';

        static String convert(InputStream stream) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
                total.append(RETURN_SYMBOL);
            }

            return total.toString();
        }
    }
}