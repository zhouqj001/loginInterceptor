package com.login.interceptor.core;

public class Constants {
	
	/**
	 * 配置文件名
	 */
	public static final String XML_NAME = "loginInterceptor.xml";
	
	/**
	 * 所需扫描的包 节点
	 */
	public static final String COMPONENT_SCAN = "component-scan";
	
	/**
	 * 所需拦截的注解的类  interceptor1,interceptor2  如写多个则对应存在
	 */
	public static final String INTERCEPTOR = "interceptor";
	
	/**
	 * 所需拦截对象的名字   name1,name2
	 */
	public static final String INTERCEPTOR_NAME = "interceptor-name";
	
	/**
	 * 如对象不存在时返回地址   url1,url2 如写多个则对应所需拦截对象的名字
	 */
	public static final String RETURN_URL = "return-url";
	
	/**
	 * 对象不存在时并且此拦截地址为API接口时 返回数据 可为空
	 */
	public static final String RETURN_STR = "return-str";
	
	/**
	 * 返回数据的编码格式，如不填，默认为UTF-8 
	 */
	public static final String RETURN_STR_ENCODE = "return-str-encode";
	
	/**
	 * 返回类型为url
	 */
	public static final String RETURN_TYPE_URL = "url";
	
	/**
	 * 返回类型为字符串
	 */
	public static final String RETURN_TYPE_STR = "str";
	
	public static final String REDIRECT_URL = "redirect_url";
	
}
