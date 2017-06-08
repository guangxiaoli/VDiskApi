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
/*
 * verify 必选，字符串，用于校验。由API工程师提供.测试先停止
 */
public class TestAccountAddspace extends VdiskAPITest{
	@Parameters({"uid","account_addspace_uri"})
	@BeforeClass
	public void inti(String uid,String account_login_uri){
		setUid(uid);
		setUri(account_login_uri);	
	}
	
	

	
}
