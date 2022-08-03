package com.wq.qs.view;
import android.content.Context;
import android.view.View.OnClickListener;
import android.view.View;

public class COKDialog extends MaterialDialog
{
	private int s = 0;
	public COKDialog(Context con,String title,String msgstr){
		super(con,title,msgstr);
		setPositiveButton("ok", new View.OnClickListener(){
				@Override
				public void onClick(View p1){
					dismiss();
				}
		});
	}
}