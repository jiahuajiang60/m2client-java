package org.jootnet.m2client.graphics;

import org.jootnet.m2client.texture.Texture;

/**
 * 可绘制对象
 * 
 * @author 云
 */
public interface Drawable {

	/**
	 * 更新绘制内容
	 * 
	 * @param ctx
	 * 		绘制信息上下文
	 * @return
	 * 		当前绘制内容是否需要绘制到父元素中
	 */
	boolean adjust(GraphicsContext ctx);
	
	/**
	 * 返回横向偏移量
	 * 
	 * @return 内容在父元素中的横向偏移量
	 */
	int offsetX();
	
	/**
	 * 返回纵向偏移量
	 * 
	 * @return 内容在父元素中的纵向偏移量
	 */
	int offsetY();
	
	/**
	 * 返回绘制内容
	 * 
	 * @return 要会知道父元素中的内容
	 */
	Texture content();
}















