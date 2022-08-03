package com.wq.qs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.ArrayList;

/**
 * 异步多线程池,保证池内部始终维护着固定的指定数量的线程
 * author: qi.wong
 */
public class CMultiThreadPool
{
	private ExecutorService threadPool = null;
	
	private List<Future> futures = null;

	public CMultiThreadPool(int n){
		threadPool = Executors.newFixedThreadPool(n);
		futures = new ArrayList<Future>();
	}

	public void submit(Runnable r){
		Future f = threadPool.submit(r);
		futures.add(f);
	}

	public void shutdown(){ threadPool.shutdown();}
	
	//阻塞直到所有的将来完成
	public void setOnAllDone(Runnable r){
		// A）等待所有runnables完成（阻塞）
		try
		{
			for (Future f : futures)
				f.get();// get将阻塞直到将来完成
		}
		catch (ExecutionException e)
		{}
		catch (InterruptedException e)
		{} 

		// B）检查是否所有runnables都已完成（非阻塞）
		boolean allDone = true; 
		for(Future f : futures){
			allDone &= f.isDone(); //检查未来是否已完成
		}
		if(allDone && null!=r)r.run();
	}
}