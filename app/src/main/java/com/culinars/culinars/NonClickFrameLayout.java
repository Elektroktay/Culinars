package com.culinars.culinars;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Oktayşen on 25/6/2016.
 */
public class NonClickFrameLayout extends FrameLayout {
    public NonClickFrameLayout(Context context) {
        super(context);
    }

    public NonClickFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonClickFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NonClickFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
