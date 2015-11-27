package com.example.fede.simplegallery;

import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import it.feio.android.simplegallery.models.GalleryPagerAdapter;
import it.feio.android.simplegallery.models.listeners.OnViewTouchedListener;
import it.feio.android.simplegallery.views.GalleryViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2015 Federico Iosue (federico.iosue@gmail.com)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system bar)
 * * with user interaction.
 */
public class GalleryActivity extends AppCompatActivity {

	/**
	 * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction before hiding the
	 * * system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility
	 * * upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */


	FrameLayout galleryRootView;
	GalleryViewPager mViewPager;

	List<Integer> images;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		initViews();
		initData();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_gallery, menu);
		return true;
	}


	private void initViews() {
		// Show the Up button in the action bar.
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		galleryRootView = (FrameLayout) findViewById(R.id.gallery_root);
//		galleryRootView.setOnViewTouchedListener(screenTouches);

		mViewPager = (GalleryViewPager) findViewById(R.id.fullscreen_content);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				getSupportActionBar().setSubtitle("(" + (arg0 + 1) + "/" + images.size() + ")");
			}


			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}


			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}


	/**
	 * Initializes data received from note detail screen
	 */
	private void initData() {
		String title = getIntent().getStringExtra("Gallery title");
		int clickedImage = 0;

		List<String> imagesPaths = Arrays.asList(Uri.parse("file:///android_asset/raw/sample/logout.png").getPath());

		GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(this, imagesPaths);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(clickedImage);

//		getSupportActionBar().setTitle(title);
//		getSupportActionBar().setSubtitle("(" + (clickedImage + 1) + "/" + images.size() + ")");

//		// If selected attachment is a video it will be immediately played
//		if (images.get(clickedImage).getMime_type().equals(Constants.MIME_TYPE_VIDEO)) {
//			viewMedia();
//		}
	}


//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case android.R.id.home:
//				onBackPressed();
//				break;
//			case R.id.menu_gallery_share: {
//				shareMedia();
//				break;
//			}
//			case R.id.menu_gallery: {
//				viewMedia();
//				break;
//			}
//		}
//		return super.onOptionsItemSelected(item);
//	}


	private void viewMedia() {
		Toast.makeText(this, "Share with external app", Toast.LENGTH_SHORT).show();
	}


//	private void shareMedia() {
//		Attachment attachment = images.get(mViewPager.getCurrentItem());
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType(StorageHelper.getMimeType(this, attachment.getUri()));
//		intent.putExtra(Intent.EXTRA_STREAM, attachment.getUri());
//		startActivity(intent);
//	}


	OnViewTouchedListener screenTouches = new OnViewTouchedListener() {
		private final int MOVING_THRESHOLD = 30;
		float x;
		float y;
		private boolean status_pressed = false;


		@Override
		public void onViewTouchOccurred(MotionEvent ev) {
			if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
				x = ev.getX();
				y = ev.getY();
				status_pressed = true;
			}
			if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
				float dx = Math.abs(x - ev.getX());
				float dy = Math.abs(y - ev.getY());
				double dxy = Math.sqrt(dx * dx + dy * dy);
				if (dxy >= MOVING_THRESHOLD) {
					status_pressed = false;
				}
			}
			if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				if (status_pressed) {
//					click();
					status_pressed = false;
				}
			}
		}
	};


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Gallery Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.example.fede.simplegallery/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}


	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Gallery Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.example.fede.simplegallery/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
