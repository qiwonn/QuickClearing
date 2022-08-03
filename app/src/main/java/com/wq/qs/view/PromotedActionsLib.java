package com.wq.qs.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import com.wq.qs.R;
import android.util.TypedValue;
import android.widget.*;
import android.view.*;
import android.util.*;
import com.wq.qs.*;


public class PromotedActionsLib {

    private Context context;

    private FrameLayout frameLayout;

    private ImageButton mainImageButton;

    private RotateAnimation rotateOpenAnimation;

    private RotateAnimation rotateCloseAnimation;

    private ArrayList<ImageButtonItem> promotedActions;

    private ObjectAnimator objectAnimator[];

    private int px;

    private static final int ANIMATION_TIME = 120;
	private static final int ANIMATION_ROTATE_TIME = 880;

    private boolean isMenuOpened;

    public void setup(Context activityContext, FrameLayout layout) {
        context = activityContext;
        promotedActions = new ArrayList<ImageButtonItem>();
        frameLayout = layout;
        px = (int) context.getResources().getDimension(R.dimen.dim56dp) + 10;
        openRotation();
        closeRotation();
    }
	
    public ImageButton addMainItem(Drawable drawable,View.OnClickListener onClickListener) {
        mainImageButton = (ImageButton) LayoutInflater.from(context).inflate(R.layout.promoted_action_main_button, frameLayout, false);
        
		mainImageButton.setImageDrawable(drawable);
        
		mainImageButton.setOnClickListener(onClickListener);
		
        frameLayout.addView(mainImageButton);
		
        return mainImageButton;
    }
	
    public ImageButton addItem(int pos,Drawable drawable,View.OnClickListener onClickListener) {

        ImageButton button = (ImageButton) LayoutInflater.from(context).inflate(R.layout.promoted_action_button, frameLayout, false);

        button.setImageDrawable(drawable);

        button.setOnClickListener(onClickListener);

        promotedActions.add(new ImageButtonItem(button,pos));

        frameLayout.addView(button);

        return button;
    }

	public void setOnPromotedAction(boolean b){
		if(b){//打开
			if (!isMenuOpened) {
				isMenuOpened = true;
				openPromotedActions().start();
			} 
		}else{//关闭
			if(isMenuOpened){
				closePromotedActions().start();
				isMenuOpened = false;
			}
		}
		
	}
	
    /**
     * Set close animation for promoted actions
     */
    public AnimatorSet closePromotedActions() {

        if (objectAnimator == null){
            objectAnimatorSetup();
        }

        AnimatorSet animation = new AnimatorSet();

		
        for (int i = 0; i < promotedActions.size(); i++) {
			ImageButtonItem ibi = promotedActions.get(i);
            objectAnimator[i] = setCloseAnimation(ibi.ibtn, ibi.pos);
        }

        if (objectAnimator.length == 0) {
            objectAnimator = null;
        }

        animation.playTogether(objectAnimator);
        animation.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
					mainImageButton.startAnimation(rotateCloseAnimation);
					mainImageButton.setClickable(false);
				}

				@Override
				public void onAnimationEnd(Animator animator) {
					mainImageButton.setClickable(true);
					hidePromotedActions();
				}

				@Override
				public void onAnimationCancel(Animator animator) {
					mainImageButton.setClickable(true);
				}

				@Override
				public void onAnimationRepeat(Animator animator) {}
			});

        return animation;
    }

    public AnimatorSet openPromotedActions() {

        if (objectAnimator == null){
            objectAnimatorSetup();
        }



        AnimatorSet animation = new AnimatorSet();

        for (int i = 0; i < promotedActions.size(); i++) {
			ImageButtonItem ibi = promotedActions.get(i);
            objectAnimator[i] = setOpenAnimation(ibi.ibtn, ibi.pos);
        }

        if (objectAnimator.length == 0) {
            objectAnimator = null;
        }

        animation.playTogether(objectAnimator);
        animation.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
					mainImageButton.startAnimation(rotateOpenAnimation);
					mainImageButton.setClickable(false);
					showPromotedActions();
				}

				@Override
				public void onAnimationEnd(Animator animator) {
					mainImageButton.setClickable(true);
				}

				@Override
				public void onAnimationCancel(Animator animator) {
					mainImageButton.setClickable(true);
				}

				@Override
				public void onAnimationRepeat(Animator animator) {}
			});


        return animation;
    }

    private void objectAnimatorSetup() {

        objectAnimator = new ObjectAnimator[promotedActions.size()];
    }


    /**
     * Set close animation for single button
     *
     * @param promotedAction
     * @param position
     * @return objectAnimator
     */
    private ObjectAnimator setCloseAnimation(View promotedAction, int position) {

        ObjectAnimator objectAnimator;

        /*if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_Y, -px * (promotedActions.size() - position), 0f);
            objectAnimator.setRepeatCount(0);
            objectAnimator.setDuration(ANIMATION_TIME * (promotedActions.size() - position));

		}else {}
		}*/
		objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_X, px * position, 0f);
		objectAnimator.setRepeatCount(0);
		objectAnimator.setDuration(ANIMATION_TIME * Math.abs(position));
		
        return objectAnimator;
    }

    /**
     * Set open animation for single button
     *
     * @param promotedAction
     * @param position
     * @return objectAnimator
     */
    private ObjectAnimator setOpenAnimation(View promotedAction, int position) {

        ObjectAnimator objectAnimator;

        /*if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_Y, 0f, -px * (promotedActions.size() - position));
            objectAnimator.setRepeatCount(0);
            objectAnimator.setDuration(ANIMATION_TIME * (promotedActions.size() - position));

        } else {}
        }*/
		objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_X, 0f, px * position);
		objectAnimator.setRepeatCount(0);
		objectAnimator.setDuration(ANIMATION_TIME * Math.abs(position));

        return objectAnimator;
    }

    private void hidePromotedActions() {

        for (int i = 0; i < promotedActions.size(); i++) {
            promotedActions.get(i).ibtn.setVisibility(View.GONE);
        }
    }

    private void showPromotedActions() {

        for (int i = 0; i < promotedActions.size(); i++) {
            promotedActions.get(i).ibtn.setVisibility(View.VISIBLE);
        }
    }

    private void openRotation() {

        rotateOpenAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateOpenAnimation.setFillAfter(true);
        rotateOpenAnimation.setFillEnabled(true);
        rotateOpenAnimation.setDuration(ANIMATION_ROTATE_TIME);
    }

    private void closeRotation() {

        rotateCloseAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateCloseAnimation.setFillAfter(true);
        rotateCloseAnimation.setFillEnabled(true);
        rotateCloseAnimation.setDuration(ANIMATION_ROTATE_TIME);
    }
	
	private class ImageButtonItem{
		public View ibtn;
		public int pos;
		public ImageButtonItem(View btn,int p){
			ibtn = btn; pos = p;
		}
	}
}