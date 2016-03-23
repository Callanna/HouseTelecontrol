/**********************************************************************
 * AUTHOR：YOLANDA
 * DATE：2015年2月27日上午10:05:15
 * Copyright © 56iq. All Rights Reserved
 * ======================================================================
 * EDIT HISTORY
 * ----------------------------------------------------------------------
 * |  DATE      | NAME       | REASON       | CHANGE REQ.
 * ----------------------------------------------------------------------
 * | 2015年2月27日    | YOLANDA    | Created      |
 * <p/>
 * DESCRIPTION：create the File, and add the content.
 ***********************************************************************/
package com.github.callanna.metarialframe.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author YOLANDA
 * @Time 2015年2月27日 上午10:05:15
 */
public class StringUtil {
    private static String hexString = "0123456789ABCDEF";

    private static byte charToByte(int c) {
        return (byte) hexString.indexOf(c);
    }

    /**
     * byte转16进制
     *
     * @param src
     * @return
     * @author YOLANDA
     */
    public static String byteToHex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte[]转Hex字符串
     *
     * @param b
     * @return
     * @author YOLANDA
     */
    public static String byte2Hex(byte[] b) {
        if (null == b)
            return null;
        StringBuffer sBuffer = new StringBuffer();
        String sTmep;
        for (int i = 0; i < b.length; i++) {
            sTmep = Integer.toHexString(b[i] & 0xFF);
            if (sTmep.length() == 1)
                sBuffer.append("0");
            sBuffer.append(sTmep.toUpperCase(Locale.getDefault()));
        }
        return sBuffer.toString();
    }

    /**
     * 16进制转byte
     *
     * @param hexString
     * @return
     * @author YOLANDA
     */
    public static byte[] hexToByte(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase(Locale.getDefault());
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Hex字符串转byte[]
     *
     * @param hexString
     * @return
     * @author YOLANDA
     */
    public static byte[] hex2Byte(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        if (hexString.length() % 2 != 0) {
            hexString = hexString.substring(0, hexString.length() - 1) + "0" + hexString.substring(hexString.length() - 1, hexString.length());
        }
        int length = hexString.length() / 2;
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int k = Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
            b[i] = (byte) k;
        }
        return b;
    }

