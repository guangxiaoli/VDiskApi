package com.vdisk.test.file_related;

import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;



/*
 * delta接口/2/delta/sandbox
 * @param cursor
 * @param cursor_str
 * */
public class TestDelta extends VdiskAPITest{
	private String paramName;
	
	public TestDelta(String paramName) {
		super();
		this.paramName = paramName;
	}
	
//	public TestDelta(){}
	
	@BeforeClass
	@Parameters({"uid","delta_uri"})
	public void init(String uid,String delta_uri){
		setUid(uid);
		setUri(delta_uri);
	}
	
	/*************************逻辑验证**************************/
	@Test
	public void testDelta_cursor(){
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		vdiskRequest.getVdiskResult().getBody();
		String info = getRequestInfo("push_url");
		Assert.assertFalse(info.equals(""));;
	}
	@Parameters("ingteger")
	@Test
	public void testDelta_cursor_整数(@Optional("10") String param){
		paramsMap.put("cursor", param);
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		vdiskRequest.getVdiskResult().getBody();
		String info = getRequestInfo("push_url");
		Assert.assertFalse(info.equals(""));;
	}	
	@Parameters("zero")
	@Test
	public void testDelta_cursor_零(@Optional("0") String param){
		paramsMap.put("cursor", param);
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		vdiskRequest.getVdiskResult().getBody();
		String info = getRequestInfo("push_url");
		Assert.assertFalse(info.equals(""));;
	}	
	/*************************cursor参数验证**************************/
	@Parameters("decimal")
	@Test
	public void testDelta_cursor_小数(String param){
		paramsMap.put("cursor", param);
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	@Parameters("negative")
	@Test
	public void testDelta_cursor_负数(String param){
		paramsMap.put("cursor", param);
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	@Parameters("chinese")
	@Test
	public void testDelta_cursor_汉字(String param){
		paramsMap.put("cursor", CommonUtils.urlEncode(param));
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	@Parameters("letter")
	@Test
	public void testDelta_cursor_字母(String param){
		paramsMap.put("cursor", param);
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	@Parameters("spec_str")
	@Test
	public void testDelta_cursor_特殊字符(String param){
		paramsMap.put("cursor", CommonUtils.urlEncode(param));
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	@Parameters("empty_str")
	@Test
	public void testDelta_cursor_空字符串(String param){
		paramsMap.put("cursor", CommonUtils.urlEncode(param));
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}		
	@Parameters("en_space")
	@Test
	public void testDelta_cursor_半角空格(String param){
		paramsMap.put("cursor", CommonUtils.urlEncode(param));
		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001");
	}
	
	/*************************cursor_str参数验证**************************/
//	@Parameters("decimal")
//	@Test
//	public void testDelta_cursor_str_小数(String param){
//		paramsMap.put("cursor_str", CommonUtils.urlEncode(param));
//		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
//		String body = vdiskRequest.getVdiskResult().getBody();
//		String errID = vdiskRequest.jsonEncodeErrMsg(VdiskAPITest.ERR_KEY,body);
//		Assert.assertEquals(errID, "40001");
//	}
//	@Parameters("negative")
//	@Test
//	public void testDelta_cursor_str_负数(String param){
//		paramsMap.put("cursor_str", CommonUtils.urlEncode(param));
//		vdiskRequest.doGet(uri, paramsMap, headersMap, uid);
//		String body = vdiskRequest.getVdiskResult().getBody();
//		String errID = vdiskRequest.jsonEncodeErrMsg(VdiskAPITest.ERR_KEY,body);
//		Assert.assertEquals(errID, "40001");
//	}	
	//根据参数名称来创建测试接口对象
//	@Factory
//	public Object[] factory(){
//		return new Object[] { 
//				new TestDelta("cursor"),
//				new TestDelta("cursor_str")
//		};
//	}
	@AfterMethod
	public void cleanParams(){
		paramsMap.clear();
	}
	
	private String getRequestInfo(String key){
		String body = vdiskRequest.getVdiskResult().getBody();
		JSONObject json = new JSONObject().fromObject(body);
		return json.get(key).toString();
	}
}
