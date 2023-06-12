package com.wos.services.internal.utils;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class EtagUtils {
    // 目录固定etag值
    public static final String DIRECTORY_FIXED_ETAG_VALUE = "d41d8cd98f00b204e9800998ecf8427e";
    private static final ILogger logger = LoggerBuilder.getLogger(EtagUtils.class);

    public static String getFileEtag(String bucketName, String fileName, File file) {
        if (file.isDirectory()) {
            return "\"" + DIRECTORY_FIXED_ETAG_VALUE + "\"";
        }
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        String etagHash = encoderByMd5Of32(data);
        if (StringUtils.isBlank(etagHash)) {
            etagHash = getSignatureHmacSHA1((bucketName + ":" + fileName).getBytes(), "file");
        }
        etagHash = "\"" + etagHash + "\"";
        return etagHash;
    }

    public static String getFileEtagByMultiPart(File file, long partSize) throws IOException {
        if (file.isDirectory()) {
            logger.warn("file is directory, please use simple upload!");
            return null;
        }
        List<String> md5s = new ArrayList<>();
        long fileLength = file.length();
        long partCount = fileLength % partSize == 0 ? fileLength / partSize : fileLength / partSize + 1;
        for (int i = 0; i < partCount; i++) {
            long offset = i * partSize;
            long currPartSize = (i + 1 == partCount) ? fileLength - offset : partSize;
            String etag = getPartEtag(file, offset, currPartSize);
            md5s.add(etag.replaceAll("\"", ""));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String md5 : md5s) {
            stringBuilder.append(md5);
        }

        String hex = stringBuilder.toString();
        byte[] raw = null;
        try {
            raw = BaseEncoding.base16().decode(hex.toUpperCase());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        Hasher hasher = Hashing.md5().newHasher();
        hasher.putBytes(raw);
        String digest = hasher.hash().toString();

        return digest + "-" + md5s.size();
    }

    private static String getPartEtag(File file, long offset, long currPartSize) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) currPartSize];
        inputStream.skip(offset);
        inputStream.read(buffer);
        byte[] bytes = encoderByMd5Of32(buffer).getBytes();
        // 处理读取的数据块
        return new String(bytes);
    }

    public static String encoderByMd5Of32(byte[] str) {
        //确定计算方法
        MessageDigest md5 = null;
        String newstr = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(str);
            byte[] md5String = md5.digest();
            //加密后的字符串
            int i;
            StringBuilder buf = new StringBuilder();
            for (int offset = 0; offset < md5String.length; offset++) {
                i = md5String[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            newstr = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return newstr;
    }

    public static String getSignatureHmacSHA1(byte[] data, String key) {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac mac;
        StringBuilder sb = new StringBuilder();
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data);

            for (byte b : rawHmac) {
                sb.append(byteToHexString(b));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    private static String byteToHexString(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0f];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

}