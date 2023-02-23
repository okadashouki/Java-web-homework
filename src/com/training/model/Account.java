package com.training.model;

public class Account {
	// 身份證字號
	private String id;	
	// 帳戶名稱
	private String name;
	// 帳戶密碼
	private String pwd;
	
	
	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", pwd=" + pwd
				+ "]";
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}	
}
