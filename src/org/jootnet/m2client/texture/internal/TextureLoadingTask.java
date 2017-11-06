package org.jootnet.m2client.texture.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jootnet.m2client.texture.Texture;

/**
 * 纹理加载任务
 */
final class TextureLoadingTask implements Callable<Texture> {

	// 所属纹理加载器
	private TextureLoader loader;
	// 要加载的纹理索引
	private int index;
	public int getIndex() {
		return index;
	}
	/** 加载任务 */
	private Future<Texture> loadFuture = null;
	
	TextureLoadingTask(TextureLoader loader, int index) {
		this.loader = loader;
		this.index = index;
	}
	
	@Override
	public Texture call() throws Exception {
		return Textures.get(loader.getDataFileName()).tex(index);
	}
	
	boolean update() {
		if (loadFuture == null) {
			loadFuture = loader.getThreadPool().submit(this);
		}
		else if (loadFuture.isDone()) {
			try {
				Textures.putTexture(loader.getDataFileName(), index, loadFuture.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO 可能的异常处理？上抛？通知？
			}
			return true;
		}
		return false;
	}
}
