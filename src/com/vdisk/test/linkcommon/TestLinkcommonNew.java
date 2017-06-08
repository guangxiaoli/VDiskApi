package com.vdisk.test.linkcommon;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestLinkcommonNew extends VdiskAPITest{
	
	@Parameters({"uid","linkcommon_new_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String linkcommon_new_uri,String local_file_path){
		setUid(uid);
		setUri(linkcommon_new_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
		
	}

	@Test(description="创建提取码分享")
	public  void testTestLinkcommonNew(){
		String FilePath=upShareFile();
		paramsMap.put("path", FilePath);
		String body=Linkcommon.getCode(paramsMap,FilePath, uid);
		String resPath ="/"+fileops.jsonDecode(body, "path");
		Assert.assertEquals(resPath,FilePath,"创建提取码失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));

		
	}
	
	//上传文件并获得	上传文件的路径
	private  String  upShareFile(){
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile(file, vDirector, uid, paramsMap);
		String filePath = fileops.jsonDecode(body, "path");
		return filePath;
	}
	@AfterClass	
	public void cleanup(){
		fileops.delete(vDirector, uid);
	}
	
}