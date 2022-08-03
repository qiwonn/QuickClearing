package com.wq.qs;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
/**
 * 异步单线程池,保证池内部始终维护着唯一的单个线程
 * author: qi.wong
 */
public class CSingleThreadPool
{
	private ExecutorService threadPool = null;

	//private CompletionService<Integer> cs = null;
	
	public CSingleThreadPool(){
		threadPool = Executors.newSingleThreadExecutor();
		//cs = new ExecutorCompletionService<Integer>(threadPool);
	}
	
	public void submit(Runnable r){
		//cs.submit(c);
		threadPool.submit(r);
	}
	
	public void setCallback(Runnable r){
		
	}
	
	public void shutdown(){
		threadPool.shutdown();
	}
	
	public void shutdownBlocking(){
		threadPool.shutdown();
		try{
			boolean loop = true;
			do{
				loop=!threadPool.awaitTermination(2, TimeUnit.SECONDS);
			}while(loop);
		}catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void newThreadCheckingAwaitTermination(final Runnable r){
		ExecutorService checkthreadPool = Executors.newSingleThreadExecutor();
		checkthreadPool.submit(new Runnable(){
				@Override
				public void run(){
					shutdownBlocking();
					if(null!=r)r.run();
				}
		});
		checkthreadPool.shutdown();
	}
}