package it.feio.android.simplegallery.models;
import it.feio.android.simplegallery.GalleryPagerFragment;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

public class GalleryPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<String> resources;
//	private Activity mActivity;


	public GalleryPagerAdapter(FragmentActivity activity, List<String> resources) {
		super(activity.getSupportFragmentManager());
//		this.mActivity = activity;
		this.resources = resources;
	}

	@Override
	public Fragment getItem(int position) {
		try {
			return GalleryPagerFragment.create(position, resources.get(position));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return resources.size();
	}
}