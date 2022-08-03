package com.wq.qs;
import com.plattysoft.leonids.*;
import android.content.*;
import android.app.*;
import com.plattysoft.leonids.modifiers.*;
import android.animation.AnimatorSet;
import java.util.ArrayList;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.View;
import android.animation.Animator;
import android.animation.ObjectAnimator;

public class MyParticles
{
	public static void build(Activity con){
		new ParticleSystem(con, 50, R.drawable.file, 3000)		
			.setSpeedByComponentsRange(-0.1f, 0.1f, -0.1f, 0.02f)
			.setAcceleration(0.000003f, 90)
			.setInitialRotationRange(0, 360)
			.setRotationSpeed(120)
			.setFadeOut(2000)
			.addModifier(new ScaleModifier(0f, 1.5f, 0, 1500))
			.oneShot(null, 10);
	}
	
	private static void foundGuy(View foundDevice){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }
}