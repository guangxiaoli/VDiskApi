package com.vdisk.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

public class VdiskResult {
	private String body;//请求body
	private int retCode;//请求返回码
	private Map<String,String> respHeaders;//响应头
	private Map<String,String> respCookies;//响应cookie
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getRetCode() {
		return retCode;
	}
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	public String getRespHeaders(String key) {
		return respHeaders.get(key);
	}
	public void setRespHeaders(Header[] respHeaders) {
		this.respHeaders = new HashMap();
		for (Header header : respHeaders) {
			this.respHeaders.put(header.getName(), header.getValue());
		}
	}
	public Map<String, String> getRespCookies() {
		return respCookies;
	}
	public void setRespCookies(Map<String, String> repsCookies) {
		this.respCookies = repsCookies;
	}

	public String getRespHeader(String key) {
		return respHeaders.get(key);
	}
	public void setRespHeaders(String key,String value) {
		this.respHeaders.put(key, value);
	}

}
