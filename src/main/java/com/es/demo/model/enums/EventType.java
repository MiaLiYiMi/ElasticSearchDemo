package com.es.demo.model.enums;

public enum EventType {
	visit("浏览信息"),
	click("点击购买"),
	deal("完成付款");
	
	private String displayName;
	
	public String getDisplayName() {
		return displayName;
	}
	
	private EventType(String displayName) {
		this.displayName=displayName;
	}
}
