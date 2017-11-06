package org.jootnet.m2client.texture.internal;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.util.SDK;

public final class Textures {

	private static Map<String, ImageLibrary> libraries = new HashMap<String, ImageLibrary>();
	private static Object lib_locker = new Object();
	
	/**
	 * 从指定路径中解析出一个图片库并存入内存缓存
	 * 
	 * @param libName
	 * 		图片库名称
	 * @return 图片库对象
	 */
	static final ImageLibrary get(String libName) {
		synchronized (lib_locker) {
			if(libraries.containsKey(libName))
				return libraries.get(libName);
			try{
				String libPath = System.getProperty("org.jootnet.m2client.data.dir", System.getProperty("user.dir"));
				if(!libPath.endsWith(File.separator))
					libPath += File.separator;
				libPath += libName;
				String wzlPath = SDK.changeFileExtension(libPath, "wzl");
				WZL wzl = new WZL(wzlPath);
				if(wzl.isLoaded()) {
					libraries.put(libName, wzl);
					return wzl;
				}
				String wisPath = SDK.changeFileExtension(libPath, "wis");
				WIS wis = new WIS(wisPath);
				if(wis.isLoaded()) {
					libraries.put(libName, wis);
					return wis;
				}
				String wilPath = SDK.changeFileExtension(libPath, "wil");
				WIL wil = new WIL(wilPath);
				if(wil.isLoaded()) {
					libraries.put(libName, wil);
					return wil;
				}
				return null;
			}catch(RuntimeException ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}

	private static Object tex_locker = new Object();
	private static Stack<TextureLoader> texLoaders = new Stack<TextureLoader>();
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	private static Set<String> loadings = new HashSet<String>();
	static class UpdateThread extends Thread {
		UpdateThread() {
			setName("TextureLoader-autoUpdate-Thread");
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while(true) {
				boolean allDone = true;
				for(TextureLoader l : texLoaders) {
					if(!l.update())
						allDone = false;
				}
				if(allDone) break;
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	private static UpdateThread updateThread = new UpdateThread();
	private static String buildKey(String dataFileName, int index) {
		return ((char)(dataFileName.length() + '0')) + dataFileName + index;
	}
	// 给包内部对象使用的函数，主要是给异步加载对象使用的存入异步加载成功的纹理数据
	static void putTexture(String libName, int index, Texture tex) {
		synchronized (tex_locker) {
			String key = buildKey(libName, index);
			textures.put(key, tex);
			loadings.remove(key);
		}
	}
	
	/**
	 * 异步读取一张纹理到缓存
	 * <br>
	 * 需要获取这张纹理的话请使用{@link #getTextureFromCache(String, int) getTextureFromCache}函数
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @see #isTextureInLoad(String, int)
	 * @see #isTextureInCache(String, int)
	 */
	public static void loadTextureAsync(String dataFileName, int index) {
		synchronized (tex_locker) {
			TextureLoader loader = null;
			boolean containFlag = false;
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName)) {
					containFlag = true;
					loader = l;
					break;
				}
			}
			if(!containFlag) {
				loader = new TextureLoader(dataFileName);
				texLoaders.add(loader);
			}
			loader.load(index);
			if(!updateThread.isAlive())
				updateThread.start();
		}
	}
	
	/**
	 * 立即获取某张纹理
	 * <br>
	 * 不加入缓存
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 获取到的纹理，不可能为null，但可能为空，使用{@link Texture#empty()}判定
	 * @see #getTextureFromCache(String, int)
	 */
	public static Texture getTextureImmediately(String dataFileName, int index) {
		return get(dataFileName).tex(index);
	}
	
	/**
	 * 从缓存中获取已异步加载的纹理
	 * <br>
	 * 需要先请求异步加载{@link #loadTextureAsync(String, int) loadTextureAsync}
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 获取到的纹理，可能为null
	 * @see #getTextureImmediately(String, int)
	 */
	public static Texture getTextureFromCache(String dataFileName, int index) {
		synchronized (tex_locker) {
			return textures.get(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * 判定指定纹理是否已存在于缓存中
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 指定纹理是否已加入缓存
	 */
	public static boolean isTextureInCache(String dataFileName, int index) {
		synchronized (tex_locker) {
			return textures.containsKey(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * 判定指定纹理是否正在异步加载
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 指定纹理是否已请求异步加载并还未成功
	 */
	public static boolean isTextureInLoad(String dataFileName, int index) {
		synchronized (tex_locker) {
			return loadings.contains(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * 获取某个图片库的异步加载进度
	 * 
	 * @param dataFileName
	 * 		图片库名称
	 * @return
	 * 		0~1之间的小数表示百分比；为1表示(该库)所有被请求的异步加载均成功；一般不为0，为0要么是一个都还未成功(可能性极低)，要么是该库内纹理从未被请求异步加载
	 */
	public static float getLoadProgress(String dataFileName) {
		synchronized (tex_locker) {
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName))
					return l.getProgress();
			}
			return 0f;
		}
	}
	
	/**
	 * 获取异步加载进度(所有库)
	 * 
	 * @return
	 * 		0~1之间的小数表示百分比；为1表示所有被请求的异步加载均成功；一般不为0，为0要么是一个都还未成功(可能性极低)，要么是还没有纹理被请求异步加载
	 */
	public static float getLoadProgress() {
		synchronized (tex_locker) {
			if(texLoaders.isEmpty()) return 0f;
			float ap = 0f;
			for(TextureLoader l : texLoaders) {
				ap += l.getProgress();
			}
			return ap / texLoaders.size(); // FIXME float除int会不会不为1而为0.999999???
		}
	}
}
