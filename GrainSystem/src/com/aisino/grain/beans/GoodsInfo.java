package com.aisino.grain.beans;

public class GoodsInfo {
	private String GoodsOwner = null;
	private int GoodsKind = 0;
	private double Price = 0;
	public String getGoodsOwner() {
		return GoodsOwner;
	}
	public void setGoodsOwner(String goodsOwner) {
		GoodsOwner = goodsOwner;
	}
	public int getGoodsKind() {
		return GoodsKind;
	}
	public void setGoodsKind(int goodsKind) {
		GoodsKind = goodsKind;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	
}
