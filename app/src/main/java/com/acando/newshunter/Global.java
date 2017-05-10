package com.acando.newshunter;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.nio.ByteBuffer;

/**
 * The application Global handles the caching of this project. The app caches the images and api
 * responds. The api responds are only stored for a limited time.
 */
public class Global extends Application {

    /**
     * Indicates the time in milliseconds that stores an api respond.
     */
    private static final int DATA_TIME_DIFF = 120000;

    /**
     * Value that represents that max memory of the device. This value is used to calculate the
     * cache size for each part.
     */
    private static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);

    /**
     * Cache structure which saves the images of the app. Key is the image url.
     */
    private LruCache<String, Bitmap> mImageCache;

    /**
     * Cache which saves the json responds. Key is the url which has been used.
     */
    private LruCache<String, String> mDataCache;

    /**
     * Cache which saves the time stamp for the json responds. Key is the same url as the related
     * json respond entry.
     */
    private LruCache<String, Long> mDataTimeCache;

    /**
     * Initialise the data caching for the app. This part will take up to 1/16 of the memory.
     */
    public void initDataCaching() {
        final int cacheSize = MAX_MEMORY / 16;

        mDataCache = new LruCache<String, String>(cacheSize) {
            @Override
            protected int sizeOf(String key, String data) {
                return data.getBytes().length / 1024;
            }
        };

        mDataTimeCache = new LruCache<String, Long>(cacheSize) {
            @Override
            protected int sizeOf(String key, Long data) {
                byte[] bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(data).array();
                return bytes.length / 1024;
            }
        };
    }

    /**
     * Adds a new json respond to the cache, using the url as key. This will also initialise the
     * time cache which is used to track the remaining time the respond is saved.
     *
     * @param key  url of the json respond
     * @param data json respond as string
     */
    public void addDataToMemoryCache(String key, String data) {
        if (getDataFromMemCache(key) == null) {
            mDataTimeCache.put(key, System.currentTimeMillis());
            mDataCache.put(key, data);
        }
    }

    /**
     * Returns a json respond as a string from the key using a key. The cache only returns the
     * object if it's existing and the related time is still valid.
     *
     * @param key url of the json respond
     * @return json respond as string
     */
    public String getDataFromMemCache(String key) {
        if (mDataCache != null && mDataTimeCache != null) {
            Long time = mDataTimeCache.get(key);
            if (time != null && System.currentTimeMillis() - time < DATA_TIME_DIFF) {
                return mDataCache.get(key);
            } else {
                mDataTimeCache.remove(key);
                mDataCache.remove(key);
                return null;
            }
        } else {
            initDataCaching();
            return null;
        }
    }

    /**
     * Initialise the image caching. This takes up to 1/8 of the device memory.
     */
    public void initImageCaching() {
        final int cacheSize = MAX_MEMORY / 8;

        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * Adds a new image to the cache using the image url.
     *
     * @param key    image url for the key
     * @param bitmap image to be stored
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mImageCache.put(key, bitmap);
        }
    }

    /**
     * Returns image from cache if it is existing.
     *
     * @param key url of the possible cached image
     * @return image which belongs to the given url
     */
    public Bitmap getBitmapFromMemCache(String key) {
        if (mImageCache != null) {
            return mImageCache.get(key);
        } else {
            initImageCaching();
            return null;
        }
    }

    /**
     * Clears the cache which has been stored.
     */
    public void deleteCache() {
        if (mImageCache != null) {
            mImageCache.evictAll();
        }

        if (mDataCache != null) {
            mDataCache.evictAll();
        }

        if (mDataTimeCache != null) {
            mDataTimeCache.evictAll();
        }

        mImageCache = null;
        mDataCache = null;
        mDataTimeCache = null;
    }
}
