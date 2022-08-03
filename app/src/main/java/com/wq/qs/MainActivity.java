package com.wq.qs;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import java.util.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.io.*;
import android.os.*;
import android.content.*;
import android.text.*;
import android.net.*;
import android.view.animation.AnimationUtils;
import org.apache.http.impl.client.*;
import android.graphics.Bitmap;
import com.wq.qs.swipelistview.SwipeMenuListView;
import com.wq.qs.swipelistview.SwipeListAdapter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.util.TypedValue;
import com.wq.qs.swipelistview.*;
import com.wq.qs.Action1;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.support.annotation.*;
import com.wq.qs.view.*;

public class MainActivity extends Activity 
{
	private long m_totalsize = 0;
	private int m_clearCount = 0;
	//是不是正式版 false:测试版  true:正式版
	private boolean m_isofficialversion = false;
	private boolean m_isopenning = false;
	private boolean m_isAsycClear = false;
	private boolean m_isChecked = false;
	private ProgressBar ssprogressBar = null;
	private RippleBackground rippleBackground = null;
	private Button  btnReturn = null;
	private ImageButton btnCancel=null;
	private ImageButton selectAll=null;
	private PromotedActionsLib promotedActionsLib = null;
	private SwipeListAdapter mylva = null;
	private SwipeMenuListView listView=null;
	private List<Guy> itemlist = null;
	private List<Guy> clearlist = null;
	//private StringBuilder tsb = new StringBuilder();
	private String parentpath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		MagTest.Instance.init(this);
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = intent.getData();
			parentpath = Uri.decode(uri.getEncodedPath());
			//Toast.makeText(this,"ACTION_VIEW == "+parentpath,Toast.LENGTH_LONG).show();
			int index = parentpath.lastIndexOf("/");
			if(index>=0 && index < parentpath.length())
				parentpath = parentpath.substring(0,index);
		}else if(Intent.ACTION_ANSWER.equals(action)){
			parentpath = intent.getStringExtra("path");
		}
		
		if(null==parentpath || "".equals(parentpath)){
			parentpath = Environment.getExternalStorageDirectory().getAbsolutePath();
			//+"/Android";
		}
		
		setTitleText(parentpath);
		clearlist = new ArrayList<Guy>();
		
		//返回
        btnReturn = (Button) findViewById(R.id.returnBtn);
		btnReturn.setVisibility(View.GONE);
		btnReturn.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_menu_revert));
		btnReturn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1){
					//正在打开..,则不继续执行
					if(m_isopenning){
						Toast.makeText(MainActivity.this,"正在打开..",Toast.LENGTH_SHORT).show();
						return;
					}
					btnReturn.setClickable(false);
					m_isopenning = true;
					String root = Environment.getExternalStorageDirectory().getAbsolutePath();
					//当前已是根目录,则不继续执行
					if(parentpath.equals(root)){
						m_isopenning = false;
						(new COKDialog(MainActivity.this,"温馨提示","当前已是根目录,不能再返回。")).show();
						btnReturn.setClickable(true);
						return;
					}
					//(new LoadingRelaxDialog(MainActivity.this)).show();
					cancel();//取消全选
					release();//释放列表资源
					parentpath = parentpath.substring(0,parentpath.lastIndexOf("/"));
					//上一目录是根目录
					if(parentpath.equals(root) || 10 < (new File(parentpath)).list().length){
						initFileItemData(parentpath,true);
					}else{
						initFileItemData(parentpath,false);
						btnReturn.setClickable(true);
					}
					setTitleText(parentpath);
					m_isopenning = false;
				}
			});
			
		ssprogressBar = (ProgressBar)findViewById(R.id.clear_progrossbar);
		
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);

		rippleBackground = (RippleBackground)findViewById(R.id.ripple_content);
		
        promotedActionsLib = new PromotedActionsLib();
		
        promotedActionsLib.setup(getApplicationContext(), frameLayout);
		//全选
        selectAll = promotedActionsLib.addItem(-2, getResources().getDrawable(R.drawable.box), new View.OnClickListener(){
				@Override
				public void onClick(View p1){
					m_isChecked = !m_isChecked;
					setChecked(m_isChecked);
					//
					if(m_isChecked){//全选
						selectedAllItemsClearOrNot(true);
						mylva.notifyDataSetChanged();
					}else{
						selectedAllItemsClearOrNot(false);
						mylva.notifyDataSetChanged();
						clearlist.clear();
					}
				}
		});
		//取消
        btnCancel = promotedActionsLib.addItem(2, getResources().getDrawable(R.drawable.cancelbut), new View.OnClickListener(){
				@Override
				public void onClick(View p1){
					//
					cancel();
				}
		});
		//奇速清理
        promotedActionsLib.addMainItem(getResources().getDrawable(R.drawable.qisuqingli3), new View.OnClickListener(){
				@Override
				public void onClick(View p1){
					//正处于异步执行中...
					if(m_isAsycClear){
						Toast.makeText(MainActivity.this,"清理中...",Toast.LENGTH_LONG).show();
						return;
					}
					m_isAsycClear = true;
					rippleBackground.startRippleAnimation();
					
					if(m_isofficialversion)
						qisuOfficialVersionClear();
					else
						qisuBetaVersionClear();

					m_clearCount++;
					m_isAsycClear = false;
					rippleBackground.stopRippleAnimation();
				}
		});
		
		rippleBackground.startRippleAnimation();
		
		if(null!=itemlist && 0 < itemlist.size()){
			setListAdapter(itemlist);
		}else{
			open(true);
		}
		
    }

	private void setTitleText(String strpath){
		//是根目录
		if(strpath.equals(Environment.getExternalStorageDirectory().getAbsolutePath()))
			setTitle("文件管理/../0/");
		else
			setTitle("文件管理/.."+strpath.substring(strpath.lastIndexOf("/"),strpath.length()));
		
	}
	
	private void open(final boolean isAsyc){
		//初始化列表数据 此处使用线程会闪退(*°O°*)
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
		threadPool.execute(new Runnable(){
				@Override
				public void run(){
					initFileItemData(parentpath,isAsyc);
				}
			});
		threadPool.shutdown();
	}
	
	private void setChecked(boolean b){
		if(b){
			selectAll.setImageDrawable(getResources().getDrawable(R.drawable.boxchecked));
		}else{
			selectAll.setImageDrawable(getResources().getDrawable(R.drawable.box));
		}
	}
	
	private void initFileItemData(String path,boolean isAsyc){
		if(null==itemlist)itemlist = new ArrayList<Guy>();
		File[] files = (new File(path)).listFiles();
		//不需要异步执行
		if(!isAsyc){
			int i = -1, n = files.length;
			for(;++i<n;){
				File f = files[i];
				if(f.isHidden())
					continue;
				long sz = FileHelper.getInstance().qisu_single_specified_file(f,true,itemlist,null);
				m_totalsize += sz;
				f = null;
			}
			//降序排列
			Collections.sort(itemlist, new GuyComparator());
			//适配列表视图
			MagTest.Instance.sendMsg(65536,0,0);
		}
		else{
			List<File> flist = new ArrayList<File>();
			int i = -1,n = files.length;
			for(;++i<n;){
				File f = files[i];
				if(f.isHidden())
					continue;
				int t=0,c=0;
				if(f.isDirectory()){
					t = R.drawable.folder;
					String[] ss = f.list();
					if(null!=ss)c=ss.length;
				}else{
					t = R.drawable.folder;
				}
				Guy o = new Guy(itemlist.size(),f.getAbsolutePath(),f.lastModified(),t,0,c);
				itemlist.add(o);
				flist.add(f);
			}
			//先显示列表视图
			MagTest.Instance.sendMsg(65536,0,0);
			
			//然后用多线程更新每个文件的大小
			FileHelper.getInstance().qisu_all_specified_list_thread(flist,itemlist, true, null, new Runnable(){
					@Override
					public void run(){
						//发送初始化完毕的消息
						//设置列表视图适配器
						for(Guy g:itemlist){m_totalsize += g.getSizeLong();}
						//降序排列
						Collections.sort(itemlist, new GuyComparator());
						MagTest.Instance.sendMsg(32768,0,0);
					}
				});
		}
	}
	
	private int setClearList(boolean isScrollTo){
		if(0<clearlist.size()){//已存在
			if(isScrollTo){
				int pos = mylva.getItemPosition(clearlist.get(clearlist.size()-1));
				scrollTo(pos);
			}
			return clearlist.size();
		}
		//此处不能是其它列表,否则位置不对,ui会刷新错乱
		int i = itemlist.size();
		for(;--i>=0;){
			Guy o = itemlist.get(i);
			if(0==o.getSizeLong()){
				intoClear(o,"✘");
				mylva.updateChangedItem(i,o,listView);
			}
		}
		//Toast.makeText(this,"cleaorlist.size(): "+clearlist.size(),Toast.LENGTH_LONG).show();
		//没有满足条件要清理的,则返回
		if(0>=clearlist.size())return clearlist.size();
		if(isScrollTo){
			int pos = mylva.getItemPosition(clearlist.get(clearlist.size()-1));
			scrollTo(pos);
		}

		return clearlist.size();
	}
	//检索空文件,将之添加到清理列表中。
	private int setClearListFromTemp(List<Guy> list,boolean isScrollTo){
		if(null==clearlist)clearlist = new ArrayList<Guy>();
		if(0<clearlist.size()){//已存在
			if(isScrollTo){
				int pos = mylva.getItemPosition(clearlist.get(clearlist.size()-1));
				scrollTo(pos);
			}
			return clearlist.size();
		}
		int i = list.size();
		for(;--i>=0;){
			Guy o = list.get(i);
			if(0==o.getSizeLong()){
				intoClear(o,"✘");
				int p = mylva.getItemPosition(o);
				mylva.updateChangedItem(p,o,listView);
			}
		}
		//Toast.makeText(this,"cleaorlist.size(): "+clearlist.size(),Toast.LENGTH_LONG).show();
		//没有满足条件要清理的,则返回
		if(0>=clearlist.size())return clearlist.size();
		if(isScrollTo){
			int pos = mylva.getItemPosition(clearlist.get(clearlist.size()-1));
			scrollTo(pos);
		}
		
		return clearlist.size();
	}
	
	//奇速测试版清除功能
	private void qisuBetaVersionClear(){
		clearlist.clear();
		List<Guy> tmplist = new ArrayList<Guy>();
		for(Guy g:itemlist){
			if(g.isSelected())tmplist.add(g);
		}
		//用户没有选择任何一项
		if(0>=tmplist.size()){
			Toast.makeText(MainActivity.this,"没有选中任何文件*★",Toast.LENGTH_LONG).show();
			m_isAsycClear = false;
			return;
		}
		
		//测试版,从用户所选的项目中挑出空的项目执行删除
		int numOfClear = setClearListFromTemp(tmplist,true);
		if(numOfClear>0){
			String str = "删除: "+clearlist.size() +"个\n";
			for(Guy g:clearlist){
				str += g.getName()+" "+mylva.getItemPosition(g) + "\n";
			}
			MagTest.Instance.sendDebuggerMsg(str);
			qisuAsycClear();//阻塞式
			Toast.makeText(MainActivity.this,"清理完毕*★",Toast.LENGTH_LONG).show();
		}else{
			(new COKDialog(MainActivity.this,"温馨提示","测试版，暂不使用删除非空文件功能，防止误删。(如果要删除非空文件,安装正式版即可。)")).show();
			for(Guy g:tmplist){
				g.setSelected(false);
			}
			selectAll.setVisibility(View.GONE);
			//selectAll.setChecked(false);
			setChecked(false);
			btnCancel.setVisibility(View.GONE);
			//收回按钮
			promotedActionsLib.setOnPromotedAction(false);
			mylva.setSelecting(false);
			for(int i = firstVisiblePos;i <= lastVisiblePos;i++){
				mylva.updateChangedItem(i,itemlist.get(i),listView);
			}
		}
	}
	
	//奇速正式版清除功能
	private void qisuOfficialVersionClear(){
		//安全起见,检查有没有遗漏的用户选择项
		clearlist.clear();
		for(Guy g:itemlist){
			if(g.isSelected())clearlist.add(g);
		}
		//用户没有选择任何一项
		if(0>=clearlist.size()){
			Toast.makeText(MainActivity.this,"没有选中任何文件,开启自动检索..*★",Toast.LENGTH_LONG).show();
			int numOfClear = setClearList(true);
			if(numOfClear>0){
				String str = "";
				for(Guy g:clearlist){
					str += g.getName()+" "+mylva.getItemPosition(g) + "\n";
				}
				MagTest.Instance.sendDebuggerMsg(str);
				qisuAsycClear();//阻塞式
				Toast.makeText(MainActivity.this,"清理完毕*★",Toast.LENGTH_LONG).show();
			}
			else
				Toast.makeText(MainActivity.this,"已清理干净*★",Toast.LENGTH_LONG).show();
		}
		String str = "";
		for(Guy g:clearlist){
			str += g.getName()+" "+mylva.getItemPosition(g) + "\n";
		}
		MagTest.Instance.sendDebuggerMsg(str);
		qisuAsycClear();//阻塞式
		Toast.makeText(MainActivity.this,"清理完毕*★",Toast.LENGTH_LONG).show();
		//MyParticles.build(MainActivity.this);
	}
	
	//异步清理
	private void qisuAsycClear() {
		m_isAsycClear = true;
		CSingleThreadPool fixThreadPool = new CSingleThreadPool();
		int i  = clearlist.size();
		for(;--i>=0;){
			Guy b = clearlist.get(i);
			int pos = mylva.getItemPosition(b);
			fixThreadPool.submit(new CRunnableDelete(b,pos,false,callbackDelete));
		}
		//当此方法被调用时，ExecutorService停止接收新的任务
		//并且等待已经提交的任务（包含提交正在执行和提交未执行）执行完成。
		//当所有提交任务执行完毕，线程池即被关闭。
		fixThreadPool.shutdownBlocking();
		fixThreadPool = null;
		if(mylva.isSelecting())cancel();//取消全选
		else mylva.notifyDataSetChanged();//更新ui
		m_isAsycClear = false;
    }
	
	private Action1<Guy> callbackDelete = new Action1<Guy>(){
		@Override
		public void invoke(Guy o1){
			if(null==o1){// 文件删除失败
				return;
			}
			itemlist.remove(o1);
			clearlist.remove(o1);
		}
	};
	
	//滚动停止
	private void onScrollStop(int p2){
		if(p2!=0)return;
	}
	
	private int firstVisiblePos = -1,lastVisiblePos = -1;
	//设置列表视图
	private void setListAdapter(List<Guy> data){
		//适配器指定应用自己定义的xml格式
		listView = findViewById(R.id.listView);
        mylva = new SwipeListAdapter(MainActivity.this, data);
        listView.setAdapter(mylva);
		mylva.setTotalSize(m_totalsize);
		
		SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "删除" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
					getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
																	 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
   
                menu.addMenuItem(deleteItem);
				
				// create "打开" item
                SwipeMenuItem openItem = new SwipeMenuItem(
					getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,									   0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("打开");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);
		
		listView.setOnScrollListener(new AbsListView.OnScrollListener(){
				@Override
				public void onScrollStateChanged(AbsListView p1, int p2){
					onScrollStop(p2);
				}
				@Override
				public void onScroll(AbsListView p1, int p2, int p3, int p4){
					firstVisiblePos = p2; lastVisiblePos = p2+p3-1;
				}
		});
		
        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(int pos, SwipeMenu menu, int index) {
					final int position = pos;
					final Guy item = itemlist.get(position);
					switch (index) {
						case 0:
							// delete 测试版,不删除
							//正在清理中...
							if(m_isAsycClear){
								Toast.makeText(MainActivity.this,"清理中...",Toast.LENGTH_LONG).show();
								break;
							}
							CPopupDialog.showBottomDialog(MainActivity.this, new Runnable(){
									@Override
									public void run(){
										m_isAsycClear = true;
										cancel();//取消全选
										//文件尺寸
										if(0==item.getSizeLong()){
											boolean success = (new CRunnableDelete(item,position,true,callbackDelete)).delete();
											if(success){//成功则更新ui
												mylva.notifyDataSetChanged();
											}
										}else{
											(new COKDialog(MainActivity.this,"温馨提示","测试版，暂不使用删除非空文件功能，防止误删。(如果要删除非空文件,安装正式版即可。)")).show();
										}
										m_isAsycClear = false;
									}
								});
							
							break;
						case 1:
							m_isopenning = true;//正在打开..
							if(R.drawable.file == item.getType()){//不是文件夹
								m_isopenning = false;
								return false;
							}
							cancel();//取消全选
							release();//释放列表资源
							String path = item.getPath();
							setTitleText(path);
							parentpath = path;
							if(524288000 < item.getSizeLong() && 5 < item.getKidsInt()){
								//Toast.makeText(MainActivity.this,"open(true) 多线程",Toast.LENGTH_SHORT).show();
								initFileItemData(parentpath,true);
							}else{
								initFileItemData(parentpath,false);
							}
							m_isopenning = false;//打开完成
							break;
					}
					return false;
				}
			});

        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
				@Override
				public void onSwipeStart(int position) {
					//MagTest.Instance.sendDebuggerMsg("onSwipeStart: "+position);
				}
				@Override
				public void onSwipeEnd(int position) {
					//MagTest.Instance.sendDebuggerMsg("onSwipeEnd: "+position);
				}
			});

        // set MenuStateChangeListener
        listView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
				@Override
				public void onMenuOpen(int position) {
					//MagTest.Instance.sendDebuggerMsg("onMenuOpen: "+position);
				}
				@Override
				public void onMenuClose(int position) {
					//MagTest.Instance.sendDebuggerMsg("onMenuClose: "+position);
				}
			});

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
											   int position, long id) {
					int iid = itemlist.get(position).getId();
					MagTest.Instance.sendMsg(16384,position,iid);
					//showItemLongClick(position,iid);
					//MagTest.Instance.sendDebuggerMsgLong("position: "+position+", iid: "+iid+", name: "+itemlist.get(position).getName() + "\n \n \n");
					return true;
				}
			});
			
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					Guy bf = itemlist.get(p3);
					if(bf.isSelected()){//选中
						outClear(bf,"○");
					}else{ 
						intoClear(bf,"✘");
					}
					mylva.updateChangedItem(p3,bf,listView);
					//MagTest.Instance.sendMsg(8192,p3,bf.getId());
				}
		});
	}
	
	private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
											   getResources().getDisplayMetrics());
    }
	
	private void release(){
		itemlist.clear();
		clearlist.clear();
		m_clearCount = 0;
		m_totalsize = 0;
	}
	
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0 && mylva.isSelecting()) {
                cancel();
				return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //点击取消或返回键的处理逻辑
    private void cancel(){
		selectAll.setVisibility(View.GONE);
		setChecked(false);
		btnCancel.setVisibility(View.GONE);
		selectedAllItemsClearOrNot(false);
		//收回按钮
		promotedActionsLib.setOnPromotedAction(false);
		mylva.setSelecting(false);
        for(int i = firstVisiblePos;i <= lastVisiblePos;i++){
			mylva.updateChangedItem(i,itemlist.get(i),listView);
		}
    }
	
	private void selectedAllItemsClearOrNot(boolean s){
		if(s)
			for(Guy b:itemlist){intoClear(b,"✘");}
		else
			for(Guy b:itemlist){outClear(b,"○");}
	}

	private void intoClear(Guy o,String s){
		o.setSelected(true);
		o.setChoose(s);
		clearlist.add(o);
	}

	private void outClear(Guy o,String s){
		o.setSelected(false);
		o.setChoose(s);
		clearlist.remove(o);
	}

	private void showItemClickMsg(int pos,int id,boolean isScrollTo){
		Guy bf = itemlist.get(pos);
		//int index = getListIndex(clearlist,id);
		if(bf.isSelected()){//选中
			outClear(bf,"○");
		}else{ 
			//Toast.makeText(this,"name: "+bf.getName()+", position:"+pos,Toast.LENGTH_SHORT).show();
			intoClear(bf,"✘");
		}
		mylva.updateChangedItem(pos,bf,listView);
	}
	
	private void showItemLongClick(int pos,int id){
		if(mylva.isSelecting()){
			cancel(); return;
		}
		Guy gf = itemlist.get(pos);
		intoClear(gf,"✘");
		selectAll.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.VISIBLE);
		//按钮弹出
		promotedActionsLib.setOnPromotedAction(true);
		//设置全选,并更新可见项
		mylva.setSelecting(true);
		for(int i = firstVisiblePos;i <= lastVisiblePos;i++){
			mylva.updateChangedItem(i,itemlist.get(i),listView);
		}
	}
	
	private void updateItemData(int position,long sz){
		Guy b = (Guy)mylva.getItem(position);
		b.setSize(sz);
		mylva.updateChangedItem(position,b,listView);
	}
	
	private void scrollTo(final int position){
		listView.post(new Runnable() {
				@Override
				public void run() {
					listView.smoothScrollToPosition(position);
				}
			});
	}
	
	private void onInitCompleted(){
		mylva.setTotalSize(m_totalsize);
		lastVisiblePos = 5;
		for(int i = firstVisiblePos;i <= lastVisiblePos;i++){
			mylva.updateChangedItem(i,itemlist.get(i),listView);
		}
		rippleBackground.stopRippleAnimation();
		if(View.GONE == btnReturn.getVisibility())btnReturn.setVisibility(View.VISIBLE);
		if(!btnReturn.isClickable())btnReturn.setClickable(true);
		Toast.makeText(this,itemlist.size()+" onInitCompleted ヾ(❀╹◡╹)ﾉ~",Toast.LENGTH_SHORT).show();
		//查找并显示可清理列表
		/*new Thread(){
			@Override
			public void run(){
				try{
					Thread.sleep(1000);
				}
				catch (InterruptedException e){e.printStackTrace();}
				finally{
					MagTest.Instance.sendMsg(2048,0,0);
				}
			}
		}.start();*/
	}
	
	//消息句柄
	private Handler handler = new Handler(){ 
		@Override
        public void handleMessage(Message msg){
		  	Bundle bdl = msg.getData();
			int msgid = bdl.get("msgid");
			if(msgid == 1024){
				String s = (String)bdl.get("debugger");
				int l = bdl.get("length");
				showToast(s,l);
			}else if(msgid == 2048){
				setClearList(true);
			}else if(msgid == 4096){
				int p = bdl.getInt("position");
				long sz = bdl.getLong("+size");
				updateItemData(p,sz);
			}else if(msgid == 8192){
				int p = bdl.getInt("position");
				int id = (int)bdl.getLong("+size");
				showItemClickMsg(p,id,true);
			}else if(msgid == 16384){
				int p = bdl.getInt("position");
				int id = (int)bdl.getLong("+size");
				showItemLongClick(p,id);
			}else if(msgid == 32768){
				onInitCompleted();
			}else if(msgid == 65536){
				setListAdapter(itemlist);
			}
			return;
        }
    };
	public Handler getHandler() {
  		return handler;
  	}
	private void showToast(String s,int l){
		if(l==0)Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
		else if(l>0)Toast.makeText(this,s,Toast.LENGTH_LONG).show();
	}
}