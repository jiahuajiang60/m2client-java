package org.jootnet.m2client.map.internal;

/**
 * 热血传奇2地图
 * <br>
 * 即地图文件(*.map)到Java中数据结构的描述
 * <br>
 * 即一个MapHeader和一个MapTile二维数组
 * <br>
 * 但实际上不使用MapHeader和MapTile，因为MapHeader和MapTile数据太散，不便于使用
 * <br>
 * 而是将MapHeader中关键地图信息提取出来放到Map里，将MapTile重新解析为{@link MapTileInfo}以方便程序逻辑
 * 
 * @author johness
 */
public final class MapInfo {
	
	/** 地图宽度 */
	private short width;
	/** 地图高度 */
	private short height;
	/** 地图块数据 */
	private MapTileInfo[][] tiles;
	
	MapInfo() { }
	
	/** 获取地图宽度 */
	public short getWidth() {
		return width;
	}
	/** 设置地图宽度 */
	void setWidth(short width) {
		this.width = width;
	}
	/** 获取地图高度 */
	public short getHeight() {
		return height;
	}
	/** 设置地图高度 */
	void setHeight(short height) {
		this.height = height;
	}
	/** 获取地图块信息 */
	public MapTileInfo[][] getTiles() {
		return tiles;
	}
	/** 设置地图块信息 */
	void setMapTiles(MapTileInfo[][] mapTiles) {
		this.tiles = mapTiles;
	}
}
