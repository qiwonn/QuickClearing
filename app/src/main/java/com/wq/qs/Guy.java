package com.wq.qs;
import java.util.*;
import java.text.*;
import java.io.Serializable;

public class Guy implements Serializable{
    private String name;
	private String choose = "○";
	private long modificationDate;
    private long size;
	private int type;
	private int kids;
	private int id = -1;//保存在列表中最初的位置(唯一的)
	private boolean selected = false;
	
    public Guy(int id,String guyName,long guyDate,int guyType,long guySize,int children) {
        this.id = id;
		name = guyName;
		modificationDate = guyDate;
        type = guyType;
		size = guySize;
		kids = children;
    }
	
	//获取在列表中最初的位置(唯一的)
	public int getId(){
		return id;
	}
	
	public int getType() {
        return type;
    }
	
	public long getSizeLong(){
		return size;
	}
	
	public String getPath(){
		return name;
	}
	
    public String getName() {
        return name.substring(name.lastIndexOf("/")+1,name.length());
    }
	
	public String getChoose(){
		return choose;
	}
	
	public String getDate(){
		Date d = new Date(modificationDate);
		return formatDate(d);
	}
	
	private String formatDate(Date date){
		return date.toLocaleString().split(" ")[0];
	}
	
	private String formatDateddMMnn(Date date){
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
        SimpleDateFormat sdf2= new SimpleDateFormat("dd");
        String n = sdf0.format(date); //得到 年，例如：2020
        String m = sdf1.format(date); //得到 月，例如：7
        String d = sdf2.format(date); //得到 日，例如：31
		return d+"/"+m+"/"+n;
	}
	
	public String getSize(){
		return FileHelper.getInstance().FormatFileSize(size);
	}
	
	public String getKids(){
		return type==R.drawable.file?"":kids+" 项";
	}
	
	public int getKidsInt(){
		return kids;
	}
	
    public void setSize(long l) {
        size = l;
    }
	
	public void addSize(long a){
		size += a;
	}
	
	public void setChoose(String s){
		choose = s;
	}
	
	public void setType(int t){
		type = t;
	}
	
	public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}