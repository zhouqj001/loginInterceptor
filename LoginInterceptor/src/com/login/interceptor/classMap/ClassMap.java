package com.login.interceptor.classMap;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.login.interceptor.annotation.Interception;
import com.login.interceptor.core.Constants;
import com.login.interceptor.core.InterceptionObj;


/**
 * 存放需要注解的类
 * @author Administrator
 *
 */
public class ClassMap {
	
	private static List<InterceptionObj> INTERCEPTION_MAP_INFO = new ArrayList<InterceptionObj>();
	
	private static String basePath;
	
	public static Map<String, String> areInterception(HttpServletRequest request) throws Exception{
		
		if(StringUtils.isBlank(basePath)){
			String path = request.getContextPath();
			basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + path;
			if(basePath.contains(":80/")){
				basePath = basePath.replace(":80/", "/");
			}
		}
		
		String url = request.getRequestURL().toString();
		if(url.contains(":80/")){
			url = url.replace(":80/", "/");
		}
		String actionPath = url.replace(basePath, "");
		if(basePath.contains("http://localhost")){
			basePath = basePath.replace("http://localhost", "http://127.0.0.1");
			actionPath = actionPath.replace(basePath, "");
		}
		if(actionPath.indexOf("/") != 0){
			actionPath = "/" + actionPath;
		}
		if(actionPath.indexOf("?") > 0 && actionPath.indexOf("?") == actionPath.lastIndexOf("?")){
			actionPath = actionPath.substring(0, actionPath.lastIndexOf("?"));
		}
		List<InterceptionObj> interceptionObjPattern = new ArrayList<InterceptionObj>();
		InterceptionObj interceptionObj = null;
		com.login.interceptor.core.Interception returnInterception = null;
		for (InterceptionObj obj : ClassMap.INTERCEPTION_MAP_INFO) {
			Map<String, com.login.interceptor.core.Interception> map = obj.getInterceptorMapInfo();
			for (String key : map.keySet()) {
				if(key.contains("Pattern")){
					interceptionObjPattern.add(obj);
					break;
				}
			}
			
			com.login.interceptor.core.Interception interception = obj.getInterceptionMapInfoObj(actionPath);
			if(null != interception && interception.getIsInterception()){
				interceptionObj = obj;
				returnInterception = interception; 
			}
		}
		
		for (InterceptionObj obj : interceptionObjPattern) {
			if(null != obj){
				Map<String, com.login.interceptor.core.Interception> map = obj.getInterceptorMapInfo();
				for (String key : map.keySet()) {
					if(null != map.get(key) && map.get(key).getIsInterception()){
						Pattern pattern = Pattern.compile(key);
						Matcher matcher = pattern.matcher(actionPath);
						if(matcher.matches()){
							interceptionObj = obj;
							returnInterception = map.get(key);
						}
					}
				}
			}
		}
		Map<String, String> resultMap = null;
		if(null != interceptionObj && null != returnInterception){
			if(returnInterception.getIsApi()){
				if(null != interceptionObj.getInterceptorName() && !"".equals(interceptionObj.getInterceptorName())){
					if(null == request.getSession().getAttribute(interceptionObj.getInterceptorName())){
						resultMap = new HashMap<String, String>();
						String responseStr = returnInterception.getResponseStr();
						String encode = interceptionObj.getResponseEncode();
						resultMap.put(Constants.RETURN_TYPE_STR, responseStr);
						resultMap.put(Constants.RETURN_STR_ENCODE, encode);
					}
				}else{
					throw new ClassMapException("interceptorName is null");
				}
			}else{
				if(null != interceptionObj.getInterceptorName() && !"".equals(interceptionObj.getInterceptorName())){
					if(null == request.getSession().getAttribute(interceptionObj.getInterceptorName())){
						resultMap = new HashMap<String, String>();
						String returnUrl = returnInterception.getReturnUrl();
						resultMap.put(Constants.RETURN_TYPE_URL, returnUrl);
						request.getSession().setAttribute(Constants.REDIRECT_URL, actionPath);
					}
				}else{
					throw new ClassMapException("interceptorName is null");
				}
			}
		}
		return resultMap;
	}
	
