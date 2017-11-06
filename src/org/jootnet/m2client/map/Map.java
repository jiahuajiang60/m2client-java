package org.jootnet.m2client.map;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jootnet.m2client.graphics.Drawable;
import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.map.internal.MapInfo;
import org.jootnet.m2client.map.internal.MapTileInfo;
import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.texture.internal.Textures;

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
	
	private Texture mapTex = null;
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
			moved = false;
		}
		if(mapTex == null || mapTex.getWidth() != ctx.getWidth() || mapTex.getHeight() != ctx.getHeight())
			mapTex = new Texture(new byte[ctx.getWidth() * ctx.getHeight() * 3], (short)ctx.getWidth(), (short)ctx.getHeight());
		
		// 绘制，并加入缓存
		List<Integer> tileIdx = new ArrayList<Integer>();
		List<Integer> smTileIdx = new ArrayList<Integer>();
		List<Integer> obj0Idx = new ArrayList<Integer>();
		List<Integer> obj2Idx = new ArrayList<Integer>();
		List<Integer> obj3Idx = new ArrayList<Integer>();
		List<Integer> obj4Idx = new ArrayList<Integer>();
		List<Integer> obj5Idx = new ArrayList<Integer>();
		List<Integer> obj6Idx = new ArrayList<Integer>();
		List<Integer> obj7Idx = new ArrayList<Integer>();
		List<Integer> obj8Idx = new ArrayList<Integer>();
		List<Integer> obj9Idx = new ArrayList<Integer>();
		List<Integer> obj10Idx = new ArrayList<Integer>();
		List<Integer> obj11Idx = new ArrayList<Integer>();
		List<Integer> obj12Idx = new ArrayList<Integer>();
		List<Integer> obj13Idx = new ArrayList<Integer>();
		List<Integer> obj14Idx = new ArrayList<Integer>();
		List<Integer> obj15Idx = new ArrayList<Integer>();
		// 对于地图数据，如果绘制的第一列为奇数，则大地砖不会显示，此处将绘制区域向左移，保证大地砖和动态地图/光线等正确绘制
		int left = tws - EXTEND_LEFT;
		if(left < 0)
			left = 0;
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// 绘制左上角x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// 绘制左上角y
				int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
				if (mti.isHasBng()) {
					Texture tex = Textures.getTextureFromCache("Tiles", mti.getBngImgIdx());
					if(tex == null) {
						tileIdx.add((int) mti.getBngImgIdx());
					} else {
						mapTex.blendNormal(tex, new Point(cpx, cpy), 1);
					}
				}
				if (mti.isHasMid()) {
					Texture tex = Textures.getTextureFromCache("SmTiles", mti.getBngImgIdx());
					if(tex == null) {
						smTileIdx.add((int) mti.getBngImgIdx());
					} else {
						mapTex.blendNormal(tex, new Point(cpx, cpy), 1);
					}
				}
			}
		}
		// 绘制完地砖后再绘制对象层
		// TODO 将动态地图绘制提出到最上层精灵
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// 绘制左上角x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// 绘制左上角y
				int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
				if (mti.isHasAni()) {
					int frame = mti.getAniFrame();
					int ati = (ctx.getTickFrames() - 1) / (ctx.getMaxFps() / frame);
					if(ati < 0) ati = 0;
					if(ati >= frame) ati = frame - 1;
					String objFileName = "Objects";
					if(mti.getObjFileIdx() != 0)
						objFileName += mti.getObjFileIdx();
					Texture t = Textures.getTextureFromCache(objFileName, mti.getObjImgIdx() + ati);
					if(t == null) {
						switch(mti.getObjFileIdx()) {
						case 0:
							obj0Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 2:
							obj2Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 3:
							obj3Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 4:
							obj4Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 5:
							obj5Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 6:
							obj6Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 7:
							obj7Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 8:
							obj8Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 9:
							obj9Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 10:
							obj10Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 11:
							obj11Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 12:
							obj12Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 13:
							obj13Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 14:
							obj14Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 15:
							obj15Idx.add(mti.getObjImgIdx() + ati);
							break;
						}
					} else {
						mapTex.blendAdd(t, new Point(cpx + t.getOffsetX(), cpy - t.getHeight() + t.getOffsetY()), 1);
					}
				} else if (mti.isHasObj()) {
					String objFileName = "Objects";
					if(mti.getObjFileIdx() != 0)
						objFileName += mti.getObjFileIdx();
					Texture t = Textures.getTextureFromCache(objFileName, mti.getObjImgIdx());
					if(t == null) {
						switch(mti.getObjFileIdx()) {
						case 0:
							obj0Idx.add((int) mti.getObjImgIdx());
							break;
						case 2:
							obj2Idx.add((int) mti.getObjImgIdx());
							break;
						case 3:
							obj3Idx.add((int) mti.getObjImgIdx());
							break;
						case 4:
							obj4Idx.add((int) mti.getObjImgIdx());
							break;
						case 5:
							obj5Idx.add((int) mti.getObjImgIdx());
							break;
						case 6:
							obj6Idx.add((int) mti.getObjImgIdx());
							break;
						case 7:
							obj7Idx.add((int) mti.getObjImgIdx());
							break;
						case 8:
							obj8Idx.add((int) mti.getObjImgIdx());
							break;
						case 9:
							obj9Idx.add((int) mti.getObjImgIdx());
							break;
						case 10:
							obj10Idx.add((int) mti.getObjImgIdx());
							break;
						case 11:
							obj11Idx.add((int) mti.getObjImgIdx());
							break;
						case 12:
							obj12Idx.add((int) mti.getObjImgIdx());
							break;
						case 13:
							obj13Idx.add((int) mti.getObjImgIdx());
							break;
						case 14:
							obj14Idx.add((int) mti.getObjImgIdx());
							break;
						case 15:
							obj15Idx.add((int) mti.getObjImgIdx());
							break;
						}
					} else {
						mapTex.blendAdd(t, new Point(cpx, cpy - t.getHeight()), 1);
					}
				}
			}
		}
		Textures.loadTextureAsync("Tiles", tileIdx);
		Textures.loadTextureAsync("SmTiles", smTileIdx);
		Textures.loadTextureAsync("Objects", obj0Idx);
		Textures.loadTextureAsync("Objects2", obj2Idx);
		Textures.loadTextureAsync("Objects3", obj3Idx);
		Textures.loadTextureAsync("Objects4", obj4Idx);
		Textures.loadTextureAsync("Objects5", obj5Idx);
		Textures.loadTextureAsync("Objects6", obj6Idx);
		Textures.loadTextureAsync("Objects7", obj7Idx);
		Textures.loadTextureAsync("Objects8", obj8Idx);
		Textures.loadTextureAsync("Objects9", obj9Idx);
		Textures.loadTextureAsync("Objects10", obj10Idx);
		Textures.loadTextureAsync("Objects11", obj11Idx);
		Textures.loadTextureAsync("Objects12", obj12Idx);
		Textures.loadTextureAsync("Objects13", obj13Idx);
		Textures.loadTextureAsync("Objects14", obj14Idx);
		Textures.loadTextureAsync("Objects15", obj15Idx);
		
		/*try {
			if(tileIdx.isEmpty() &&
					smTileIdx.isEmpty() &&
					obj0Idx.isEmpty() &&
					obj2Idx.isEmpty() &&
					obj3Idx.isEmpty())
			javax.imageio.ImageIO.write(mapTex.toBufferedImage(false), "jpg", new java.io.File("C:\\Users\\云\\Desktop\\1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//return true;
		return tileIdx.isEmpty() &&
				smTileIdx.isEmpty() &&
				obj0Idx.isEmpty() &&
				obj2Idx.isEmpty() &&
				obj3Idx.isEmpty() &&
				obj4Idx.isEmpty() &&
				obj5Idx.isEmpty() &&
				obj6Idx.isEmpty() &&
				obj7Idx.isEmpty() &&
				obj8Idx.isEmpty() &&
				obj9Idx.isEmpty() &&
				obj10Idx.isEmpty() &&
				obj11Idx.isEmpty() &&
				obj12Idx.isEmpty() &&
				obj13Idx.isEmpty() &&
				obj14Idx.isEmpty() &&
				obj15Idx.isEmpty();
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
		return mapTex;
	}

}
