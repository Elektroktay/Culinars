package com.culinars.culinars;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Oktayşen on 21/6/2016.
 */
public class CustomSpeedLinearLayoutManager extends LinearLayoutManager {
    int speed;

    private CustomSpeedLinearLayoutManager(Context context) {
        super(context);
        speed = 10;
    }

    public CustomSpeedLinearLayoutManager(Context context, int dpPerSecond) {
        super(context);
        this.speed = dpPerSecond;
    }
    public CustomSpeedLinearLayoutManager(Context context, int direction, int dpPerSecond) {
        super(context, direction, false);
        this.speed = dpPerSecond;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return CustomSpeedLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 1000 / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, speed, displayMetrics);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

}
