package org.jootnet.m2client.graphics;

/**
 * 绘制信息
 * 
 * @author 云
 */
public interface GraphicsContext {
	/**
	 * 获取当前游戏区域宽度
	 * 
	 * @return 宽度
	 */
	int getWidth();
	
	/**
	 * 获取当前游戏区域高度
	 * 
	 * @return 高度
	 */
	int getHeight();
	
	/**
	 * 获取当前游戏绘制每秒最大刷新次数
	 * <br>
	 * 实际刷新次数应该小于此数值
	 * 
	 * @return 最大帧数
	 */
	int getMaxFps();
	
	/**
	 * 获取两帧间隔
	 * 
	 * @return 当前帧与前一帧之间的间隔(单位为毫秒)
	 */
	float getDeltaTime();
	
	/**
	 * 获取渲染帧数
	 * 
	 * @return 前一秒总共渲染帧数
	 */
	int getFramesPerSecond();
	
	/**
	 * 获取当前帧数
	 * 
	 * @return 当前为这一秒第几帧
	 */
	int getCurrentFrame();
	
	/**
	 * 获取当前是{@link #getMaxFps() maxFps}内第几帧
	 * <br>
	 * 此参数不是当前(渲染)秒内的帧数，而是在一次渲染循环中的第几帧
	 * <br>
	 * 每发生一次有效渲染，此数值加1
	 * <br>
	 * 此数值最大为{@link #getMaxFps() maxFps}，最小为1
	 * <br>
	 * 此参数作为精灵帧控制参数
	 * 
	 * @return 获取当前为渲染循环中第几次有效渲染
	 */
	int getTickFrames();
}
