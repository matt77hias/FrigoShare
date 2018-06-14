package com.frigoshare.utils;

public final class IdUtils {

    public static final String FACEBOOK_PREFIX = "FB#";
    public static final String GOOGLEPLUS_PREFIX = "G+#";
    public static final String TWITTER_PREFIX =  "TW#";
    public static final String UNKNOWN_PREFIX = "U#";
    public static final String TEST_PREFIX = "T#";

    public static String getIdPrefix(String provider) {
        if ("facebook".equals(provider)) {
            return FACEBOOK_PREFIX;
        } else  if ("googleplus".equals(provider)) {
            return GOOGLEPLUS_PREFIX;
        } else  if ("twitter".equals(provider)) {
            return TWITTER_PREFIX;
        } else  if ("test".equals(provider)) {
            return TEST_PREFIX;
        } else {
            return UNKNOWN_PREFIX;
        }
    }
}