    /**
     * String转16进制
     *
     * @param str
     * @return
     * @author YOLANDA
     */
    public static String stringToHex(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 16进制转String
     *
     * @return
     * @author YOLANDA
     */
    public static String hexToString(String hexStr) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hexStr.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < hexStr.length(); i += 2) {
            try {
                baos.write((hexString.indexOf(hexStr.charAt(i)) << 4 | hexString.indexOf(hexStr.charAt(i + 1))));
            } catch (StringIndexOutOfBoundsException e) {
                return "非16进制数据";
            }
        }
        return new String(baos.toByteArray());
    }

    /**
     * 生成变色文字
     *
     * @param msg
     * @param color
     * @param start
     * @param end
     * @return
     * @author YOLANDA
     */
    public static SpannableString getSpannableStringColor(CharSequence msg, int color, int start, int end) {
        SpannableString builder = new SpannableString(msg);
        builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private static final String HMAC_SHA1 = "HmacSHA1";

    /**
     * 生成签名数据
     *
     * @param data 待加密的数据
     * @param key  加密使用的key
     * @return 生成MD5编码的字符串
     */
    public static byte[] getSignature(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (Throwable e) {
            return new byte[]{};
        }
    }

    /**
     * 获取文件MD5
     *
     * @param file
     * @return
     * @throws Exception
     * @author YOLANDA
     */
    public static String getFileMD5Value(File file) throws Exception {
        InputStream fis;
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return byte2Hex(messagedigest.digest());
    }

    /**
     * 获取字符串的MD5值
     *
     * @param string
     * @return
     * @author YOLANDA
     */
    public static String getStringMD5Value(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        return byte2Hex(hash);
    }

    /**
     * RC4加密,解密
     *
     * @param aInput
     * @param aKey
     * @return
     * @author YOLANDA
     */
    public static String parseOrCreateRC4(String aInput, String aKey) {
        int[] iS = new int[256];
        byte[] iK = new byte[256];

        for (int i = 0; i < 256; i++)
            iS[i] = i;

        int j = 1;

        for (short i = 0; i < 256; i++) {
            iK[i] = (byte) aKey.charAt((i % aKey.length()));
        }

        j = 0;

        for (int i = 0; i < 255; i++) {
            j = (j + iS[i] + iK[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
        }

        int i = 0;
        j = 0;
        char[] iInputChar = aInput.toCharArray();
        char[] iOutputChar = new char[iInputChar.length];
        for (short x = 0; x < iInputChar.length; x++) {
            i = (i + 1) % 256;
            j = (j + iS[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
            int t = (iS[i] + (iS[j] % 256)) % 256;
            int iY = iS[t];
            char iCY = (char) iY;
            iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
        }
        return new String(iOutputChar);
    }

    /**
     * 验证是否是URL
     *
     * @param url
     * @return
     * @author YOLANDA
     */
    public static boolean isTrueURL(String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(url);
        return matcher.matches();
    }

    /**
     * 验证是ipv4地址
     *
     * @param ipAddress
     * @return
     * @author YOLANDA
     */
    public static boolean isIpv4(String ipAddress) {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * 得到IP最后一段
     *
     * @param ip
     * @return
     * @author YOLANDA
     */
    public static String getIpLastNumber(String ip) {
        int i = ip.lastIndexOf(".") + 1;
        return ip.substring(i, ip.length());
    }

    /**
     * 得到加冒号 或 取去掉冒号的Mac地址
     *
     * @param mac
     * @return
     * @author YOLANDA
     */
    public static String getMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return null;
        } else if (mac.contains(":")) {
            mac = mac.replace(":", "");
        } else {
            String[] macs = new String[6];
            for (int i = 0; i <= 5; i++) {
                macs[i] = mac.substring(i * 2, i * 2 + 2);
            }
            mac = macs[0];
            for (int i = 1; i < macs.length; i++) {
                mac += ":" + macs[i];
            }
        }
        return mac;
    }

    /**
     * Mac加法
     *
     * @param mac Mac地址，eg：ABCDEF56BFD0
     * @param add 要加的数
     * @return
     * @author YOLANDA
     */
    public static String getMacAdd(String mac, int add) {
        return Long.toHexString(Long.parseLong(mac, 16) + add).toUpperCase(Locale.getDefault());
    }

    /**
     * Mac减法
     *
     * @param mac   Mac地址：eg：ABCDEF56BFD0
     * @param minus 要减的数
     * @return
     * @author YOLANDA
     */
    public static String getMacMinus(String mac, int minus) {
        return Long.toHexString(Long.parseLong(mac, 16) - minus).toUpperCase(Locale.getDefault());
    }

    /**
     * Mac尾数加法
     *
     * @param mac Mac地址：CAFDEFB469DF
     * @param add 要加的数
     * @return 返回为数加 减后的Mac地址
     * @author YOLANDA
     */
    public static String getMacLastAdd(String mac, int add) {
        String lastChar = mac.substring(mac.length() - 1).toUpperCase(Locale.getDefault());
        mac = mac.substring(0, mac.length() - 1);
        if ("F".equals(lastChar)) {
            lastChar = "0";
        } else {
            int tempChar = Integer.parseInt(lastChar, 16) + add;
            lastChar = Integer.toHexString(tempChar).toUpperCase(Locale.getDefault());
        }
        return (mac + lastChar);
    }

    /**
     * Mac尾数减法
     *
     * @param mac   Mac地址：CAFDEFB469DF
     * @param minus 要减的数
     * @return 返回为数加 减后的Mac地址
     * @author YOLANDA
     */
    public static String getMacLastMinus(String mac, int minus) {
        String lastChar = mac.substring(mac.length() - 1).toUpperCase(Locale.getDefault());
        mac = mac.substring(0, mac.length() - 1);
        if ("0".equals(lastChar)) {
            lastChar = "F";
        } else {
            int tempChar = Integer.parseInt(lastChar, 16) - minus;
            lastChar = Integer.toHexString(tempChar).toUpperCase(Locale.getDefault());
        }
        return (mac + lastChar);
    }

    /**
     * 判断字符是否是中文
     *
     * @param c
     * @return
     * @author YOLANDA
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName
     * @return
     * @author YOLANDA
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否是合法的文件名，需要扩展名
     *
     * @param fileName
     * @return
     * @author YOLANDA
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255) {
            return false;
        } else {
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }

    /**
     * 字符串是否包含字母、数字、下划线、减号
     *
     * @param str
     * @return
     * @author YOLANDA
     */
    public static boolean isValidString(String str) {
        boolean result = false;
        Pattern pt = Pattern.compile("^[0-9a-zA-Z_-]+$");
        Matcher mt = pt.matcher(str);
        if (mt.matches()) {
            result = true;
        }
        return result;
    }

    /**
     * 是否是正确的JSON
     *
     * @param content
     * @return
     * @author YOLANDA
     */
    public static JSONObject isTrueJson(String content) {
        JSONObject jsonObject = null;
        try {
            if (null != content) {
                jsonObject = new JSONObject(content);
            }
        } catch (JSONException e) {
            if (LogUtil.isPrint) e.printStackTrace();
        }
        return jsonObject;
    }

}
