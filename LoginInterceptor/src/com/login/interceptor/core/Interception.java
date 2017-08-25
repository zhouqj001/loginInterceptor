package com.login.interceptor.core;

public class Interception {
	
	private Boolean isInterception;//是否需要被拦截
	private String returnUrl;//返回地址
	private Boolean isApi;//是否为api接口     
	private String responseStr; //当此拦截地址为 api时  所需返回的数据
	
	
	public Boolean getIsInterception() {
		return isInterception;
	}
	public void setIsInterception(Boolean isInterception) {
		this.isInterception = isInterception;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public Boolean getIsApi() {
		return isApi;
	}
	public void setIsApi(Boolean isApi) {
		this.isApi = isApi;
	}
	public String getResponseStr() {
		return responseStr;
	}
	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}
	
}
