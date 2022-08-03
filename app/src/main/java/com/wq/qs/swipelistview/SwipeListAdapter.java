package com.wq.qs.swipelistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.CheckBox;
import android.graphics.Color;
import java.util.List;
import com.wq.qs.Guy;
import com.wq.qs.R;
import com.wq.qs.MagTest;
import java.util.ArrayList;

/**
 * 可水平滑动item的列表适配器 qi.wong
 */
public class SwipeListAdapter extends BaseSwipListAdapter {
	private List<Guy> list = null;
	private LayoutInflater inflater;
	private boolean isSelecting = false;
	private float totalsize = 0;
	public SwipeListAdapter(Context context, List<Guy> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
	
	public void updateChangedItem(int position,Guy guy,ListView viewGroup){
		list.set(position, guy);
		if(null==viewGroup)
			return;
		int visibleFirstPosition = viewGroup.getFirstVisiblePosition();
		int visibleLastPosition = viewGroup.getLastVisiblePosition();
        //如果此项可见，那么就手动刷新
        if (visibleFirstPosition <= position 
			&& position <= visibleLastPosition) {
            View view = viewGroup.getChildAt(position-visibleFirstPosition);
            if (view == null) {
				MagTest.Instance.sendDebuggerMsg("view == null!!");
                return;
            }
			getView(position,view,viewGroup);
        }
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	public Guy getItemById(int id, Integer out){
		Guy b = null;
		int i = list.size();
		for(;--i>=0;){
			b = list.get(i);
			if(id == b.getId()){out = Integer.valueOf(i);return b;}
		}
		return null;
	}
	
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	public int getItemPosition(Guy b){
		int i = list.size();
		for(;--i>=0;){
			if(b.getId() == list.get(i).getId())
				return i;
		}
		return -1;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_a, null);
			holder = new ViewHolder(convertView);
		}else{
			holder = (ViewHolder) convertView.getTag();
			if(null==holder)holder = new ViewHolder(convertView);
		}
		Guy o = list.get(position);
		setViewHolder(holder,o,position);
		
		return convertView;
	}

	private void setViewHolder(ViewHolder holder,Guy o,int position){
		holder.tv_Name.setText(o.getName());
       	holder.iv_Type.setBackgroundResource(o.getType());
		holder.tv_Detail.setText(o.getKids()+" "+o.getSize()+" "+o.getDate());
		float osz = o.getSizeLong();
		int color = 0xffa6a6a6;
		if(totalsize!=0){
			int red = (int)((osz/totalsize)*255);
			color = Color.rgb(red,0,0);
		}
		holder.tv_Detail.setTextColor(color);
		holder.tv_Choose.setText(position+o.getChoose());
		holder.tv_Choose.setTextColor(o.isSelected()?0xffff0000:0xffbbbbbb);
		holder.checkBox.setVisibility(isSelecting ? View.VISIBLE : View.GONE);
		holder.checkBox.setChecked(o.isSelected());
	}
	
	private class ViewHolder {
		public TextView tv_Name;
		public TextView tv_Detail;
		public TextView tv_Choose;
        public ImageView iv_Type;
		public CheckBox checkBox;
		public ViewHolder(View view) {
			tv_Name = (TextView) view.findViewById(R.id.tv_name);
			tv_Detail = (TextView) view.findViewById(R.id.tv_detail);
			tv_Choose = (TextView) view.findViewById(R.id.tv_choose);
            iv_Type = (ImageView) view.findViewById(R.id.iv_type);
			checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
			view.setTag(this);
		}
	}

	@Override
	public boolean getSwipEnableByPosition(int position) {
		//if(position % 2 == 0){
		//	return false;
		//}
		return true;
	}
	
	public void setSelecting(boolean show){
		isSelecting = show;
	}
	public boolean isSelecting(){return isSelecting;}
	
	public void setTotalSize(long ts){
		totalsize = ts;
	}
}