	public static void init(){
		core();
	}
	
	/**
	 * 核心方法
	 */
	private static void core(){
		InputStream in = ClassMap.class.getClassLoader().getResourceAsStream(Constants.XML_NAME);
		Element element = null;
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		String componentScanText = null;
		//String interceptorText = null;
		String interceptorNameText = null;
		String returnUrlText = null;
		String returnStrText = null;
		String returnStrEncodeText = null;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(in);
			element = dt.getDocumentElement();
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node1 = childNodes.item(i);
				if(Constants.COMPONENT_SCAN.equals(node1.getNodeName())){
					try {
						componentScanText = node1.getTextContent().trim();
					} catch (Exception e) {
						throw new ClassMapException("componentScanText is null");
					}
					if(StringUtils.isBlank(componentScanText)){
						throw new ClassMapException("componentScanText is null");
					}
				}
				/*
				else if (Constants.INTERCEPTOR.equals(node1.getNodeName())) {
					try {
						interceptorText = node1.getTextContent().trim();
					} catch (Exception e) {
						throw new ClassMapException("interceptorText is null");
					}
					if(StringUtils.isBlank(interceptorText)){
						throw new ClassMapException("interceptorText is null");
					}
				}
				*/
				else if (Constants.INTERCEPTOR_NAME.equals(node1.getNodeName())) {
					try {
						interceptorNameText = node1.getTextContent().trim();
					} catch (Exception e) {
						throw new ClassMapException("interceptorNameText is null");
					}
					if(StringUtils.isBlank(interceptorNameText)){
						throw new ClassMapException("interceptorNameText is null");
					}
				}else if (Constants.RETURN_URL.equals(node1.getNodeName())) {
					try {
						returnUrlText = node1.getTextContent().trim();
					} catch (Exception e) {
						throw new ClassMapException("returnUrlText is null");
					}
					if(StringUtils.isBlank(returnUrlText)){
						throw new ClassMapException("returnUrlText is null");
					}
				}else if (Constants.RETURN_STR.equals(node1.getNodeName())) {
					String returnStr = node1.getTextContent();
					returnStrText = (null != returnStr && !"".equals(returnStr)) ? returnStr : "";
				}else if (Constants.RETURN_STR_ENCODE.equals(node1.getNodeName())) {
					String returnStrEncode = node1.getTextContent();
					returnStrEncodeText = (null != returnStrEncode && "".equals(returnStrEncode)) ? returnStrEncode : "UTF-8";
				}
			}
			
			String[] componentScanTexts = componentScanText.split(",");
			//String[] interceptorTexts = interceptorText.split(",");
			String[] interceptorNameTexts = interceptorNameText.split(",");
			
			List<InterceptionObj> interceptionObjs = getInterceptionObj(componentScanTexts, null, interceptorNameTexts, returnStrEncodeText);
			
			for (InterceptionObj obj : interceptionObjs) {
				String splashPath = obj.getComponentScan().replaceAll("\\.", "/").trim();
				ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
				URL url = loader.getResource(splashPath);
				String filePath = getRootPath(url);
				if(isJarFile(filePath)){
					try {
						throw new ClassMapException("is jar file");
					} catch (ClassMapException e) {
						e.printStackTrace();
					}
				}else{
					List<String> classNameList = readFromDirectory(filePath);
					if(null != classNameList && classNameList.size() > 0){
						obj.setInterceptorMapInfo(setInterception(splashPath, classNameList, obj.getInterceptorName(), returnUrlText, returnStrText));
						ClassMap.INTERCEPTION_MAP_INFO.add(obj);
					}else{
						throw new ClassMapException("directory's children is empty");
					}
				}
			}
			
