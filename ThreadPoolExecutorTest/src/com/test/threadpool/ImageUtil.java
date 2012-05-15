package com.test.threadpool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageUtil {
	private static final String LOG_TAG = "ImageDownloader";
	MyThreadPoolExecutor executor = new MyThreadPoolExecutor();
	Handler hasHandler = new Handler();

	public void loadPool(final String url, final ImageView imageView) {
		final WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(
				imageView);

		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			executor.runTask(new Runnable() {
				@Override
				public void run() {
					final Bitmap bit = downloadBitmap(url);
					addBitmapToCache(url, bit);
					hasHandler.post(new Runnable() {
						@Override
						public void run() {
							if (imageViewReference != null) {
								ImageView imageView = imageViewReference.get();
								imageView.setImageBitmap(bit);
							}
						}
					});
				}

			});

		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	public void load(String url, ImageView imageView) {

		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap == null) {
			forceDownload(url, imageView);
		} else {
			cancelPotentialDownload(url, imageView);
			// imageView.setImageBitmap(bitmap);
			Drawable Avater = new BitmapDrawable(bitmap);
			imageView.setImageDrawable(Avater);
		}
	}

	private void forceDownload(String url, ImageView imageView) {
		// State sanity: url is guaranteed to never be null in
		// DownloadedDrawable and cache keys.
		if (url == null) {
			imageView.setImageDrawable(null);
			return;
		}

		if (cancelPotentialDownload(url, imageView)) {

			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			imageView.setMinimumHeight(156);
			task.execute(url);

		}
	}

	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	public static Bitmap getBitMap(String fileFullName, int width, int height)
			throws Exception {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		FileInputStream fileInputStream = new FileInputStream(new File(
				fileFullName));
		BitmapFactory.decodeFile(fileFullName, opts);

		int be = 1;
		if (height != 0) {
			be = (int) (opts.outHeight / (float) width);
			if (be <= 0)
				be = 1;
		}
		if (width != 0) {
			be = (int) (opts.outWidth / (float) width);
			if (be <= 0)
				be = 1;
		}
		opts.inSampleSize = be;
		opts.inJustDecodeBounds = false;

		final Bitmap bm = BitmapFactory.decodeStream(fileInputStream, null,
				opts);

		fileInputStream.close();
		return bm;
	}

	Bitmap downloadBitmap(String url) {
		final int IO_BUFFER_SIZE = 4 * 1024;
		try {
			final Bitmap bit = getBitMap(url, 480, 480);
			if (bit != null) {
				return bit;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					return BitmapFactory.decodeStream(new FlushedInputStream(
							inputStream));
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			if ((client instanceof AndroidHttpClient)) {
				((AndroidHttpClient) client).close();
			}
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadBitmap(url);
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			addBitmapToCache(url, bitmap);

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

				if (this == bitmapDownloaderTask) {
					Drawable Avater = new BitmapDrawable(bitmap);
					imageView.setImageDrawable(Avater);
					// imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	static class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			super(Color.BLACK);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000;

	private final static HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {

				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param bitmap
	 *            The newly downloaded bitmap.
	 */
	public static void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}

	public static Bitmap getBitmapFromCache(String url) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
				Log.e("imageDowlnloader", "sSoftBitmapCache.remove");
			}
		}

		return null;
	}

	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
		Log.e(this.toString(), "clear");
	}

	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}
}
