package it.feio.android.simplegallery.models;
import it.feio.android.simplegallery.GalleryPagerFragment;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
 * objects, in sequence.
 */
public class GalleryPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<String> mResources;
	private Activity mActivity;

	public GalleryPagerAdapter(FragmentActivity activity, List<String> resources) {
		super(activity.getSupportFragmentManager());
        this.mResources = resources;
        this.mActivity = activity;
	}

	@Override
	public Fragment getItem(int position) {
		try {
			return GalleryPagerFragment.create(position, mResources.get(position));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return mResources.size();
	}
}