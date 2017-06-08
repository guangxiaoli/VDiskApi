package com.vdisk.test.linkcommon;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

	public class TestLinkcommonDelete extends VdiskAPITest{
	
	@Parameters({"uid","linkcommon_delete_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String linkcommon_delete_uri,String local_file_path){
		setUid(uid);
		setUri(linkcommon_delete_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));

	}

	@Test(description="删除提取码")
	public void testLinkcommonDelete(){
		String FilePath= upShareFile();
		String code =Linkcommon.getCode(paramsMap, FilePath, uid);
		String from_copy_ref=fileops.jsonDecode(code, "copy_ref");
		paramsMap.put("from_copy_ref", from_copy_ref);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String  body = vdiskRequest.getVdiskResult().getBody();
		String  res=fileops.jsonDecode(body, "success");
		Assert.assertEquals(res, "true","删除提取码分享失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
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