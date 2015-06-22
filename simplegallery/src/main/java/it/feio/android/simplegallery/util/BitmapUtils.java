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

import android.content.res.Resources;
import android.graphics.*;
import android.provider.SyncStateContract;
import it.feio.android.simplegallery.R;

import java.io.*;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;


public class BitmapUtils {

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
	public static Bitmap decodeSampledFromUri(Context mContext, Uri uri, int reqWidth, int reqHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream inputStream = null;
		InputStream inputStreamSampled = null;
		try {
			inputStream = mContext.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(inputStream, null, options);

			// Setting decode options
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;

			// Bitmap is now decoded for real using calculated inSampleSize
			inputStreamSampled = mContext.getContentResolver().openInputStream(uri);
			return BitmapFactory.decodeStream(inputStreamSampled, null, options);
		} catch (IOException e) {
			Log.e("BitmapUtils", "Error");
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_broken);
		} finally {
			try {
				inputStream.close();
				inputStreamSampled.close();
			} catch (IOException | NullPointerException e) {
				Log.e("BitmapUtils", "Failed to close streams");
			}
		}
	}


	/**
	 * Decoding with inJustDecodeBounds=true to check sampling index without breaking memory
	 * @throws FileNotFoundException
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
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
			
//			if ( ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth)	
//				&& (halfWidth/halfHeight > 4 || halfHeight/halfWidth > 4) ){
//				inSampleSize *= 2;
//			}			
			while ( (halfHeight / inSampleSize) > reqHeight * 2 || (halfWidth / inSampleSize) > reqWidth * 2) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}


	/**
	 * Draws text on a bitmap
	 */
	public static Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap,
										  String text, Integer offsetX, Integer offsetY, float textSize,
										  Integer textColor) {
		Resources resources = mContext.getResources();
		float scale = resources.getDisplayMetrics().density;
		// Bitmap bitmap =
		// BitmapFactory.decodeResource(resources, gResId);

		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.RGB_565;
		}
		// if bitmap is not mutable a copy is done
		if (!bitmap.isMutable())
			bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(textColor);
		// text size in pixels is converted as follows:
		// 1. multiplied for scale to obtain size in dp
		// 2. multiplied for bitmap size to maintain proportionality
		// 3. divided for a constant (300) to assimilate input size with android text sizes
		textSize = (int) (textSize * scale * bitmap.getWidth() / 100);
		// If is too big it will be limited
		textSize = textSize < 15 ? textSize : 15;
		paint.setTextSize(textSize);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// Preparing text paint bounds
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		// Calculating position
		int x, y;
		// If no offset are set default is center of bitmap
		if (offsetX == null) {
			x = (bitmap.getWidth() - bounds.width()) / 2;
		} else {
			// If is a positive offset is set position is calculated
			// starting from left limit of bitmap
			if (offsetX >= 0) {
				x = offsetX;
				// Otherwise if negative offset is set position is calculated
				// starting from right limit of bitmap
			} else {
				x = bitmap.getWidth() - bounds.width() - offsetX;
			}
		}
		// If no offset are set default is center of bitmap
		if (offsetY == null) {
			y = (bitmap.getHeight() - bounds.height()) / 2;
		} else {
			// If is a positive offset is set position is calculated
			// starting from top limit of bitmap
			if (offsetY >= 0) {
				y = offsetY;
				// Otherwise if negative offset is set position is calculated
				// starting from bottom limit of bitmap
			} else {
				y = bitmap.getHeight() - bounds.height() + offsetY;
			}
		}

		// Drawing text
		canvas.drawText(text, x, y, paint);

		return bitmap;
	}
	
	
	public static Uri getUri(Context mContext, int resource_id) {
		Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resource_id);
		return uri;
	}
	
	
	
	/**
	 * Creates a thumbnail of requested size by doing a first sampled decoding of the bitmap to optimize memory
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
			dstBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
		}
		
		else if (type == TYPE_VIDEO) {
			srcBmp = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
			dstBmp = createVideoThumbnail(mContext, srcBmp, reqWidth, reqHeight);
		}
		
		return dstBmp;
	}




	/**
	 * Scales a bitmap to fit required ratio
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
	 */
	public static Bitmap createVideoThumbnail(Context mContext, Bitmap video, int width, int height) {
		video = ThumbnailUtils.extractThumbnail(video, width, height);
		Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(thumbnail);
		canvas.drawBitmap(video, 0, 0, null);
		
		// Movie mark
		int markSize = calculateVideoMarkSize(width, height);
		Bitmap mark = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.play_no_bg), markSize, markSize);
		int x = video.getWidth() / 2 - mark.getWidth() / 2;
		int y = video.getHeight() / 2 - mark.getHeight() / 2;
		canvas.drawBitmap(mark, x, y, null);

		return thumbnail;
	}


	private static int calculateVideoMarkSize(int width, int height) {
		int referredSize = Math.min(width, height);
		int result = referredSize / 9;
		if (result < 30) result = 30;
		if (result > 200) result = 200;
		return result;
	}


	private static int dpToPx(Context mContext, int dp) {
		float density = mContext.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}





}
