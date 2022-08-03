package com.wq.qs;
import java.util.Comparator;
public class GuyComparator implements Comparator<Guy> {
    @Override
    public int compare(Guy f1, Guy f2) {
		// 按照大小进行排列
		long s1 = f1.getSizeLong();
		long s2 = f2.getSizeLong();
        if(s1 == s2) {
            return 0;
        }
        if(s1 > s2) {
            return -1;
        }
        if(s1 < s2) {
            return 1;
        }
        // 按照字母顺序排列
        return f1.getName().compareToIgnoreCase(f2.getName());
    }
}