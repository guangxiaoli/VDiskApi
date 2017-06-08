package com.vdisk.test.user;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestAccountLogin extends VdiskAPITest{
	@Parameters({"uid","account_login_uri"})
	@BeforeClass
	public void inti(String uid,String account_login_uri){
		setUid(uid);
		setUri(account_login_uri);	
	}
	
	
	//有起始时间  截止时间  且时间跨度不超过 7天 ，验证用户登录，
	@Test(description="正常时间段验证登陆")
	public void testLogin_Normal() throws ParseException{
		long engTime =getSystime();//获得系统时间作为截止时间
		long startTime=engTime-3600*24*3;
		paramsMap.put("time_begin", Long.toString(startTime));
		paramsMap.put("time_end",Long.toString(engTime));
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		long loginTime=Long.parseLong(fileops.jsonDecode(str, "login"));
		Assert.assertTrue(startTime<=loginTime&&loginTime<=engTime, "验证用户登陆失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
		
	
	//截止时间为空， 验证用户登录
	@Test(description="有起始时间，截止时间为空。")
	public void testLogin_EndtimeNull()throws ParseException{
		long beginTime=getSystime()-3600*24*3;
		paramsMap.put("time_begin", Long.toString(beginTime));
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		long loginTime=Long.parseLong(fileops.jsonDecode(str, "login"));
		Assert.assertTrue(beginTime<=loginTime,"验证用户登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	
	
	//起始时间为空 ，验证用户登录
	@Test(description="有截止时间、起始时间为空，")
	public  void testLogin_StarttimeNull() throws ParseException{
		long  endTimes=getSystime();
		paramsMap.put("time_end", Long.toString(endTimes));
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		long loginTime=Long.parseLong(fileops.jsonDecode(str, "login"));
		Assert.assertTrue(loginTime<=endTimes,"验证用户登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));	
	}
	
	
	//起始时间， 截止时间都为空 验证用户登录
	@Test(description="起始时间， 截止时间都为空")
	public void testLogin_Null() throws ParseException{
		long nowTime=getSystime();
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		long loginTime=Long.parseLong(fileops.jsonDecode(str, "login"));
		Assert.assertTrue(loginTime<nowTime, "验证用户登陆失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}


	//时间段超过7天   系统时间减去 10天， 时间跨度大于7
	@Test(description="时间段大于7天 ")
	public void testTimeOut() throws ParseException{
		long endTime =getSystime();
		long startTime=endTime-3600*24*10;
		paramsMap.put("time_begin", Long.toString(startTime));
		paramsMap.put("time_end", Long.toString(endTime));
		String str = vdiskRequest.getVdiskResult().getBody();
		long loginTime=Long.parseLong(fileops.jsonDecode(str, "login"));
		Assert.assertTrue(startTime<=loginTime&&loginTime<=endTime, "验证用户登陆失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	
	
	
	
	//获取当前时间 并转为时间戳
	public  long  getSystime() throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tempTime=df.format(new Date());
		Date date = df.parse(tempTime);
		long temp=date.getTime()/1000L;
		return temp;
	}

}
