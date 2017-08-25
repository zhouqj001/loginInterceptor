package com.login.interceptor.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.interceptor.classMap.ClassMap;
import com.login.interceptor.core.Constants;


public class CoreFilter implements Filter{
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			if (!(request instanceof HttpServletRequest)) {
				chain.doFilter(request, response);
				return;
			}
			
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			String contextPath = httpRequest.getContextPath();
			Map<String, String> resultMap = ClassMap.areInterception(httpRequest);
			if(null != resultMap){
				if(null != resultMap.get(Constants.RETURN_TYPE_URL) && !"".equals(resultMap.get(Constants.RETURN_TYPE_URL).trim())){
					String returnUrl = resultMap.get(Constants.RETURN_TYPE_URL).trim();
					if(returnUrl.indexOf("/") != 0){
						returnUrl = "/" + returnUrl;
					}
					httpResponse.sendRedirect(contextPath + returnUrl);
				}else if(null != resultMap.get(Constants.RETURN_TYPE_STR) && !"".equals(resultMap.get(Constants.RETURN_TYPE_STR).trim())){
					String returnStr = resultMap.get(Constants.RETURN_TYPE_STR).trim();
					String returnStrEncode = resultMap.get(Constants.RETURN_STR_ENCODE).trim();
					httpResponse.setCharacterEncoding(returnStrEncode);
					httpResponse.setContentType("text/html;charset="+returnStrEncode);
					PrintWriter writer = httpResponse.getWriter();
					writer.write(returnStr);
					writer.close();
				}else{
					chain.doFilter(httpRequest, httpResponse);
				}
			}else{
				chain.doFilter(httpRequest, httpResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		ClassMap.init();
	}

}
