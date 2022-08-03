package com.wq.qs.view;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Window;
import android.view.View;
import android.widget.*;

public class LoadingRelaxDialog
{
	private AlertDialog adlg = null;
	private LoadingRelaxView view=null;
	
	public LoadingRelaxDialog(Context con){
		adlg = new android.app.AlertDialog.Builder(con).create();
        show();
        Window window = adlg.getWindow();
		view = new LoadingRelaxView(con);
		view.setBackgroundColor(0x00000000);
		view.setViewColor(0xff00ff00);
        view.setBallColor(0xffff0000);
		view.setLayoutParams(new LinearLayout.LayoutParams(720,64));
        window.setContentView(view);
		view.startAnim();
	}
	
	public void show() { adlg.show();}
	
	public void dismiss() { view.stopAnim();adlg.dismiss();}
}