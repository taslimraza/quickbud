package com.app.android.quickbud.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mobi11 on 15/10/15.
 */
public class RecyclerViewClickListener implements
        RecyclerView.OnItemTouchListener {

    private OnClickListener mClickListener;
    private GestureDetector mGestureDetector;

    public interface OnClickListener {
        public void onClick(View view, int position);
    }

    public RecyclerViewClickListener(Context context, OnClickListener clickListener) {
        this.mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (childView != null && mClickListener != null && mGestureDetector.onTouchEvent(motionEvent)) {
            mClickListener.onClick(childView, recyclerView.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }
}
