package com.ntsoft.ihhq.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.Cache;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.toolbox.ImageCache;

import java.io.File;

public  class DiskBitmapCache extends DiskBasedCache implements ImageCache {

        public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
            super(rootDirectory, maxCacheSizeInBytes);
        }

        public DiskBitmapCache(File cacheDir) {
            super(cacheDir);
        }

        public Bitmap getBitmap(String url) {
            final Cache.Entry requestedItem = get(url);

            if (requestedItem == null)
                return null;

            return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
        }

        public void putBitmap(String url, Bitmap bitmap) {

            final Cache.Entry entry = new Cache.Entry();

/*			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
             // If w & h is set during image request ( using ImageLoader ) then this is not required.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap downSized = VideoUtility.downSizeBitmap(bitmap, 50);

			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();

			System.out.println("####### Size of bitmap is ######### "+url+" : "+data.length);
	        entry.data = data ; */

            entry.data = BitmapUtility.convertBitmapToBytes(bitmap) ;
            put(url, entry);
        }

        @Override
        public void invalidateBitmap(String url) {

        }
    }