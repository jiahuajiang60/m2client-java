package org.jootnet.m2client.map;

import org.jootnet.m2client.graphics.Drawable;
import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.map.internal.MapInfo;
import org.jootnet.m2client.texture.Texture;

public abstract class Map implements Drawable {
	
	/**
	 * 地图磁块宽
	 * <br>
	 * 逻辑坐标点的屏幕像素宽度
	 */
	private final static byte PIXEL_WIDTH_PER_TILE = 48;
	/**
	 * 地图磁块高
	 * <br>
	 * 逻辑坐标点的屏幕像素高度
	 */
	private final static byte PIXEL_HEIGHT_PER_TILE = 32;
	/** 绘制地图时向左延伸块儿数量 */
	private final static byte EXTEND_LEFT = 5;
	/** 绘制地图时向右延伸块儿数量 */
	private final static byte EXTEND_RIGHT = 5;
	/** 绘制地图时向下延伸块儿数量 */
	private final static byte EXTEND_BOTTOM = 5;
	
	/** 地图宽度 */
	private int mw;
	/** 地图高度 */
	private int mh;
	/** 角色身处横坐标 */
	private short x;
	/** 角色身处纵坐标 */
	private short y;
	/** 地图绘制区域左上角(相对于游戏区域直角坐标系) */
	private short px;
	/** 地图绘制区域右上角(相对于游戏区域直角坐标系) */
	private short py;
	/** 地图绘制区域宽度 */
	private short gw;
	/** 地图绘制区域高度 */
	private short gh;
	/** 绘图区域左上角为地图块第几列 */
	private short tws;
	/** 绘图区域左上角为地图块第几行 */
	private short ths;
	/** 绘制区域右下角为地图块第几列 */
	private short twe;
	/** 绘制区域右下角为地图块第几行 */
	private short the;
	/**
	 * 纹理图片需要准备的坐标左上角列数
	 * <br>
	 * 对于地图绘制而要，需要先预测角色可能出现的坐标
	 * <br>
	 * 我们将绘制区域2倍大小作为预测的角色可能出现的位置
	 */
	private short pws;
	/**
	 * 纹理图片需要准备的左上角行数
	 */
	private short phs;
	/**
	 * 纹理图片需要准备的右下角列数
	 */
	private short pwe;
	/**
	 * 纹理图片需要准备的右下角行数
	 */
	private short phe;
	
	private String name;
	private MapInfo info;
	protected Map(String name, MapInfo info) {
		this.name = name;
		this.info = info;
		// 地图宽度(像素)
		mw = info.getWidth() * PIXEL_WIDTH_PER_TILE;
		// 地图高度(像素)
		mh = info.getHeight() * PIXEL_HEIGHT_PER_TILE;
	}
	
	/**
	 * 获取角色身处横坐标
	 * 
	 * @return 横坐标
	 */
	public int roleX() {
		return x;
	}
	/**
	 * 获取角色身处纵坐标
	 * 
	 * @return 纵坐标
	 */
	public int roleY() {
		return y;
	}
	
	private boolean moved;
	/**
	 * 移动角色身处的坐标(相对于地图)
	 * 
	 * @param x
	 * 		横坐标
	 * @param y
	 * 		纵坐标
	 */
	public void move(int x, int y) {
		this.x = (short) x;
		this.y = (short) y;
		moved = true;
	}
	
	@Override
	public boolean adjust(GraphicsContext ctx) {
		if(moved) {
			// 计算绘制区域左上角坐标
			// 绘制区域左上角x
			px = (short) (ctx.getWidth() > mw ? (ctx.getWidth() - mw) / 2 : 0);
			// 绘制区域左上角y
			py = (short) (ctx.getHeight() > mh ? (ctx.getHeight() - mh) / 2 : 0);
			// 计算绘制宽度和高度
			// 绘制宽度
			gw = (short) (ctx.getWidth() > mw ? mw : ctx.getWidth());
			// 绘制高度
			gh = (short) (ctx.getHeight() > mh ? mh : ctx.getHeight());
	
			// 绘图区域左上角为地图块第几列
			tws = (short) (x - (gw / PIXEL_WIDTH_PER_TILE - 1) / 2);
			if (tws < 0)
				tws = 0;
			// 绘图区域左上角为地图块第几行
			ths = (short) (y - (gh / PIXEL_HEIGHT_PER_TILE - 1) / 2);
			if (ths < 0)
				ths = 0;
			
			// 绘制区域右下角为地图块第几列
			// 将绘制区域向右移动，保证对象层不缺失
			twe = (short) (tws + gw / PIXEL_WIDTH_PER_TILE + EXTEND_RIGHT);
			if(the > info.getWidth())
				the = info.getWidth();
			// 绘制区域右下角为地图块第几行
			// 将绘制区域向下延伸，保证对象层不缺失
			the = (short) (ths + gh / PIXEL_HEIGHT_PER_TILE + EXTEND_BOTTOM);
			if(the > info.getHeight())
				the = info.getHeight();
	
			// 纹理准备参数
			pws = (short) (x - (gw / PIXEL_WIDTH_PER_TILE - 1));
			if (pws < 0)
				pws = 0;
			phs = (short) (y - (gh / PIXEL_HEIGHT_PER_TILE - 1));
			if (phs < 0)
				phs = 0;
			pwe = (short) (tws + gw / PIXEL_WIDTH_PER_TILE * 2);
			if (pwe > info.getWidth())
				pwe = info.getWidth();
			phe = (short) (ths + gh / PIXEL_HEIGHT_PER_TILE * 2);
			if (phe > info.getHeight())
				phe = info.getHeight();
	
			// 对于无法置于绘制区域“正中”的情况，在上面的起始位置中相应坐标向上移动了一格，绘制终止坐标也要相应的上移
			if ((gw / PIXEL_WIDTH_PER_TILE - 1) % 2 != 0)
				twe -= 1;
			if ((gh / PIXEL_HEIGHT_PER_TILE - 1) % 2 != 0)
				the -= 1;
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int offsetX() {
		return 0;
	}

	@Override
	public int offsetY() {
		return 0;
	}

	@Override
	public Texture content() {
		// TODO Auto-generated method stub
		return null;
	}

}
