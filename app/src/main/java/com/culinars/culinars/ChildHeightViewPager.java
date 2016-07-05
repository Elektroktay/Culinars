package com.culinars.culinars;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Oktayşen on 26/6/2016.
 */
public class ChildHeightViewPager extends ViewPager{
    public ChildHeightViewPager(Context context) {
        super(context);
    }

    public ChildHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //Courtesy of: http://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
