package com.wq.qs;

import com.plattysoft.leonids.ParticleSystem;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.content.Intent;
import android.os.*;
import android.widget.*;
import com.plattysoft.leonids.modifiers.*;
import com.wq.qs.*;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		/*EventDispatcher.Add(Events.UIEvent.PlayEffect, new Action(){
				@Override
				public void invoke()
				{
					createNewParticlesSystem(null);
					Toast.makeText(WelcomeActivity.this,"welcome event",Toast.LENGTH_LONG).show();
				}
		});*/
		ImageView iv_circle = (ImageView)findViewById(R.id.iv_circle);
		createFirstParticleSystem(iv_circle, new Runnable(){
				@Override
				public void run()
				{
					Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
					intent.setAction(Intent.ACTION_ANSWER);
					startActivity(intent);
					//finish();
					createNewParticlesSystem(null);
				}
		});
		/**/
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		//EventDispatcher.Remove(Events.UIEvent.PlayEffect);
		super.onDestroy();
	}
	
	private void createFirstParticleSystem(View arg0,Runnable r) {
		// Launch 2 particle systems one for each image
		new ParticleSystem(this, 10, R.drawable.file, 2000)		
			.setSpeedByComponentsRange(-0.1f, 0.1f, -0.1f, 0.02f)
			.setAcceleration(0.000003f, 90)
			.setInitialRotationRange(0, 360)
			.setRotationSpeed(120)
			.setFadeOut(1000)
			.addModifier(new ScaleModifier(0f, 1.5f, 0, 1500))
			.setEndOnceCallback(r)
			.oneShot(arg0, 10);
	}
	
	private void createNewParticlesSystem(View arg0){
		createFirstParticleSystem(null,new Runnable(){
				@Override
				public void run()
				{
					createNewParticlesSystem(null);
				}
			});
	}
}