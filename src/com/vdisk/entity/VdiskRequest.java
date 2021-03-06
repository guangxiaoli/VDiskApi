package com.vdisk.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.vdisk.util.CommonUtils;
import com.vdisk.util.HttpClientHelper;

public class VdiskRequest {
	public static final HttpClient CLIENT = HttpClientHelper.getHttpClient();
	public static final String VCUID = "?x-vdisk-cuid=";
	private VdiskResult vdiskResult;
	private String url;//请求url
	private Map<String,String> requestInfo;//保存请求url
	
	public VdiskRequest(){
		this.vdiskResult = new VdiskResult();
		this.requestInfo = new HashMap<String,String>();
	}
	
	public VdiskResult getVdiskResult() {
		return vdiskResult;
	}
	
	public void setVdiskResult(VdiskResult vdiskResult) {
		this.vdiskResult = vdiskResult;
	}
	
	public String getUrl(){
		return url;
	}
	
	public Map getReqsInfo(){
		return requestInfo;
	}
	/**
	 * GET Request
	 * @param uri
	 *  请求uri
	 * @param paramsMap
	 *  请求参数
	 * @param headersMap
	 *  请求头
	 * @param uid
	 *  用户uid
	 * */
	public void doGet(String uri,Map<String,String> paramsMap,Map<String,String> headersMap,String uid){
		Header[] allHeaders = null;
		HttpResponse response = null;
		String uriForSign = "";
		if(paramsMap != null && paramsMap.size() != 0){
			uriForSign = uri + VCUID + uid + CommonUtils.getRequestParams(paramsMap);
		}else{
			uriForSign = uri + VCUID + uid;
		}
		String expire = CommonUtils.getExpireTime(3600);
//		String url  = generateUrl(uriForSign, paramsMap, headersMap, "GET",expire);
		url  = generateUrl(uriForSign, paramsMap, headersMap, "GET",expire);
//		System.out.println(url);
		HttpGet httpGet = new HttpGet(url);
		if(headersMap != null && headersMap.size() != 0){
			allHeaders = CommonUtils.getRequestHeaders(headersMap);
			httpGet.setHeaders(allHeaders);
		}
		//设置get请求遇到302不自动重定向
		CLIENT.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		try {
			response = CLIENT.execute(httpGet);
			setReturnInfo(response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Post Request
	 * @param uri
	 *  请求uri
	 * @param paramsMap
	 *  请求参数
	 * @param headersMap
	 *  请求头
	 * @param uid
	 *  用户uid
	 * */
	public void doPost(String uri,Map<String,String> paramsMap,Map<String,String> headersMap,String uid){
		String uriForSign = uri + VCUID + uid;
		String expire = CommonUtils.getExpireTime(3600);
//		String url  = generateUrl(uriForSign, paramsMap, headersMap, "POST",expire);
		url  = generateUrl(uriForSign, paramsMap, headersMap, "POST",expire);
	//	System.out.println(url);
		HttpPost httpPost = new HttpPost(url);
		if(headersMap != null && headersMap.size() != 0){
			Header[] allHeaders = null;
			allHeaders = CommonUtils.getRequestHeaders(headersMap);
			httpPost.setHeaders(allHeaders);
		}
		if(paramsMap != null && paramsMap.size() != 0){
			//请求参数的实体
			HttpEntity paramEntity = null;
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> myEntry : paramsMap.entrySet()) {
				String key = myEntry.getKey();
				String value = myEntry.getValue();
				paramList.add(new BasicNameValuePair(key,value));
			}
			try {				
				//创建请求参数实体，并进行utf8编码
				paramEntity = new UrlEncodedFormEntity(paramList,HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPost.setEntity(paramEntity);
		}
		try {
			HttpResponse response = CLIENT.execute(httpPost);
			setReturnInfo(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Put Request
	 * @param uri
	 *  请求uri
	 * @param paramsMap
	 *  请求参数
	 * @param headersMap
	 *  请求头
	 * @param uid
	 *  用户uid
	 * */
	public void doPut(String uri,Map<String,String> paramsMap,Map<String,String> headersMap,String uid){
		String uriForSign = uri + VCUID + uid;
		String expire = CommonUtils.getExpireTime(3600);
//		String url  = generateUrl(uriForSign, paramsMap, headersMap, "POST",expire);
		url  = generateUrl(uriForSign, paramsMap, headersMap, "POST",expire);
		
		HttpPut httpPut = new HttpPut(url);
		if(headersMap != null && headersMap.size() != 0){
			Header[] allHeaders = null;
			allHeaders = CommonUtils.getRequestHeaders(headersMap);
			httpPut.setHeaders(allHeaders);
		}
		if(paramsMap != null && paramsMap.size() != 0){
			//请求参数的实体
			HttpEntity paramEntity = null;
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> myEntry : paramsMap.entrySet()) {
				String key = myEntry.getKey();
				String value = myEntry.getValue();
				paramList.add(new BasicNameValuePair(key,value));
			}
			try {				
				//创建请求参数实体，并进行utf8编码
				paramEntity = new UrlEncodedFormEntity(paramList,HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPut.setEntity(paramEntity);
		}
		try {
			HttpResponse response = CLIENT.execute(httpPut);
			setReturnInfo(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//设置返回信息
	public void setReturnInfo(HttpResponse response){
		HttpEntity entity = null;
		try {
			//返回响应对象的实体
			entity = response.getEntity();
			InputStream ins = null;
			if(entity != null){
				ins = entity.getContent();
				//set Body
				vdiskResult.setBody(CommonUtils.getBody(ins).toString());
				requestInfo.put(url, vdiskResult.getBody());
			}
			//set http 返回码
			vdiskResult.setRetCode(response.getStatusLine().getStatusCode());
			//set 响应头
			vdiskResult.setRespHeaders(response.getAllHeaders());
			//set cookies
			List<Cookie> list = ((AbstractHttpClient) CLIENT).getCookieStore().getCookies();
			
			vdiskResult.setRespCookies(CommonUtils.getCookies(list));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(entity != null){
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	//生成app_key、expire、ssign参数串
	public String generateCommonParam(String requestType,Map<String,String> x_vdisk_headers,String dest_uri,String expire){
		String sign = generateSignature(requestType,x_vdisk_headers,expire,dest_uri);
		String str = "";
		Map<String,String> param = new HashMap<String,String>();
		try {
			param.put("app_key", VdiskAPITest.APP_KEY);	
			param.put("expire", expire);
			param.put("ssig", URLEncoder.encode(sign,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();}		
		List<Map.Entry<String, ?>> list = CommonUtils.sortMapByKey(param);
		for (int i = 0; i < list.size(); i++) {
			str += list.get(i) + "&";
		}
		String commonParam = "&" + str.substring(0,str.length() - 1);
		return commonParam;
	}		
	//生成签名
	private String generateSignature(String requestType,Map<String,String> x_vdisk_headers,String expire,String dest_uri){
		String algo = "HmacSHA1";
		StringBuffer sBuffer = new StringBuffer();
		try {
			byte[] keyByte = VdiskAPITest.APP_SECRET.getBytes("utf-8");
			SecretKeySpec signingKey = new SecretKeySpec(keyByte, algo);
			Mac mac = Mac.getInstance(algo);
			mac.init(signingKey);
			
			sBuffer.append(requestType);
			sBuffer.append("\n\n");
			sBuffer.append(expire);
			sBuffer.append("\n");
			
			//排序x_vdisk_header
			if(x_vdisk_headers != null){
				Object[] headerObjArr = x_vdisk_headers.entrySet().toArray();
				Arrays.sort(headerObjArr);
				for (Object header : headerObjArr) {
					String str = header.toString();
					sBuffer.append(str.split("=")[0] + ":" + str.split("=")[1]);
					sBuffer.append("\n");
				}
//				if(headerObjArr.length == 0){
//					sBuffer.append("\n");
//				}
			}else{
				sBuffer.append("\n");
			}
			sBuffer.append(dest_uri);
			
			byte[] rawHmac = mac.doFinal(sBuffer.toString().getBytes());
			return (Base64.encode(rawHmac)).substring(5, 15);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
	//请求生成url
	public String generateUrl(String uri,Map<String,String> params,Map<String,String> headers,String requestType,String expire){
		String commonParam = generateCommonParam(requestType, headers, uri ,expire);
		String url = VdiskAPITest.HOST + uri + commonParam;
		return url;
	}
	
	public static void main(String[] args) {
		VdiskRequest reqs = new VdiskRequest();
		Map<String,String> map = new HashMap<String,String>();
		map.put("rev", "12345");
		reqs.doGet("/2/files", map, null, "1746090663");
		reqs.doPost("/2/files",map,null,"1746090663");
	}
}
