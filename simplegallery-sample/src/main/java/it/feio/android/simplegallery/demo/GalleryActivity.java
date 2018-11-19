package it.feio.android.simplegallery.demo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.feio.android.simplegallery.models.GalleryPagerAdapter;
import it.feio.android.simplegallery.views.GalleryViewPager;

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

	private final List<String> IMAGE_NAMES = Arrays.asList("02.png", "03.png", "04.png", "05.png");

	private FrameLayout galleryRootView;
	private GalleryViewPager mViewPager;
	private List<Uri> images;
	private Toolbar mTopToolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		initViews();
        initData();
	}


	private void initViews() {

		mTopToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mTopToolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("SimpleGallery Sample");
			getSupportActionBar().setSubtitle("(1/" + IMAGE_NAMES.size() + ")");
        }

		galleryRootView = findViewById(R.id.gallery_root);

		mViewPager = findViewById(R.id.fullscreen_content);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
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
		int clickedImage = 0;

		GalleryPagerAdapter pagerAdapter =
				new GalleryPagerAdapter(this, getImages());
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(clickedImage);
	}


	@NonNull
	private List<Uri> getImages() {
		images = new ArrayList<>();
		for (String imageName : IMAGE_NAMES) {
			images.add(Uri.parse("file:///android_asset/" + imageName));
		}
		return images;
	}


	private void viewMedia() {
		Toast.makeText(this, "Share with external app", Toast.LENGTH_SHORT).show();
	}

}
