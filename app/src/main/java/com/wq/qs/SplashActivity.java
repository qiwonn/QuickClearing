package com.wq.qs;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.plattysoft.leonids.*;
import com.plattysoft.leonids.modifiers.*;
import android.widget.*;
import android.view.animation.*;
import android.net.Uri;
/**
 * 启动引导界面 qi.wong
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = intent.getData();
			String parentpath = Uri.decode(uri.getEncodedPath());
			int index = parentpath.lastIndexOf("/");
			if(index>=0 && index < parentpath.length())
				parentpath = parentpath.substring(0,index);
			intent = new Intent(this,MainActivity.class);
			intent.setAction(Intent.ACTION_ANSWER);
			intent.putExtra("path",parentpath);
			startActivity(intent);
			finish();
		}
        else{
			startActivity(new Intent(this,MainActivity.class));
			finish();
		}
    }
}