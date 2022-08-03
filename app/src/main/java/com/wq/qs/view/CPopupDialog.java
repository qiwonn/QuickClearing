package com.wq.qs.view;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.Gravity;
import android.view.ViewGroup;
import android.content.Context;
import com.wq.qs.R;
//确定取消弹出对话框 qi.wong
public class CPopupDialog
{
	public static void showBottomDialog(Context con,final Runnable rcanplay) {
		//1、使用Dialog、设置style
		final Dialog dialog = new Dialog(con, R.style.DialogTheme);
		//2、设置布局
		View view = View.inflate(con, R.layout.ok_popup_window, null);
		dialog.setContentView(view);

		Window window = dialog.getWindow();
		//设置弹出位置
		window.setGravity(Gravity.BOTTOM);
		//设置弹出动画
		window.setWindowAnimations(R.style.main_menu_animStyle);
		//设置对话框大小
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		dialog.show();

		dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(null!=rcanplay)rcanplay.run();
					dialog.dismiss();
				}
			});

		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});

	}
}