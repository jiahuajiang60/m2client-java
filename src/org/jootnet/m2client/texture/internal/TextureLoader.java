package org.jootnet.m2client.texture.internal;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 纹理读取类
 * <br>
 * 此类可实例化，类实例线程安全<b>使用多个类实例实现多线程加载以提升速度</b>
 */
final class TextureLoader {
	// 图片库库名称
	private String dataFileName;
	String getDataFileName() {
		return dataFileName;
	}
	TextureLoader(String dataFileName) {
		this.dataFileName = dataFileName;
	}
	
	/** 已经加载的纹理数量 */
	private volatile int loaded = 0;
	/** 需要加载的纹理数量 */
	private volatile int toLoad = 0;
	
	/** 任务队列 */
	private Stack<TextureLoadingTask> tasks = new Stack<TextureLoadingTask>();

	/** 线程池 */
	private final ExecutorService threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "TextureLoader-Load-Thread");
			thread.setDaemon(true);
			return thread;
		}
	});
	ExecutorService getThreadPool() {
		return threadPool;
	}
	
	/**
	 * 把纹理加载放入任务队列
	 * 
	 * @param index
	 * 		纹理索引
	 */
	synchronized void load(int index) {
		if (tasks.size() == 0) {
			loaded = 0;
			toLoad = 0;
		}
		tasks.push(new TextureLoadingTask(this, index));
		toLoad++;
	}
	/**
	 * 进行纹理加载的操作
	 * <br>
	 * 请逐帧调用
	 * 
	 * @return 如果所有需要加载的纹理都加载了则返回true，反之返回false
	 */
	synchronized boolean update() {
		if(tasks.size() == 0) return true;
		return updateTask() && tasks.size() == 0;
	}
	/** 更新当前执行的任务状态<br>XXX遍历任务队列，不限于每帧只加载一个纹理 */
	private boolean updateTask() {
		boolean result = false;
		Iterator<TextureLoadingTask> itTasks = tasks.iterator();
		while(itTasks.hasNext()) {
			TextureLoadingTask task = itTasks.next();
			if (task.update()) {
				loaded++;
				itTasks.remove();
				result = true;
			}
		}
		return result;
	}
	/** 阻塞线程等待所有加载完成 */
	void finishLoading() {
		while (!update())
			Thread.yield();
	}
	/** 获取加载百分比 */
	float getProgress() {
		if (toLoad == 0) return 1;
		return Math.min(1, loaded / (float)toLoad);
	}
}
