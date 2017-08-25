package com.login.interceptor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 某个请求是否需要被拦截
 * @author Administrator
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interception {
	
	/**
	 * 是否需要被拦截
	 * @return
	 */
	boolean isInterception() default true;
	/**
	 * 拦截地址    	可多个  ;分割      url1;url2;
	 * @return
	 */
	String url() default "";
	/**
	 * 需要拦截的名字
	 * @return
	 */
	String interceptionName() default "";
	
	/**
	 * 是否读取Controller的注解  1：不读取       2: url需要使用正则表达式匹配    ， 其他所有值均为读取
	 * 可对应拦截地址     1;0;    
	 * 如地址为多个   是否读取标志为单个 则默认全部为此方式       
	 * 如地址为3个   是否读取地址标志为2个，除对应的值外，默认读取最后一个   
	 * @return
	 */
	String isReadController() default "";
	
	/**
	 * 是否为api接口     
	 * true：是    false：否
	 * 如不填，默认false
	 * @return
	 */
	boolean isApi() default false;
	
	/**
	 * 返回的json数据
	 * 当此拦截地址为 api时  所需返回的数据      
	 * 如不填写，则默认取配置文件的路径
	 * @return
	 */
	String responseStr() default "";
	
	/**
	 * 返回的路径地址
	 * 当此拦截地址不为 api时  所需返回的路径 
	 * 如不填写，则默认取配置文件的路径      
	 * @return
	 */
	String responseUrl() default "";
}

