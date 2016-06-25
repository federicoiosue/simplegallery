package it.feio.android.simplegallery.models;
import android.net.Uri;
import it.feio.android.simplegallery.GalleryPagerFragment;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

public class GalleryPagerAdapter extends FragmentStatePagerAdapter {

	private List<Uri> resources;


	public GalleryPagerAdapter(FragmentActivity activity, List<Uri> resources) {
		super(activity.getSupportFragmentManager());
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