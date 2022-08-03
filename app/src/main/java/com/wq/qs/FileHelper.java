package com.wq.qs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import android.graphics.*;
import android.os.Environment;
import java.io.FileOutputStream;
import java.io.*;
import java.util.*;
import com.wq.qs.Action1;
import java.text.*;
import java.util.concurrent.*;

public class FileHelper {

    private static FileHelper fileHelper;

    public static FileHelper getInstance() {
        if (fileHelper == null)
            fileHelper = new FileHelper();
        return fileHelper;
    }


	@SuppressWarnings("unused")
	public void str2File(String result, String outPath) {
		try{
			String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
			
			File txt = new File(folder + outPath);
			if (!txt.exists()) {
				txt.createNewFile();
			}
			byte bytes[] = new byte[512];
			bytes = result.getBytes();
//		int b = bytes.length; // ???????????????
			FileOutputStream fos = new FileOutputStream(txt);
//		fos.write(bytes, 0, b);
			fos.write(bytes);
			fos.flush();
			fos.close();
		}catch (IOException e){
			e.printStackTrace();
		}

	}


	
	
    /**
     * Get file extension from string passed as param
     *
     * @param url
     * @return file extension
     */
    public String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    /**
     * Performing copy file operation
     *
     * @param srcFile
     * @param destDir
     * @return true -> success, false -> fail
     */
    public boolean copyFile(File srcFile, File destDir) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        in = new FileInputStream(srcFile);
        out = new FileOutputStream(destDir.getPath() + File.separator + srcFile.getName());

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        in = null;

        // write the output file (You have now copied the file)
        out.flush();
        out.close();
        out = null;

