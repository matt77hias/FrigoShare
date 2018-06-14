package com.frigoshare.cache;

public final class DataTools {

    private static DataCache currentDataCache = new DataCache();

    public static DataCache getCurrentDataCache() {
        return currentDataCache;
    }

    public static void cleanData() {
        getCurrentDataCache().clean();
    }

    public static void fetchData() {
        getCurrentDataCache().fetch();
    }
}
