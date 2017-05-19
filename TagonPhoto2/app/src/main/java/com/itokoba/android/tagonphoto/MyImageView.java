package com.itokoba.android.tagonphoto;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hideki on 2017/05/15.
 */

public class MyImageView extends ImageView {
    public MyImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private OnVisibilityChangeListener mOnVisibilityChangeListener;
    public interface OnVisibilityChangeListener {
        public void onVisibilityChange(int visiblity);
    }
    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        mOnVisibilityChangeListener = listener;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mOnVisibilityChangeListener != null) {
            mOnVisibilityChangeListener.onVisibilityChange(visibility);
        }
    }
}