			/*
			String splashPath = componentScanText.replaceAll("\\.", "/").trim();
			URL url = ClassMap.class.getClassLoader().getResource(splashPath);
			String filePath = getRootPath(url);
			
			if(isJarFile(filePath)){
				try {
					throw new ClassMapException("is jar file");
				} catch (ClassMapException e) {
					e.printStackTrace();
				}
			}else{
				List<String> classNameList = readFromDirectory(filePath);
				if(null != classNameList && classNameList.size() > 0){
					
				}else{
					throw new ClassMapException("directory's children is empty");
				}
			}
			*/
			System.out.println("映射加载完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 组装InterceptionObj对象
	 * @param componentScanTexts
	 * @param interceptorTexts
	 * @param interceptorNameTexts
	 * @param returnUrlTexts
	 * @return
	 */
	private static List<InterceptionObj> getInterceptionObj(
				String[] componentScanTexts, String[] interceptorTexts,
				String[] interceptorNameTexts, String returnStrEncodeText
			){
		
		Integer componentScanTextsLen = componentScanTexts.length;
		//Integer interceptorTextsLen = interceptorTexts.length;
		Integer interceptorTextsLen = 0;
		Integer interceptorNameTextsLen = interceptorNameTexts.length;
		
		Integer eachCount = 0;
		
		Integer first = Math.max(componentScanTextsLen, interceptorTextsLen);
		Integer second = Math.max(first, interceptorNameTextsLen);
		eachCount = second;
		List<InterceptionObj> resultList = new ArrayList<InterceptionObj>();
		for (int i = 0; i < eachCount; i++) {
			InterceptionObj obj = new InterceptionObj();
			if(i >= componentScanTextsLen - 1){
				obj.setComponentScan(componentScanTexts[componentScanTextsLen - 1].trim());
			}else{
				obj.setComponentScan(componentScanTexts[i].trim());
			}
			/*
			if(i >= interceptorTextsLen - 1){
				obj.setInterceptor(interceptorTexts[interceptorTextsLen - 1].trim());
			}else{
				obj.setInterceptor(interceptorTexts[i].trim());
			}
			*/
			if(i >= interceptorNameTextsLen - 1){
				obj.setInterceptorName(interceptorNameTexts[interceptorNameTextsLen - 1].trim());
			}else{
				obj.setInterceptorName(interceptorNameTexts[i].trim());
			}
			obj.setResponseEncode(returnStrEncodeText.trim());
			resultList.add(obj);
		}
		return resultList;
	}
	
	/**
	 * 解析类中的方法是否有注解
	 * @param componentScanInfo
	 * @param classList
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, com.login.interceptor.core.Interception> setInterception(String componentScanInfo, List<String> classList, String interceptionName, String returnUrl, String returnStr) throws ClassNotFoundException{
		Map<String, com.login.interceptor.core.Interception> resultMap = new HashMap<String, com.login.interceptor.core.Interception>();
		for (String s : classList) {
			if(StringUtils.isNotBlank(s)){
				Class clazz = Class.forName(componentScanInfo.trim().replaceAll("/", "\\.") + "." + s.trim().replace(".class", ""));
				boolean hasAnnotation = clazz.isAnnotationPresent(RequestMapping.class);
				String requestMappingValue = "";
				if(hasAnnotation){
					RequestMapping requestMappingInfo = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
					String[] requestMappingValues = requestMappingInfo.value();
					if(null != requestMappingValues && requestMappingValues.length > 0){
						requestMappingValue = requestMappingValues[0];
					}
				}
				Method[] methods = clazz.getMethods();
				if(null != methods && methods.length > 0){
					for (Method m : methods) {
						com.login.interceptor.core.Interception interceptionInfo = new com.login.interceptor.core.Interception();
						if(null != m){
							hasAnnotation = m.isAnnotationPresent(Interception.class);
							if(hasAnnotation){
								Interception interception = m.getAnnotation(Interception.class);
								String methodInterceptorName = interception.interceptionName();
								if(interceptionName.equals(methodInterceptorName)){
									String urlName = interception.url();
									boolean booleanFlag = interception.isInterception();
									String isReadController = interception.isReadController();
									interceptionInfo.setIsApi(interception.isApi());
									interceptionInfo.setReturnUrl(returnUrl);
									if(StringUtils.isNotBlank(interception.responseStr())){
										interceptionInfo.setResponseStr(interception.responseStr());
									}
									interceptionInfo.setIsInterception(booleanFlag);
									
									if(StringUtils.isBlank(urlName)){
										urlName = m.getName();
									}
									if(StringUtils.isNotBlank(requestMappingValue) && requestMappingValue.lastIndexOf("/") == requestMappingValue.length() - 1){
										requestMappingValue = requestMappingValue.substring(0, requestMappingValue.lastIndexOf("/"));
									}
									if(urlName.indexOf("/") != 0){
										urlName = "/" + urlName;
									}
									if(null == isReadController || "".equals(isReadController.trim())){
										resultMap.put(requestMappingValue + urlName, interceptionInfo);
									}else{
										String[] urlNames = urlName.split(";");
										String[] isReadControllers = isReadController.split(";");
										if(null != urlNames && urlNames.length > 0){
											for (int i = 0; i < urlNames.length; i++) {
												if(i > isReadControllers.length - 1){
													if("1".equals(isReadControllers[isReadControllers.length])){
														resultMap.put(urlNames[i], interceptionInfo);
													}else if("2".equals(isReadControllers[isReadControllers.length])){
														resultMap.put(urlNames[i], interceptionInfo);
														resultMap.put(urlNames[i] + "Pattern", interceptionInfo);
													}else{
														resultMap.put(requestMappingValue + urlNames[i], interceptionInfo);
													}
												}else{
													if("1".equals(isReadControllers[i])){
														resultMap.put(urlNames[i], interceptionInfo);
													}else{
														resultMap.put(requestMappingValue + urlNames[i], interceptionInfo);
													}
												}
											}
										}else{
											if("1".equals(isReadControllers[0])){
												resultMap.put(urlName, interceptionInfo);
											}else if("2".equals(isReadControllers[isReadControllers.length])){
												resultMap.put(urlName, interceptionInfo);
												resultMap.put(urlName + "Pattern", interceptionInfo);
											}else{
												resultMap.put(requestMappingValue + urlName, interceptionInfo);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return resultMap;
	}
	
	
	
	/**
	 * 返回文件地址
	 * @param url
	 * @return
	 */
	private static String getRootPath(URL url) {
		String fileUrl = url.getFile();
		int pos = fileUrl.indexOf('!');
		
		if (-1 == pos) {
		    return fileUrl;
		}
		
		return fileUrl.substring(5, pos);
	}
	
	/**
	 * 判断是否为Jar文件
	 * @param name
	 * @return
	 */
	private static boolean isJarFile(String name){
		return name.endsWith(".jar");
	}
	
	/**
	 * 判断是否为class文件
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean isClassFile(String name){
		return name.endsWith(".class");
	}
	
	/**
	 * 获取文件夹下面所有的class文件
	 * @param path
	 * @return
	 */
	private static List<String> readFromDirectory(String path) {
		File file = new File(path);
		String[] names = file.list();
		if (null == names) {
			return null;
		}
		return Arrays.asList(names);
	}
	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		System.out.println(startTime);
		System.out.println("---------------------------------------------------------");
		for(int i = 0; i < 50000; i++){
			Pattern pattern = Pattern.compile("/users/([a-zA-Z0-9]+).html");
			Matcher matcher = pattern.matcher("/users/asdfafasf156497.html");
			System.out.println(matcher.matches());
		}
		System.out.println("---------------------------------------------------------");
		long endTime = System.currentTimeMillis();
		System.out.println(endTime);
		System.out.println(endTime - startTime);
	}
}
