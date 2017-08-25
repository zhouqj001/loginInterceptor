package com.login.interceptor.core;

import java.util.HashMap;
import java.util.Map;

public class InterceptionObj {
	
	private String componentScan;//需要扫描的包名
	private String interceptor;//用到的拦截器注解类
	private String interceptorName;//需要拦截的模块名称
	private String responseEncode; // 返回数据的字符集编码
	
	private Map<String, Interception> interceptorMapInfo = new HashMap<String, Interception>();
	
	public String getComponentScan() {
		return this.componentScan;
	}
	public void setComponentScan(String componentScan) {
		this.componentScan = componentScan;
	}
	public String getInterceptor() {
		return this.interceptor;
	}
	public void setInterceptor(String interceptor) {
		this.interceptor = interceptor;
	}
	public String getInterceptorName() {
		return this.interceptorName;
	}
	public void setInterceptorName(String interceptorName) {
		this.interceptorName = interceptorName;
	}
	public Map<String, Interception> getInterceptorMapInfo() {
		return this.interceptorMapInfo;
	}
	public void setInterceptorMapInfo(Map<String, Interception> interceptorMapInfo) {
		this.interceptorMapInfo = interceptorMapInfo;
	}
	public Interception getInterceptionMapInfoObj(String key){
		return this.interceptorMapInfo.get(key);
	}
	public void putInterceptionMapInfoObj(String urlName, Interception obj){
		this.interceptorMapInfo.put(urlName, obj);
	}
	public String getResponseEncode() {
		return responseEncode;
	}
	public void setResponseEncode(String responseEncode) {
		this.responseEncode = responseEncode;
	}
	
}
