/*******************************************************************************
 * Copyright 2014 Federico Iosue (federico.iosue@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package it.feio.android.simplegallery.util;

import it.feio.android.simplegallery.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;


public class BitmapHelper {

	/**
	 * Decodifica ottimizzata per la memoria dei bitmap
	 * 
	 * @param uri
	 *            URI bitmap
	 * @param reqWidth
	 *            Larghezza richiesta
	 * @param reqHeight
	 *            Altezza richiesta
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap decodeSampledFromUri(Context mContext, Uri uri, int reqWidth, int reqHeight)
			throws FileNotFoundException {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options);

		// Setting decode options
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		// Bitmap is now decoded for real using calculated inSampleSize
		Bitmap bmp = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options);
		return bmp;
	}
	
	
	
	/**
	 * Decoding with inJustDecodeBounds=true to check sampling index without breaking memory
	 * @param mContext
	 * @param uri
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * @throws FileNotFoundException
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
			throws FileNotFoundException {

		// Calcolo dell'inSampleSize e delle nuove dimensioni proporzionate
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
	
	
	
	public static Uri getUri(Context mContext, int resource_id) {
		Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resource_id);
		return uri;
	}
	
	
	
	/**
	 * Creates a thumbnail of requested size by doing a first sampled decoding of the bitmap to optimize memory
	 * @param ctx
	 * @param uri
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap getThumbnail(Context mContext, Uri uri, int reqWidth, int reqHeight) {

		final int TYPE_IMAGE = 0;
		final int TYPE_VIDEO = 1;
		
		Bitmap srcBmp;
		Bitmap dstBmp = null;
		
		int type = TYPE_IMAGE;
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			if (mime.getMimeTypeFromExtension(extension).contains("video/")) type = TYPE_VIDEO;
		}
			
		if (type == TYPE_IMAGE) {
			try {
				dstBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
			} catch (FileNotFoundException e) {
				Log.e("AndroidTouchGallery", "Missing attachment file: " + uri.getPath());
			}
		}
		
		else if (type == TYPE_VIDEO) {
			srcBmp = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
			dstBmp = createVideoThumbnail(mContext, srcBmp, reqWidth, reqHeight);
		}
		
		srcBmp = null;
		return dstBmp;
	}
	
	
	
	
	/**
	 * Scales a bitmap to fit required ratio
	 * @param bmp Image to be scaled
	 * @param reqWidth
	 * @param reqHeight
	 */
	@SuppressWarnings("unused")
	private static Bitmap scaleImage(Context mContext, Bitmap bitmap, int reqWidth, int reqHeight) {

		// Get current dimensions AND the desired bounding box
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int boundingX = dpToPx(mContext, reqWidth);
		int boundingY = dpToPx(mContext, reqHeight);

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundingX) / width;
		float yScale = ((float) boundingY) / height;
		float scale = (xScale >= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the
		// ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		return scaledBitmap;
	}
	
	
	
	/**
	 * To avoid problems with rotated videos retrieved from camera
	 * @param bitmap
	 * @param filePath
	 * @return
	 */
	public static Bitmap rotateImage(Bitmap bitmap, String filePath) {
		Bitmap resultBitmap = bitmap;

		try {
			ExifInterface exifInterface = new ExifInterface(filePath);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Matrix matrix = new Matrix();

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_270);
			}

			// Rotate the bitmap
			resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception exception) {
			Log.d("AndroidTouchGallery", "Could not rotate the image");
		}
		return resultBitmap;
	}
	
	
	
	
	
	public static InputStream getBitmapInputStream(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
		byte[] bitmapdata = bos.toByteArray();
		ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
		return bs;
	}
	
	

	
	/**
	 * Draws a watermark on ImageView to highlight videos
	 * 
	 * @param bmp
	 * @param overlay
	 * @return
	 */
	public static Bitmap createVideoThumbnail(Context mContext, Bitmap video, int width, int height) {
		video = ThumbnailUtils.extractThumbnail(video, width, height);
		
		Bitmap thumbnail = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(thumbnail);
		canvas.drawBitmap(video, 0, 0, null);
		
		// Movie mark
		Bitmap mark = ThumbnailUtils.extractThumbnail(
				BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.play_no_bg), 100, 100);
		int x = video.getWidth() / 2 - mark.getWidth() / 2;
		int y = video.getHeight() / 2 - mark.getHeight() / 2;
		canvas.drawBitmap(mark, x, y, null);

		return thumbnail;
	}
	
	

	
	
	
	private static int dpToPx(Context mContext, int dp) {
		float density = mContext.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
	
	
	
	
	
}
