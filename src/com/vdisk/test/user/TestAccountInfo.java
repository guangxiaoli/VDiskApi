package com.vdisk.test.user;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestAccountInfo extends VdiskAPITest{
	@Parameters({"uid","account_info_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String account_info_uri,String local_file_path){
		setUid(uid);
		setUri(account_info_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
	}
	@Test(description="获取用户信息")
	public void  testAccountInfo(){
		String res = accountInfo();
		long pre_consumed = getConsumed(res);
//		System.out.println(pre_consumed);
		File file = fileops.fList.get(0);
		fileops.upLoadFile(file, vDirector, uid, paramsMap);
		String res1 = accountInfo();
		long cur_consumed = Long.valueOf(fileops.jsonDecode(fileops.jsonDecode(res1, "quota_info"),"consumed"));
//		System.out.println(cur_consumed);
		Assert.assertTrue((cur_consumed - pre_consumed) > 0, "testAccountInfo获取用户信息失败\n" 
				+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@AfterClass
	public void cleanup(){
		fileops.delete(vDirector, uid);
	}
	
	public String accountInfo(){
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String str = vdiskRequest.getVdiskResult().getBody();
		return str;
	}
	private long getConsumed(String str){
		String res = fileops.jsonDecode(str, "quota_info");
		String consumed = fileops.jsonDecode(res, "consumed");
		return Long.parseLong(consumed);
	}
}
