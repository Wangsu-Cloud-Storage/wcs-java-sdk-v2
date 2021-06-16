package com.wos.services.internal.utils;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.internal.Constants;
import com.wos.services.internal.ServiceException;
import com.wos.services.internal.WosConstraint;
import com.wos.services.internal.WosProperties;
import com.wos.services.internal.ext.ExtWosConfiguration;
import com.wos.services.internal.ext.ExtWosConstraint;
import com.wos.services.model.AuthTypeEnum;
import com.wos.services.model.HttpProtocolTypeEnum;
import okhttp3.Headers;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceUtils {
    private static final ILogger log = LoggerBuilder.getLogger(ServiceUtils.class);

    protected static final String ISO_8601_TIME_PARSER_STRING = Constants.EXPIRATION_DATE_FORMATTER;
    protected static final String ISO_8601_TIME_MIDNING_PARSER_STRING = "yyyy-MM-dd'T'00:00:00'Z'";
    protected static final String ISO_8601_TIME_PARSER_WALRUS_STRING = "yyyy-MM-dd'T'HH:mm:ss";
    protected static final String RFC_822_TIME_PARSER_STRING = Constants.HEADER_DATE_FORMATTER;
    protected static final String ISO_8601_DATE_PARSER_STRING = "yyyy-MM-dd";

    private static Pattern pattern = Pattern
            .compile("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");

    public static boolean isValid(String s) {
        return s != null && !s.trim().equals("");
    }

    public static boolean isValid2(String s) {
        return s != null && !s.equals("");
    }

    public static String toValid(String s) {
        return s == null ? "" : s;
    }

    public static void asserParameterNotNull(String value, String errorMessage) {
        if (!isValid(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void asserParameterNotNull2(String value, String errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void asserParameterNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void assertParameterNotNegative(long value, String errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static Date parseIso8601Date(String dateString) throws ParseException {
        ParseException exception = null;
        SimpleDateFormat iso8601TimeParser = new SimpleDateFormat(ISO_8601_TIME_PARSER_STRING);
        TimeZone gmt = Constants.GMT_TIMEZONE;
        iso8601TimeParser.setTimeZone(gmt);
        try {
            return iso8601TimeParser.parse(dateString);
        } catch (ParseException e) {
            exception = e;
        }
        SimpleDateFormat iso8601TimeParserWalrus = new SimpleDateFormat(ISO_8601_TIME_PARSER_WALRUS_STRING);
        iso8601TimeParserWalrus.setTimeZone(gmt);
        try {
            return iso8601TimeParserWalrus.parse(dateString);
        } catch (ParseException e) {

            exception = e;
        }
        SimpleDateFormat iso8601DateParser = new SimpleDateFormat(ISO_8601_DATE_PARSER_STRING);
        iso8601DateParser.setTimeZone(gmt);
        try {
            return iso8601DateParser.parse(dateString);
        } catch (Exception e) {
        }
        throw exception;
    }

    public static String formatIso8601Date(Date date) {
        SimpleDateFormat iso8601TimeParser = new SimpleDateFormat(ISO_8601_TIME_PARSER_STRING);
        iso8601TimeParser.setTimeZone(Constants.GMT_TIMEZONE);
        return iso8601TimeParser.format(date);
    }

    public static String formatIso8601MidnightDate(Date date) {
        SimpleDateFormat iso8601TimeParser = new SimpleDateFormat(ISO_8601_TIME_MIDNING_PARSER_STRING);
        iso8601TimeParser.setTimeZone(Constants.GMT_TIMEZONE);
        return iso8601TimeParser.format(date);
    }

    public static Date parseRfc822Date(String dateString) throws ParseException {
        SimpleDateFormat rfc822TimeParser = new SimpleDateFormat(RFC_822_TIME_PARSER_STRING, Locale.US);
        rfc822TimeParser.setTimeZone(Constants.GMT_TIMEZONE);
        return rfc822TimeParser.parse(dateString);
    }

    public static String formatRfc822Date(Date date) {
        SimpleDateFormat rfc822TimeParser = new SimpleDateFormat(RFC_822_TIME_PARSER_STRING, Locale.US);
        rfc822TimeParser.setTimeZone(Constants.GMT_TIMEZONE);
        return rfc822TimeParser.format(date);
    }

    public static String signWithHmacSha1(String sk, String canonicalString) throws ServiceException {
        SecretKeySpec signingKey = null;
        try {
            signingKey = new SecretKeySpec(sk.getBytes(Constants.DEFAULT_ENCODING), Constants.HMAC_SHA1_ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to get bytes from secret string", e);
        }

        Mac mac = null;
        try {
            mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Could not find sha1 algorithm", e);
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
        }

        try {
            return ServiceUtils.toBase64(mac.doFinal(canonicalString.getBytes(Constants.DEFAULT_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to get bytes from canonical string", e);
        }
    }

    public static Map<String, Object> cleanRestMetadataMap(Map<String, List<String>> metadata, String headerPrefix,
            String metadataPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("Cleaning up REST metadata items");
        }
        Map<String, Object> cleanMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        if (metadata != null) {
            for (Map.Entry<String, List<String>> entry : metadata.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                if (key == null || values == null) {
                    continue;
                }

                Object value = values.size() == 1 ? values.get(0) : values;
                if ((Constants.CommonHeaders.DATE.equalsIgnoreCase(key)
                        || Constants.CommonHeaders.LAST_MODIFIED.equalsIgnoreCase(key))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Parsing date string '" + value + "' into Date object for key: " + key);
                    }
                    try {
                        value = ServiceUtils.parseRfc822Date(value.toString());
                    } catch (ParseException pe) {
                        // Try ISO-8601
                        try {
                            value = ServiceUtils.parseIso8601Date(value.toString());
                        } catch (ParseException pe2) {
                            if (log.isWarnEnabled()) {
                                log.warn("Date string is not RFC 822 or ISO-8601 compliant for metadata field " + key,
                                        pe);
                            }
                        }
                    }
                } else if (key.toLowerCase().startsWith(headerPrefix)) {
                    try {
                        if (key.toLowerCase().startsWith(metadataPrefix)) {
                            key = key.substring(metadataPrefix.length(), key.length());
                            key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
                            if (log.isDebugEnabled()) {
                                log.debug("Removed meatadata header prefix " + metadataPrefix + " from key: " + key
                                        + "=>" + key);
                            }
                        } else {
                            key = key.substring(headerPrefix.length(), key.length());
                        }
                        if (value instanceof List) {
                            List<String> metadataValues = new ArrayList<String>(values.size());
                            for (String metadataValue : values) {
                                metadataValues.add(metadataValue != null
                                        ? URLDecoder.decode(metadataValue, Constants.DEFAULT_ENCODING) : null);
                            }
                            value = metadataValues;
                        } else {
                            value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
                        }
                    } catch (UnsupportedEncodingException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
                } else if (key.toLowerCase().startsWith(Constants.WOS_HEADER_PREFIX)) {
                    try {
                        if (key.toLowerCase().startsWith(Constants.WOS_HEADER_META_PREFIX)) {
                            key = key.substring(Constants.WOS_HEADER_META_PREFIX.length(), key.length());
                            key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
                            if (log.isDebugEnabled()) {
                                log.debug("Removed meatadata header prefix " + Constants.WOS_HEADER_META_PREFIX
                                        + " from key: " + key + "=>" + key);
                            }
                        } else {
                            key = key.substring(Constants.WOS_HEADER_PREFIX.length(), key.length());
                        }
                        if (value instanceof List) {
                            List<String> metadataValues = new ArrayList<String>(values.size());
                            for (String metadataValue : values) {
                                metadataValues.add(metadataValue != null
                                        ? URLDecoder.decode(metadataValue, Constants.DEFAULT_ENCODING) : null);
                            }
                            value = metadataValues;
                        } else {
                            value = URLDecoder.decode(value.toString(), Constants.DEFAULT_ENCODING);
                        }
                    } catch (UnsupportedEncodingException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
                } else if (Constants.ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES
                        .contains(key.toLowerCase(Locale.getDefault()))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Leaving HTTP header item unchanged: " + key + "=" + values);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring metadata item: " + key + "=" + values);
                    }
                    continue;
                }
                cleanMap.put(key, value);
            }
        }
        return cleanMap;
    }

    public static Map<String, String> cleanRestMetadataMapV2(Map<String, String> metadata, String headerPrefix,
            String metadataPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("Cleaning up REST metadata items");
        }
        Map<String, String> cleanMap = new IdentityHashMap<String, String>();

        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Trim prefixes from keys.
                key = key != null ? key : "";
                if (key.toLowerCase().startsWith(headerPrefix)) {
                    try {
                        if (key.toLowerCase().startsWith(metadataPrefix)) {
                            key = key.substring(metadataPrefix.length(), key.length());
                            key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
                            if (log.isDebugEnabled()) {
                                log.debug("Removed meatadata header prefix " + metadataPrefix + " from key: " + key
                                        + "=>" + key);
                            }
                        } else {
                            key = key.substring(headerPrefix.length(), key.length());
                        }
                        value = URLDecoder.decode(value, Constants.DEFAULT_ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }

                } else if (key.toLowerCase().startsWith(Constants.WOS_HEADER_PREFIX)) {
                    try {
                        if (key.toLowerCase().startsWith(Constants.WOS_HEADER_META_PREFIX)) {
                            key = key.substring(Constants.WOS_HEADER_META_PREFIX.length(), key.length());
                            key = URLDecoder.decode(key, Constants.DEFAULT_ENCODING);
                            if (log.isDebugEnabled()) {
                                log.debug("Removed meatadata header prefix " + Constants.WOS_HEADER_META_PREFIX
                                        + " from key: " + key + "=>" + key);
                            }
                        } else {
                            key = key.substring(Constants.WOS_HEADER_PREFIX.length(), key.length());
                        }
                        value = URLDecoder.decode(value, Constants.DEFAULT_ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error to decode value of key:" + key);
                        }
                    }
                } else if (Constants.ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES
                        .contains(key.toLowerCase(Locale.getDefault()))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Leaving HTTP header item unchanged: " + key + "=" + value);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring metadata item: " + key + "=" + value);
                    }
                    continue;
                }

                // FIXME
                cleanMap.put(new StringBuilder(key).toString(), value);
            }
        }
        return cleanMap;
    }

    public static String toHex(byte[] data) {
        if (null == data) {
            return null;
        }
        if (data.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < data.length; i++) {
            String hv = Integer.toHexString(data[i]);
            if (hv.length() < 2) {
                sb.append("0");
            } else if (hv.length() == 8) {
                hv = hv.substring(6);
            }
            sb.append(hv);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    public static byte[] fromHex(String hexData) {
        if (null == hexData) {
            return null;
        }

        if ((hexData.length() & 1) != 0 || hexData.replaceAll("[a-fA-F0-9]", "").length() > 0) {
            throw new IllegalArgumentException("'" + hexData + "' is not a hex string");
        }

        byte[] result = new byte[(hexData.length() + 1) / 2];
        String hexNumber = null;
        int offset = 0;
        int byteIndex = 0;
        while (offset < hexData.length()) {
            hexNumber = hexData.substring(offset, offset + 2);
            offset += 2;
            result[byteIndex++] = (byte) Integer.parseInt(hexNumber, 16);
        }
        return result;
    }

    public static String toBase64(byte[] data) {
        return ReflectUtils.toBase64(data);
    }

    public static String join(Object[] items, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            sb.append(items[i]);
            if (i < items.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(List<?> items, String delimiter, boolean needTrim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i).toString();
            sb.append(needTrim ? item.trim() : item);
            if (i < items.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(List<?> items, String delimiter) {
        return join(items, delimiter, false);
    }

    public static String join(Headers headers, String delimiter, List<String> excludes) {
        if (excludes == null) {
            excludes = new ArrayList<String>();
        }
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> map = headers.toMultimap();
        int i = 0;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (!excludes.contains(entry.getKey())) {
                sb.append(entry.getValue());
            }
            if (i < map.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(int[] ints, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.length; i++) {
            sb.append(ints[i]);
            if (i < ints.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static byte[] fromBase64(String b64Data) throws UnsupportedEncodingException {
        return ReflectUtils.fromBase64(b64Data);
    }

    public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            return messageDigest.digest();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e);
                    }
                }
            }
        }
    }

    public static byte[] computeMD5Hash(InputStream is, long length, long offset)
            throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            if (offset > 0) {
                long skipByte = bis.skip(offset);
                if (log.isDebugEnabled()) {
                    log.debug("computeMD5Hash: Skip " + skipByte + " bytes");
                }
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            long readLen = 0;
            long bufLen = 16384 > length ? length : 16384;
            while (readLen < length && (bytesRead = bis.read(buffer, 0, (int) bufLen)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
                readLen += bytesRead;
                bufLen = (length - readLen) > 16384 ? 16384 : (length - readLen);
            }
            return messageDigest.digest();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e);
                    }
                }
            }
        }
    }

    public static String computeMD5(String data) throws ServiceException {
        try {
            return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(data.getBytes(Constants.DEFAULT_ENCODING)));
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        } catch (IOException e) {
            throw new ServiceException("Failed to get MD5 for requestXmlElement:" + data);
        }
    }

    public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
        return computeMD5Hash(new ByteArrayInputStream(data));
    }

    public static boolean isBucketNameValidDNSName(String bucketName) {
        if (bucketName == null || bucketName.length() > 63 || bucketName.length() < 3) {
            return false;
        }

        if (!Pattern.matches("^[a-z0-9][a-z0-9.-]+$", bucketName)) {
            return false;
        }

        if (Pattern.matches("(\\d{1,3}\\.){3}\\d{1,3}", bucketName)) {
            return false;
        }

        String[] fragments = bucketName.split("\\.");
        for (int i = 0; i < fragments.length; i++) {
            if (Pattern.matches("^-.*", fragments[i]) || Pattern.matches(".*-$", fragments[i])
                    || Pattern.matches("^$", fragments[i])) {
                return false;
            }
        }

        return true;
    }

    public static String generateHostnameForBucket(String bucketName, boolean pathStyle, String endpoint) {
        if (!isBucketNameValidDNSName(bucketName)) {
            throw new IllegalArgumentException("the bucketName is illegal");
        }

        if (!pathStyle) {
            return bucketName + "." + endpoint;
        } else {
            return endpoint;
        }
    }

    public static XMLReader loadXMLReader() throws ServiceException {
        Exception ex;
        try {
            return XMLReaderFactory.createXMLReader();
        } catch (Exception e) {
            // Ignore failure
            ex = e;
        }

        // No dice using the standard approach, try loading alternatives...
        // JDK 1.4 and Android
        String[] altXmlReaderClasspaths = new String[]{"org.apache.crimson.parser.XMLReaderImpl",
            "org.xmlpull.v1.sax2.Driver"
        };
        for (int i = 0; i < altXmlReaderClasspaths.length; i++) {
            String xmlReaderClasspath = altXmlReaderClasspaths[i];
            try {
                return XMLReaderFactory.createXMLReader(xmlReaderClasspath);
            } catch (Exception e) {
                // Ignore failure
            }
        }
        // If we haven't found and returned an XMLReader yet, give up.
        throw new ServiceException("Failed to initialize a SAX XMLReader", ex);
    }

    public static SimpleDateFormat getShortDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.SHORT_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }

    public static SimpleDateFormat getLongDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.LONG_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }

    public static SimpleDateFormat getHeaderDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.HEADER_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }

    public static SimpleDateFormat getExpirationDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.EXPIRATION_DATE_FORMATTER);
        format.setTimeZone(Constants.GMT_TIMEZONE);
        return format;
    }

    public static WosException changeFromServiceException(ServiceException se) {
        WosException exception;
        if (se.getResponseCode() < 0) {
            exception = new WosException("WOS servcie Error Message. " + se.getMessage(), se.getCause());
        } else {
            exception = new WosException(
                    (se.getMessage() != null ? "Error message:" + se.getMessage() : "") + "WOS servcie Error Message.",
                    se.getXmlMessage(), se.getCause());
            exception.setErrorCode(se.getErrorCode());
            exception.setErrorMessage(se.getErrorMessage() == null ? se.getMessage() : se.getErrorMessage());
            exception.setErrorRequestId(se.getErrorRequestId());
            exception.setErrorHostId(se.getErrorHostId());
            exception.setResponseCode(se.getResponseCode());
            exception.setResponseStatus(se.getResponseStatus());
            exception.setResponseHeaders(se.getResponseHeaders());
            exception.setErrorIndicator(se.getErrorIndicator());
        }
        return exception;
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                if (log.isWarnEnabled()) {
                    log.warn(e);
                }
            }
        }
    }

    public static String toString(InputStream in) throws IOException {
        String ret = null;
        if (in != null) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(in, Constants.DEFAULT_ENCODING));
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                ret = sb.toString();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        if (log.isWarnEnabled()) {
                            log.warn(e);
                        }
                    }
                }
                closeStream(in);
            }
        }
        return ret;
    }

    public static WosProperties changeFromWosConfiguration(WosConfiguration config) {
        WosProperties wosProperties = new WosProperties();

        String endPoint = config.getEndPoint();

        int index;
        while ((index = endPoint.lastIndexOf("/")) == endPoint.length() - 1) {
            endPoint = endPoint.substring(0, index);
        }

        if (endPoint.startsWith("http://")) {
            config.setHttpsOnly(false);
            endPoint = endPoint.substring("http://".length());
        } else if (endPoint.startsWith("https://")) {
            config.setHttpsOnly(true);
            endPoint = endPoint.substring("https://".length());
        }

        if ((index = endPoint.lastIndexOf(":")) > 0) {
            int port = Integer.parseInt(endPoint.substring(index + 1));
            if (config.isHttpsOnly()) {
                config.setEndpointHttpsPort(port);
            } else {
                config.setEndpointHttpPort(port);
            }
            endPoint = endPoint.substring(0, index);
        }

        Matcher m = pattern.matcher(endPoint);
        if (m.matches()) {
            config.setPathStyle(true);
        }

        if (config.isPathStyle() || config.isCname()) {
            config.setAuthTypeNegotiation(false);
            config.setAuthType(AuthTypeEnum.V4);
        }

        config.setEndPoint(endPoint);
        wosProperties.setProperty(WosConstraint.END_POINT, config.getEndPoint());
        wosProperties.setProperty(WosConstraint.HTTP_PORT, String.valueOf(config.getEndpointHttpPort()));
        wosProperties.setProperty(WosConstraint.HTTPS_ONLY, String.valueOf(config.isHttpsOnly()));
        wosProperties.setProperty(WosConstraint.DISABLE_DNS_BUCKET, String.valueOf(config.isPathStyle()));
        wosProperties.setProperty(WosConstraint.HTTPS_PORT, String.valueOf(config.getEndpointHttpsPort()));
        wosProperties.setProperty(WosConstraint.HTTP_SOCKET_TIMEOUT, String.valueOf(config.getSocketTimeout()));
        wosProperties.setProperty(WosConstraint.HTTP_MAX_CONNECT, String.valueOf(config.getMaxConnections()));
        wosProperties.setProperty(WosConstraint.HTTP_RETRY_MAX, String.valueOf(config.getMaxErrorRetry()));
        wosProperties.setProperty(WosConstraint.HTTP_CONNECT_TIMEOUT, String.valueOf(config.getConnectionTimeout()));
        wosProperties.setProperty(WosConstraint.PROXY_ISABLE, String.valueOf(Boolean.FALSE));
        wosProperties.setProperty(WosConstraint.BUFFER_STREAM,
                String.valueOf(config.getUploadStreamRetryBufferSize() > 0 ? config.getUploadStreamRetryBufferSize()
                        : WosConstraint.DEFAULT_BUFFER_STREAM));
        wosProperties.setProperty(WosConstraint.VALIDATE_CERTIFICATE, String.valueOf(config.isValidateCertificate()));
        wosProperties.setProperty(WosConstraint.VERIFY_RESPONSE_CONTENT_TYPE,
                String.valueOf(config.isVerifyResponseContentType()));
        wosProperties.setProperty(WosConstraint.WRITE_BUFFER_SIZE, String.valueOf(config.getWriteBufferSize()));
        wosProperties.setProperty(WosConstraint.READ_BUFFER_SIZE, String.valueOf(config.getReadBufferSize()));
        wosProperties.setProperty(WosConstraint.SOCKET_WRITE_BUFFER_SIZE,
                String.valueOf(config.getSocketWriteBufferSize()));
        wosProperties.setProperty(WosConstraint.SOCKET_READ_BUFFER_SIZE,
                String.valueOf(config.getSocketReadBufferSize()));
        wosProperties.setProperty(WosConstraint.HTTP_STRICT_HOSTNAME_VERIFICATION,
                String.valueOf(config.isStrictHostnameVerification()));
        wosProperties.setProperty(WosConstraint.HTTP_IDLE_CONNECTION_TIME,
                String.valueOf(config.getIdleConnectionTime()));
        wosProperties.setProperty(WosConstraint.HTTP_MAX_IDLE_CONNECTIONS,
                String.valueOf(config.getMaxIdleConnections()));
        wosProperties.setProperty(WosConstraint.SSL_PROVIDER,
                config.getSslProvider() == null ? "" : config.getSslProvider());
        wosProperties.setProperty(WosConstraint.KEEP_ALIVE, String.valueOf(config.isKeepAlive()));
        wosProperties.setProperty(WosConstraint.FS_DELIMITER,
                config.getDelimiter() == null ? "/" : config.getDelimiter());
        wosProperties.setProperty(WosConstraint.HTTP_PROTOCOL, config.getHttpProtocolType() == null
                ? HttpProtocolTypeEnum.HTTP1_1.getCode() : config.getHttpProtocolType().getCode());

        wosProperties.setProperty(WosConstraint.IS_CNAME, String.valueOf(config.isCname()));
        wosProperties.setProperty(WosConstraint.AUTH_TYPE_NEGOTIATION, String.valueOf(config.isAuthTypeNegotiation()));
        if (null != config.getHttpProxy()) {
            wosProperties.setProperty(WosConstraint.PROXY_ISABLE, String.valueOf(Boolean.TRUE));
            wosProperties.setProperty(WosConstraint.PROXY_HOST, config.getHttpProxy().getProxyAddr());
            wosProperties.setProperty(WosConstraint.PROXY_PORT, String.valueOf(config.getHttpProxy().getProxyPort()));
            wosProperties.setProperty(WosConstraint.PROXY_UNAME, config.getHttpProxy().getProxyUName());
            wosProperties.setProperty(WosConstraint.PROXY_PAWD, config.getHttpProxy().getUserPaaswd());
            wosProperties.setProperty(WosConstraint.PROXY_DOMAIN, config.getHttpProxy().getDomain());
            wosProperties.setProperty(WosConstraint.PROXY_WORKSTATION, config.getHttpProxy().getWorkstation());
        }

        if (config instanceof ExtWosConfiguration) {
            // retry in okhttp
            wosProperties.setProperty(ExtWosConstraint.IS_RETRY_ON_CONNECTION_FAILURE_IN_OKHTTP,
                    String.valueOf(((ExtWosConfiguration) config).isRetryOnConnectionFailureInOkhttp()));

            // retry on unexpected end exception
            wosProperties.setProperty(ExtWosConstraint.HTTP_MAX_RETRY_ON_UNEXPECTED_END_EXCEPTION,
                    String.valueOf(((ExtWosConfiguration) config).getMaxRetryOnUnexpectedEndException()));
        }

        return wosProperties;
    }

    public static Date cloneDateIgnoreNull(Date date) {
        if (null == date) {
            return null;
        } else {
            return (Date) date.clone();
        }
    }

    public static void deleteFileIgnoreException(String path) {
        if (null == path) {
            return;
        }

        if (!deleteFileIgnoreException(new File(path))) {
            log.warn("delete file '" + path + "' failed");
        }
    }

    public static boolean deleteFileIgnoreException(File file) {
        if (null == file) {
            return true;
        }

        if (file.exists() && file.isFile()) {
            return file.delete();
        }

        return true;
    }
}
