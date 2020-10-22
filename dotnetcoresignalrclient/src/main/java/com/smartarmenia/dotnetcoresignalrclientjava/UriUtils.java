package com.smartarmenia.dotnetcoresignalrclientjava;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public final class UriUtils {

    private UriUtils() {
    }

    public static URL appendSegment(URL url, String segment) throws MalformedURLException {
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "/" + segment);
    }

    public static URL addQueryParameter(String URL, String name, String value) throws MalformedURLException {
        int qpos = URL.indexOf('?');
        int hpos = URL.indexOf('#');
        char sep = qpos == -1 ? '?' : '&';
        String seg = sep + encodeUrl(name) + '=' + encodeUrl(value);
        String spec = hpos == -1 ? URL + seg : URL.substring(0, hpos) + seg
                + URL.substring(hpos);
        return new URL(spec);
    }

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(uee);
        }
    }

    public static String switchHttpToWs(URL url) {
        return url.toString().replaceFirst("http", "ws");
    }
}
