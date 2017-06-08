package com.vdisk.test.linkcommon;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

	public class TestLinkcommonCpoy extends VdiskAPITest{
	
	@Parameters({"uid","linkcommon_cpoy_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String linkcommon_cpoy_uri,String local_file_path){
		setUid(uid);
		setUri(linkcommon_cpoy_uri);
		vDirector = createFolder(CommonUtils.getClassName(this));
		fileops.addLocalFile(local_file_path);
	}

	@Test(description="提取码分享保存到微盘。")
	public void testLinkcommonCpoy(){
		//取得上传文件的path 、md5、文件名
		String body=upShareFile();
		String FilePath= fileops.jsonDecode(body, "path");
		String md5=fileops.jsonDecode(body, "md5");
		String fileName=FilePath.substring(FilePath.lastIndexOf(".")+1,FilePath.length());
		//创建一个新的提取码
		String codeInfo=Linkcommon.getCode(paramsMap, FilePath, uid);
		String copy_ref =fileops.jsonDecode(codeInfo, "copy_ref");
		String access_code=fileops.jsonDecode(codeInfo, "access_code");
		
		paramsMap.put("paramsMap", "basic");
		paramsMap.put("from_copy_ref", copy_ref);
		paramsMap.put("access_code", access_code);
		paramsMap.put("to_path", vDirector+"/Copy"+fileName);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String res = vdiskRequest.getVdiskResult().getBody();
		//取得copy后的文件的md5
		String Vmd5=fileops.jsonDecode(res, "md5");
		Assert.assertEquals(Vmd5, md5,"提取码分享保存失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	
	
	
	
		
	//上传文件并获得	上传文件的信息
	private String  upShareFile(){
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile(file, vDirector, uid, paramsMap);
		return body;
	}

	@AfterClass
	public void cleanup(){
		fileops.delete(vDirector, uid);
	}
	
}