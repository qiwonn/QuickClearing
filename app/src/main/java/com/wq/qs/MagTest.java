package com.wq.qs;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;
/*
 * 发送句柄消息
 */
public class MagTest
{
	public static final MagTest Instance = new MagTest();
	private Context con;
	public void init(Context con){
		this.con = con;
	}
	
	public void sendDebuggerMsg(String s){
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("debugger",s);
		b.putInt("length",0);
		b.putInt("msgid",1024);
		message.setData(b);
		((MainActivity)con).getHandler().sendMessage(message);
	}
	public void sendDebuggerMsgLong(String s){
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("debugger",s);
		b.putInt("length",1);
		b.putInt("msgid",1024);
		message.setData(b);
		((MainActivity)con).getHandler().sendMessage(message);
	}
	
	public void sendMsg(int id,final int position,long size){
		Message message = new Message();
		Bundle b = new Bundle();
		b.putInt("msgid",id);
		b.putInt("position",position);
		b.putLong("+size",size);
		message.setData(b);
		((MainActivity)con).getHandler().sendMessage(message);
	}
}