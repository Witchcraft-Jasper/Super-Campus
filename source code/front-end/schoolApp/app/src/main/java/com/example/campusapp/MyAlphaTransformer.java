package com.example.campusapp;

import android.view.View;

import com.fashare.stack_layout.StackLayout;

public class MyAlphaTransformer extends StackLayout.PageTransformer {
    private float mMinAlpha = 0f;
    private float mMaxAlpha = 1f;

    public MyAlphaTransformer(float minAlpha, float maxAlpha) {
        mMinAlpha = minAlpha;
        mMaxAlpha = maxAlpha;
    }

    public MyAlphaTransformer() {
        this(0f, 1f);
    }

    @Override
    public void transformPage(View view, float position, boolean isSwipeLeft) {

        View ivLike = view.findViewById(R.id.iv_like),
                ivDelete = view.findViewById(R.id.iv_delete),
                contentView = view.findViewById(R.id.layout_content);

        ivLike.setAlpha(mMinAlpha);
        ivDelete.setAlpha(mMinAlpha);
        contentView.setAlpha(mMaxAlpha);

        if (position > -1 && position <= 0) { // [-1,0]
            contentView.setVisibility(View.VISIBLE);

            // 渐变
            float diffAlpha = (mMaxAlpha-mMinAlpha) * Math.abs(position);
            contentView.setAlpha(mMaxAlpha - diffAlpha);

            // 向左滑: 显示"爱心"; 向右滑: 显示"叉叉"
            if(isSwipeLeft)
                ivLike.setAlpha(diffAlpha);
            else
                ivDelete.setAlpha(diffAlpha);
        } else{
            contentView.setAlpha(mMaxAlpha);
        }
    }

}
