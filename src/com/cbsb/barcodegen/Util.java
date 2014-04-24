package com.cbsb.barcodegen;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.cbsb.barcodegen.R;

public class Util {
	private static final String TAG = "Util";

	public static int computeSampleSize(BitmapFactory.Options options, int target) {

		int source = Math.max(options.outWidth, options.outHeight);
		int sample = 1;
		while (source/(sample<<1) > target) {
			sample <<= 1;
		}
		return sample;
	}

	public static Bitmap getBitmap(ContentResolver cr, Uri uri, int target) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		Bitmap bm = null;
		try {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(
					new BufferedInputStream(cr.openInputStream(uri)),
					null,
					options);

			int sample = computeSampleSize(options, target);
			options.inJustDecodeBounds = false;
			options.inSampleSize = sample;
			bm = BitmapFactory.decodeStream(
					new BufferedInputStream(cr.openInputStream(uri)),
					null,
					options);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFound "+uri);
		} catch (Exception e) {
			Log.e(TAG, "Unexpected exception", e);
		}

		return bm;
	}

	public static boolean equals(String a, String b) {
		return a == b || a.equals(b);
	}
}
