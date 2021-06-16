package com.wos.services.internal.utils;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlCodecUtil {

    public static String decodeAllowsDuplicates(String s, String enc) throws UnsupportedEncodingException {
        if (StringUtils.isNotEmpty(s)) {
            s = s.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            s = s.replaceAll("\\+", "%2B");
        } else {
            return s;
        }
        return URLDecoder.decode(s, enc);
    }

    public static String urlEncode(String strUrl, String enc) {
        String encodeUrl = strUrl;
        try {
            String decodeUrl = decodeAllowsDuplicates(strUrl, enc);
            encodeUrl = URLEncoder.encode(decodeUrl, enc);
        } catch (UnsupportedEncodingException e) {
        }
        String httpUrl = StringUtils.replace(encodeUrl, "%2F", "/");
        httpUrl = StringUtils.replace(httpUrl, "%3A", ":");
        httpUrl = StringUtils.replace(httpUrl, "%3F", "?");
        httpUrl = StringUtils.replace(httpUrl, "%3D", "=");
        httpUrl = StringUtils.replace(httpUrl, "%26", "&");
        httpUrl = StringUtils.replace(httpUrl, "+", "%20");
        return httpUrl;
    }

    public static String dataEncode(String strData, String enc) {
        String encodeData = strData;
        try {
            encodeData = URLEncoder.encode(strData, enc);
        } catch (UnsupportedEncodingException e) {
        }
        encodeData = StringUtils.replace(encodeData, "%2F", "/");
        encodeData = StringUtils.replace(encodeData, "+", "%20");
        return encodeData;
    }

    public static boolean isContainUnEncodedChar(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        String replaceResult = StringUtils.replacePattern(url, "%(?=[0-9a-fA-F]{2})", "");
        replaceResult = StringUtils.replacePattern(replaceResult,  "\\w|\\/|\\:|\\?|\\=|\\&|\\-|\\.", "");
        return StringUtils.isNotEmpty(replaceResult);
    }
}
