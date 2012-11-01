package org.mannsverk.common.vo;

public class User {
	private String name;
	private String registered;
	private String avatarUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", registered=" + registered + "]";
	}
}