        return true;
    }

    // 立即删除 the directory inside list of files and inner Directory
    public boolean deleteFileOrDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
			int i = -1,n = children.length;
            for (; ++i < n;) {
                boolean success = deleteFileOrDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
		boolean b = dir.delete();
        return b;
    }
	
	// 等到程序退出JVM(Java虚拟机)时才删除 会先缓存 the directory inside list of files and inner Directory
    public void deleteFileOrDirOnExit(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
			int i= -1,n = children.length;
            for (; ++i < n;) {
                deleteFileOrDirOnExit(new File(dir, children[i]));
            }
        }
        // The directory is now empty so delete it
        dir.deleteOnExit();
    }
	
	public String loadTxt(String path){
		String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder();
		try{
			File file = new File(folder+path);
			if(null==file){
				return "";
			}
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine())!=null){
				content.append(line);
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			try{
				if(reader!=null){
					reader.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return content.toString();
	}
	
	public boolean fuckBitmap2Pic(Bitmap bp,String ext) {
		File file = new File(Environment.getExternalStorageDirectory()  
							 .getAbsolutePath() + "/AppProjects/" + System.currentTimeMillis() + ext);// 保存到sdcard根目录下
        //Log.i("CXC", Environment.getExternalStorageDirectory().getPath());  
        FileOutputStream fos = null;  
        try {  
            fos = new FileOutputStream(file);  
            bp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
			return false;
        } finally{
			try {  
				fos.close();  
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	//获取指定文件夹路径下的文件列表
	public List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        List<File> result = new ArrayList<>();
        if (files == null) {
            return new ArrayList<File>();
        }
		int i = -1,n = files.length;
        for (;++i < n;) {
            result.add(files[i]);
        }
        Collections.sort(result, new FileComparator());
        return result;
    }
	
	//多线程池
	public void qisu_all_specified_list_thread(List<File> files,final List<Guy> olist,final boolean skipHiding,final Action1<Guy> prog,final Runnable completed) {
		CMultiThreadPool mFixThreadPool = new CMultiThreadPool(3);
		int i = -1,n = files.size();
		for (;++i < n;) {
			final File f = files.get(i);
			final Guy o = olist.get(i);
			if(skipHiding && f.isHidden())
				continue;
			if(f.isDirectory() && 0<f.length()){
				mFixThreadPool.submit(new Runnable(){
						@Override
						public void run(){
							qisu_single_specified_file_o(f,skipHiding,o,prog);
						}
					});
			}
			else
				qisu_single_specified_file_o(f,skipHiding,o,prog);
		}
		mFixThreadPool.setOnAllDone(completed);
	}
	
	//单线程池
	public List<Guy> qisu_all_specified_list_single_thread(File[] files,final boolean skipHiding,final Action1<Guy> prog,final Runnable completed) 
	{
		CSingleThreadPool singleThreadPool = new CSingleThreadPool();
		final List<Guy> olist = new ArrayList<Guy>();
		int i = -1,n = files.length;
		for (;++i < n;) {
			final File f = files[i];
			if(skipHiding && f.isHidden())
				continue;
			int c = f.isDirectory()? f.list().length:0;
			//此处方法的第一个参数不能用i,因为可能过滤掉隐藏的文件
			final Guy o = new Guy(olist.size(),f.getAbsolutePath(),f.lastModified(),-1,0,c);
			olist.add(o);
			singleThreadPool.submit(new Runnable(){
					@Override
					public void run(){
						qisu_single_specified_file_o(f,skipHiding,o,null);
						if(null!=prog)
							prog.invoke(null);
					}
			});
		}
		//singleThreadPool.shutdown();
		//singleThreadPool.newThreadCheckingAwaitTermination(completed);
		return olist;
	}
	public int getFileCount(File file,boolean skipHiding){
		File[] ff = file.listFiles();
		int rm = ff.length;
		int m = 0;
		for(int i = 0;i<rm;i++){
			if(skipHiding && ff[i].isHidden())
				continue;
			m++;
		}
		return m;
	}
	public long qisu_single_specified_file_o(File f,boolean skipHiding,final Guy o,final Action1<Guy> prog)
	{
		long totalsize = 0;
		if (f.isDirectory())// 如果是目录，则递归。
		{
			o.setType(R.drawable.folder);
			long size = getFolderSize(f,skipHiding,o,prog);
			o.setSize(size);
			totalsize += size;
			if(null!=prog)prog.invoke(o);
		}
		else if (f.isFile()){
			if(skipHiding && f.isHidden())
				return totalsize;
			o.setType(R.drawable.file);
			long size = f.length();
			o.setSize(size);
			totalsize += size;
			if(null!=prog)prog.invoke(o);
		}
		return totalsize;
	}
	
	
	public List<Guy> qisu_all_specified_list(List<File> files,boolean skipHiding,final Action1<Guy> prog) 
	{
		List<Guy> olist = new ArrayList<Guy>();
		int i = -1,n = files.size();
		for (;++i < n;) {
			File f = files.get(i);
			if(skipHiding && f.isHidden())
				continue;
			qisu_single_specified_file(f,skipHiding,olist,null);
			if(null!=prog)
				prog.invoke(null);
		}
		return olist;
	}
	
	public long qisu_single_specified_file(File f,boolean skipHiding,List<Guy> list,final Action1<Guy> prog)
	{
		long totalsize = 0;
		if (f.isDirectory())// 如果是目录，则递归。
		{
			String fname = f.getAbsolutePath();
			int t = R.drawable.folder;
			File[] ff = f.listFiles();
			int rm = ff.length;
			int m = 0;
			for(int i = 0;i<rm;i++){
				if(skipHiding && ff[i].isHidden())
					continue;
				m++;
			}
			long lm = f.lastModified();//最近修改的日期时间
			Guy o = new Guy(list.size(),fname,lm,t,0,m);
			list.add(o);
			long size = getFolderSize(f,skipHiding,o,prog);
			o.setSize(size);
			totalsize += size;
			if(null!=prog)prog.invoke(o);
		}
		else if (f.isFile()){
			if(skipHiding && f.isHidden())
				return totalsize;
			String fname = f.getAbsolutePath();
			int t = R.drawable.file;
			long lm = f.lastModified();
			Guy o = new Guy(list.size(),fname,lm,t,0,-1);
			list.add(o);
			long size = f.length();
			o.setSize(size);
			totalsize += size;
			if(null!=prog)prog.invoke(o);
		}
		return totalsize;
	}
	
	public List<Guy> qisu_all_specified_path(String path,boolean skipHiding,final Action1<Guy> prog) {
		List<Guy> olist = new ArrayList<Guy>();
		File file = new File(path);
		File[] files = file.listFiles();
		try{
			int i = -1,n = files.length;
			for (;++i < n;) {
				File f = files[i];
				if(skipHiding && f.isHidden())
					continue;
				qisu_single_specified_file(f,skipHiding,olist,prog);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return olist;
	}

	/**
	 * 获取指定文件夹大小
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private long getFolderSize(File file,boolean skipHiding,final Guy o,final Action1<Guy> prog) 
	{
		long size = 0;
		File[] flist = file.listFiles();
		int i = -1,n = flist.length;
		for (;++i < n;){
			File f = flist[i];
			//if(skipHiding && f.isHidden())
			//	continue;
			if (f.isDirectory()){
				size += getFolderSize(f,skipHiding,o,prog);
			}
			else if(f.isFile()){
				long a = f.length();
				size += a;
				o.addSize(a);
				if(null!=prog)prog.invoke(o);
			}
		}
		return size;
	}
	/**
	 * 转换文件大小
	 * @param fileS
	 * @return
	 */
	public String FormatFileSize(long fileS)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize="0B";
		if(fileS==0){
			return wrongSize;
		}
		if (fileS < 1024){
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576){
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		}
		else if (fileS < 1073741824){
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		}
		else{
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}
	/**
	 * 转换文件大小,指定转换的类型
	 * @param fileS 
	 * @param sizeType 
	 * @return
	 */
	private double FormatFileSize(long fileS,int sizeType)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
			case 0://B
				fileSizeLong=Double.valueOf(df.format((double) fileS));
				break;
			case 1://KB
				fileSizeLong=Double.valueOf(df.format((double) fileS / 1024));
				break;
			case 2://MB
				fileSizeLong=Double.valueOf(df.format((double) fileS / 1048576));
				break;
			case 3://GB
				fileSizeLong=Double.valueOf(df.format((double) fileS / 1073741824));
				break;
			default:
				break;
		}
		return fileSizeLong;
	}
	
}