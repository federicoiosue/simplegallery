package it.feio.android.simplegallery.async;

import it.feio.android.simplegallery.util.BitmapUtils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {

	Context mContext;
	ImageView mImageView;
	int width, height;

	public ImageLoadTask(Context mContext, ImageView mImageView, int width, int height) {
		this.mContext = mContext;
		this.mImageView = mImageView;
		this.width = width;
		this.height = height;
	}

	@Override
	protected Bitmap doInBackground(String... strings) {
		String path = strings[0];
		Bitmap bm = BitmapUtils.getFullImage(mContext, Uri.fromFile(new File(path)),
				width, height);
		return bm;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap == null) {
			mImageView.setScaleType(ScaleType.CENTER);
//			bitmap = BitmapFactory.decodeResource(getResources(),
//					R.drawable.no_photo);
//			mImageView.setImageBitmap(bitmap);
		} else {
			mImageView.setScaleType(ScaleType.MATRIX);
			mImageView.setImageBitmap(bitmap);
		}
		mImageView.setVisibility(View.VISIBLE);
//		mProgressBar.setVisibility(GONE);
	}

//	@Override
//	protected void onProgressUpdate(Integer... values) {
//		mProgressBar.setProgress(values[0]);
//	}
}