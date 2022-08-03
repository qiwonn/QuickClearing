package com.wq.qs;
import java.io.File;
import com.wq.qs.Action1;

public class CRunnableDelete implements Runnable
{
	private Guy b = null;
	private int p = -1;
	private boolean isShowTip = false;
	private Action1<Guy> callback = null;
	public CRunnableDelete(Guy bb,int pp,boolean isShowTip,Action1<Guy> callback){
		b = bb;
		p = pp;
		this.isShowTip = isShowTip;
		this.callback = callback;
	}
	
	@Override
	public void run(){
		delete();
	}
	
	public boolean delete() {
		boolean isSuccess=true;
        try {
            isSuccess = FileHelper.getInstance().deleteFileOrDir(new File(b.getPath()));
        } catch (Exception e) {
			e.printStackTrace();
        }finally{
			if(isSuccess){
				if(null!=callback)callback.invoke(b);
				if(isShowTip)MagTest.Instance.sendDebuggerMsg("已删除 "+b.getPath()+" ✔");
			}else {
				if(null!=callback)callback.invoke(null);
				MagTest.Instance.sendDebuggerMsgLong(b.getPath()+"\n 删除 "+b.getName()+" 失败 ✘");
			}
			return isSuccess;
		}
    }
}