package org.jootnet.m2client.texture.internal;

import java.io.Closeable;

import org.jootnet.m2client.texture.Texture;

/**
 * 图片库通用接口
 * 
 * @author johness
 */
interface ImageLibrary extends Closeable {

	/**
	 * 获取图片库中图片数量
	 * 
	 * @return 图片数量
	 */
	int count();
	
	/**
	 * 获取图片库中指定索引的图片数据
	 * 
	 * @param index
	 * 		图片索引
	 * @return 对应图片数据
	 */
	Texture tex(int index);
}
