package ru.relastic.cloudreception.presenter;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.relastic.cloudreception.R;

public class ZViewHolder extends RecyclerView.ViewHolder {

    ZViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setData(int position, Object item, IPresenterUICallback callback){  }


    public static ZViewHolder createInstance(ViewGroup container) {
        return new ZViewHolder(new View(container.getContext()));
    }

    public static String NoNull(String text) {
        return (text!=null) ? text : "" ;
    }
    public static void setBold(TextView[] views, boolean bold) {
        Typeface par = bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT;
        for (TextView v : views) {
            v.setTypeface(par);
        }
    }
    public static void animateSelectedItem(final View layout) {
        final int defColor = layout.getResources().getColor(R.color.background);
        final int tgtColor = layout.getResources().getColor(R.color.primary_light);
        if (!layout.getBackground().getClass().equals(GradientDrawable.class)) {
            return;
        }
        final GradientDrawable drawable = (GradientDrawable)layout.getBackground();
        ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(drawable,
                "color",
                new ArgbEvaluator(),
                defColor,
                tgtColor);
        backgroundColorAnimator.setDuration(50);
        backgroundColorAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator backgroundColorAnimatorEnd = ObjectAnimator.ofObject(drawable,
                        "color",
                        new ArgbEvaluator(),
                        tgtColor,
                        defColor);
                backgroundColorAnimatorEnd.setDuration(500);
                backgroundColorAnimatorEnd.start();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                layout.setBackgroundColor(defColor);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        backgroundColorAnimator.start();
    }
}
