/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package it.feio.android.simplegallery.views;

import it.feio.android.simplegallery.models.listeners.OnViewTouchedListener;
import it.feio.android.simplegallery.models.ui.CoverFlowPageTransformer;
import it.feio.android.simplegallery.models.ui.DepthPageTransformer;
import it.feio.android.simplegallery.models.ui.RotationPageTransformer;
import it.feio.android.simplegallery.models.ui.ZoomOutPageTransformer;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This class implements method to help <b>TouchImageView</b> fling, draggin and
 * scaling.
 */
public class GalleryViewPager extends ViewPager {

	public static final int PAGE_TRANSFORMER_DEPTH = 0;
	public static final int PAGE_TRANSFORMER_ZOOM_OUT = 1;
	public static final int PAGE_TRANSFORMER_COVER_FLOW = 2;
	public static final int PAGE_TRANSFORMER_ROTATION = 3;

	private static final int PAGE_TRANSFORMER_ROTATION_ANGLE = 160;

	PointF last;
	public TouchImageView mCurrentView;
	private OnViewTouchedListener mOnViewTouchedListener;

	public GalleryViewPager(Context context) {
		super(context);
	}

	public GalleryViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// private float[] handleMotionEvent(MotionEvent event)
	// {
	// switch (event.getAction() & MotionEvent.ACTION_MASK) {
	// case MotionEvent.ACTION_DOWN:
	// last = new PointF(event.getX(0), event.getY(0));
	// break;
	// case MotionEvent.ACTION_MOVE:
	// case MotionEvent.ACTION_UP:
	// PointF curr = new PointF(event.getX(0), event.getY(0));
	// return new float[]{curr.x - last.x, curr.y - last.y};
	//
	// }
	// return null;
	// }
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if ((event.getAction() & MotionEvent.ACTION_MASK) ==
	// MotionEvent.ACTION_UP)
	// {
	// super.onTouchEvent(event);
	// }
	//
	// float [] difference = handleMotionEvent(event);
	//
	// if (mCurrentView.pagerCanScroll()) {
	// return super.onTouchEvent(event);
	// }
	// else {
	// if (difference != null && mCurrentView.onRightSide && difference[0] < 0)
	// //move right
	// {
	// return super.onTouchEvent(event);
	// }
	// if (difference != null && mCurrentView.onLeftSide && difference[0] > 0)
	// //move left
	// {
	// return super.onTouchEvent(event);
	// }
	// if (difference == null && ( mCurrentView.onLeftSide ||
	// mCurrentView.onRightSide))
	// {
	// return super.onTouchEvent(event);
	// }
	// }
	//
	// return false;
	// }
	//
	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent event) {
	// if ((event.getAction() & MotionEvent.ACTION_MASK) ==
	// MotionEvent.ACTION_UP)
	// {
	// super.onInterceptTouchEvent(event);
	// }
	//
	// float [] difference = handleMotionEvent(event);
	//
	// if (mCurrentView.pagerCanScroll()) {
	// return super.onInterceptTouchEvent(event);
	// }
	// else {
	// if (difference != null && mCurrentView.onRightSide && difference[0] < 0)
	// //move right
	// {
	// return super.onInterceptTouchEvent(event);
	// }
	// if (difference != null && mCurrentView.onLeftSide && difference[0] > 0)
	// //move left
	// {
	// return super.onInterceptTouchEvent(event);
	// }
	// if (difference == null && ( mCurrentView.onLeftSide ||
	// mCurrentView.onRightSide))
	// {
	// return super.onInterceptTouchEvent(event);
	// }
	// }
	// return false;
	// }
	
	
	
	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	    if (v instanceof TouchImageView) {
	    	//
	    	// canScrollHorizontally is not supported for Api < 14. To get around this issue,
	    	// ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
	    	// canScrollHorizontally supporting Api >= 8, is called.
	    	//
	        return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
	        
	    } else {
	        return super.canScroll(v, checkV, dx, x, y);
	    }
	}
	
	
	

	public void setPageTransformer(int presetPageTransformer) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// setPageMargin(-2 * paddingOnPages);
			setClipChildren(false);
//			setOffscreenPageLimit(getAdapter().getCount());
			setOffscreenPageLimit(1);
			switch (presetPageTransformer) {
			case PAGE_TRANSFORMER_DEPTH:
				super.setPageTransformer(true, new DepthPageTransformer());
				break;
			case PAGE_TRANSFORMER_ZOOM_OUT:
				super.setPageTransformer(false, new ZoomOutPageTransformer());
				break;
			case PAGE_TRANSFORMER_COVER_FLOW:
				super.setPageTransformer(false, new CoverFlowPageTransformer());
				break;
			case PAGE_TRANSFORMER_ROTATION:
				super.setPageTransformer(true, new RotationPageTransformer(
						PAGE_TRANSFORMER_ROTATION_ANGLE));
				break;
			default:
				return;
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnViewTouchedListener != null) {
			mOnViewTouchedListener.onViewTouchOccurred(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * Gets and throws to parent touch events on the ViewPager
	 * 
	 * @param mOnViewTouchedListener
	 */
	public void setOnViewTouchedListener(
			OnViewTouchedListener mOnViewTouchedListener) {
		this.mOnViewTouchedListener = mOnViewTouchedListener;
	}
}
