package com.acando.newshunter.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.acando.newshunter.Global;
import com.acando.newshunter.R;
import com.acando.newshunter.UtilNetwork;
import com.acando.newshunter.internal.ListActivity;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This task is used for loading the images for the lists inside the app. Each entry of the list
 * will be added to this task as soon as the array adapter has created it's View.
 */
public class ViewImageTask extends AsyncTask<Void, Void, Void> {

    protected boolean mIsPaused;
    private static final String WAIT_LOCK = "LOCK";
    private Context mContext;
    private ConcurrentLinkedQueue<Long> mOpenItems;
    private ConcurrentHashMap<Long, String> mItems;
    private ConcurrentHashMap<Long, ImageView> mItemView;

    public ViewImageTask(Context context) {
        mContext = context;
        mOpenItems = new ConcurrentLinkedQueue<>();
        mItemView = new ConcurrentHashMap<>();
        mItems = new ConcurrentHashMap<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ListActivity activity = (ListActivity) mContext;

        while (!isCancelled()) {
            if (!mOpenItems.isEmpty()) {
                long key = mOpenItems.poll();
                String teaserURL = mItems.get(key);

                if (teaserURL != null) {
                    try {
                        Bitmap bitmap = ((Global) activity.getApplication()).getBitmapFromMemCache(teaserURL);
                        if (bitmap == null || bitmap.isRecycled()) {
                            if (UtilNetwork.isNetworkAvailable(mContext)) {
                                bitmap = UtilNetwork.getImage(teaserURL);
                                ((Global) activity.getApplication()).addBitmapToMemoryCache(teaserURL, bitmap);
                            } else {
                                throw new IOException("No internet connection.");
                            }
                        }
                        boundImageToView(activity, bitmap, key);
                    } catch (Exception ex) {
                        Log.d(ViewImageTask.class.getName(), "Error during image load. " + ex.getMessage());
                        ex.getStackTrace();
                        mOpenItems.add(key);
                        pause(1000);
                    }
                }
            } else {
                pause(5000);
            }
        }

        return null;
    }

    /**
     * Executes this part of the task on the gui thread to update the ImageView of a certain entry.
     * At the end, the task will delete the keys and values from their lists.
     *
     * @param activity activity which is used for the gui thread and for the views
     * @param image    image which should be initialised on the view
     * @param key      key which has been used for this round. via this key, the remaining objects are
     *                 deleted
     */
    private void boundImageToView(final ListActivity activity, final Bitmap image, final long key) {
        if (!isCancelled()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (image != null) {
                        mItemView.get(key).setImageDrawable(new BitmapDrawable(mContext.getResources(), image));
                    } else {
                        mItemView.get(key).setImageDrawable(new ColorDrawable(ContextCompat.getColor(
                                mContext, R.color.colorAccent)));
                    }

                    mItemView.remove(key);
                    mItems.remove(key);

                    AbsListView list = (ListView) activity.findViewById(R.id.list);
                    ((ArrayAdapter) list.getAdapter()).notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * This method adds new items to the lists. The key for one entry is based on the current time.
     * After adding all objects to the lists, the task will be resume if it is currently paused.
     * At the very end, the task will be paused for a millisecond. This ensures that the key stays
     * unique. The reason is the cpu on some devices which is simply to fast and is adding items to
     * this task in a time difference under one millisecond.
     *
     * @param item InfoItem which should be used for the teaserURL
     * @param view ImageView which should be used to display the new image
     */
    public synchronized void addItem(NewsEntry item, ImageView view) {
        if (item != null && item.imageURL != null && !item.imageURL.trim().equals("")) {
            long key = System.currentTimeMillis();
            mItemView.put(key, view);
            mItems.put(key, item.imageURL);
            mOpenItems.add(key);

            if (mIsPaused) {
                resume();
            }

            pause(1);
        }
    }

    protected void pause(long millis) {
        try {
            mIsPaused = true;
            synchronized (WAIT_LOCK) {
                WAIT_LOCK.wait(millis);
            }
        } catch (InterruptedException e) {
            Log.d(ViewImageTask.class.getName(), e.getMessage());
            e.printStackTrace();
        }
        mIsPaused = false;
    }

    /**
     * Resumes the task if it is sleeping.
     */
    protected void resume() {
        synchronized (WAIT_LOCK) {
            WAIT_LOCK.notify();
        }
    }
}
