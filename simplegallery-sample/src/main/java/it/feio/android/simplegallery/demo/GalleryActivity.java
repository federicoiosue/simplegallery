package it.feio.android.simplegallery.demo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;
import it.feio.android.demo.models.GalleryPagerAdapter;
import it.feio.android.demo.models.listeners.OnViewTouchedListener;
import it.feio.android.demo.views.GalleryViewPager;

import java.io.IOException;
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

	FrameLayout galleryRootView;
	GalleryViewPager mViewPager;
	List<Uri> images;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		initViews();
		try {
			initData();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
//				getSupportActionBar().setSubtitle("(" + (arg0 + 1) + "/" + images.size() + ")");
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
	private void initData() throws IOException {
		String title = getIntent().getStringExtra("Gallery title");
		int clickedImage = 0;

		GalleryPagerAdapter pagerAdapter =
				new GalleryPagerAdapter(this, getImages());
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(clickedImage);
	}


	@NonNull
	private List<Uri> getImages() throws IOException {
		images = new ArrayList<>();
		for (String imageName : Arrays.asList("02.png", "03.png", "04.png", "05.png")) {
			images.add(Uri.parse("file:///android_asset/" + imageName));
		}
		return images;
	}


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

}
