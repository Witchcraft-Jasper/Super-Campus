package com.example.campusapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class ImageTextView extends AppCompatTextView {



    public ImageTextView(Context context) {
        super(context);
        init(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init(Context context){
        if(!isInEditMode()) {
            Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
            this.setTypeface(iconfont);
        }
    }
}
