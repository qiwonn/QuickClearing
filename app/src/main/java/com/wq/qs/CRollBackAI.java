package com.wq.qs;

public class CRollBackAI
{
	public static void rollOffset(final int pos,final int first,final int last,final int max,final Action1<Integer> r){
		int offset = 0;
		if(last - pos < pos - first){
			//靠近last
			offset = last - pos + 1;
			if(offset <= first){
				offset *= -1;
				r.invoke(Integer.valueOf(offset));
			}else{
				offset = pos + 1;
				if(offset <= max - last){
					r.invoke(Integer.valueOf(offset));
				}
			}
		}else{
			//靠近first
			offset = pos - first + 1;
			if(offset <= max - last){
				r.invoke(Integer.valueOf(offset));
			}else{
				offset = last - pos + 1;
				if(offset <= first){
					offset *= -1;
					r.invoke(Integer.valueOf(offset));
				}
			}
		}
	}
}