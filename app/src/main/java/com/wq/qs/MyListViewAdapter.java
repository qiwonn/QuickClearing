package com.wq.qs;
import android.widget.BaseAdapter;
import java.util.List;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.*;

public class MyListViewAdapter extends BaseAdapter {
    private List<Guy> list;
    LayoutInflater inflater;
    public MyListViewAdapter(Context context, List<Guy> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

	public void updateChangedItem(int position,Guy newGuyBean,ListView viewGroup){
		list.set(position, newGuyBean);
		if(null==viewGroup)
			return;
		int visibleFirstPosition = viewGroup.getFirstVisiblePosition();
		int visibleLastPosition = viewGroup.getLastVisiblePosition();
        //如果此项可见，那么就手动刷新
        if (visibleFirstPosition <= position 
		&& position <= visibleLastPosition) {
            View view = viewGroup.getChildAt(position-visibleFirstPosition);
            if (view == null) {
                return;
            }
            //获取到该item的组件进行刷新
			//((ViewHolder) view.getTag()).guyName.setText(list.get(position).getGuyName());
			getView(position,view,viewGroup);
        }
	}
	
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int i) {
		//MagTest.Instance.sendDebuggerMsg(""+i);
        return i;
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = inflater.inflate(R.layout.item_a, null);
            viewHolder = new ViewHolder();
            viewHolder.vhName = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.vhDetail = (TextView) convertView.findViewById(R.id.tv_detail);
			viewHolder.vhChoose = (TextView) convertView.findViewById(R.id.tv_choose);
            viewHolder.vhType = (ImageView) convertView.findViewById(R.id.iv_type);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
		Guy o = list.get(i);
        viewHolder.vhName.setText(o.getName());
        viewHolder.vhType.setBackgroundResource(o.getType());
		viewHolder.vhDetail.setText(o.getKids()+" "+o.getSize()+" "+o.getDate());
		viewHolder.vhChoose.setText(o.getChoose());
        // 将这个处理好的view返回
        return convertView;
    }
    public class ViewHolder {
        public TextView vhName;
		public TextView vhDetail;
		public TextView vhChoose;
        public ImageView vhType;
    }
}