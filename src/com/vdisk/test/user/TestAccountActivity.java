package com.vdisk.test.user;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestAccountActivity extends VdiskAPITest{
	@Parameters({"uid","account_Activity_uri"})
	@BeforeClass
	public void inti(String uid,String account_login_uri){
		setUid(uid);
		setUri(account_login_uri);	
	}	
	
	@BeforeMethod
	public void clearParams(){
		paramsMap.clear();
		vdiskRequest.getReqsInfo().clear();
	}

	@Test(description="检测用户在1天内是否save有操作")
	public  void  testAccountActivity_save(){
		String info=accountInfo("save","1");
		int  day=getConsumed(info);
		Assert.assertTrue(day>=0,"检测失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	

	@Test(description="检测用户在3天内是否checkin有操作")
	public void testAccountActivity_checkin(){
		String info=accountInfo("checkin","3");
		int  day=getConsumed(info);
		Assert.assertTrue(day>=0,"检测失败"+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	@Test(description="检测的天数大于3天时 ")
	public void testAccountActivity_timeout(){
		String info=accountInfo("checkin","6");
		String errID = fileops.jsonDecode(info, ERR_KEY);
		Assert.assertEquals(errID, "40001","检测失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	
	
	//获取用户信息
	public String accountInfo(String act,String act_day){
		paramsMap.put("act", act);
		paramsMap.put("act_day", act_day);
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		return str;
	}
	
	//获取 天数
	private int  getConsumed(String str){
		String day = fileops.jsonDecode(str, "complete_day");
		return Integer.parseInt(day);
	}
	
	
}
