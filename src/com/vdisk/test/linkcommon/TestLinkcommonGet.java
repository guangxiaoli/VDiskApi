package com.vdisk.test.linkcommon;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

	public class TestLinkcommonGet extends VdiskAPITest{
	
	@Parameters({"uid","linkcommon_get_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String linkcommon_get_uri,String local_file_path){
		setUid(uid);
		setUri(linkcommon_get_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));

	}

	@Test(description=" 获取提取码分享文件详情。")
	public  void testLinkcommonGet(){
		//取得上传文件的path ,md5
		String fileInfo=upShareFile();
		String FilePath=fileops.jsonDecode(fileInfo, "path");
		String md5=fileops.jsonDecode(fileInfo, "md5");
		//创建一个新的提取码
		String body=Linkcommon.getCode(paramsMap, FilePath, uid);
		String code=fileops.jsonDecode(body, "access_code");
		String copy_ref=fileops.jsonDecode(body, "copy_ref");	
		paramsMap.put("from_copy_ref",copy_ref);
		paramsMap.put("access_code",code);
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String res = vdiskRequest.getVdiskResult().getBody();
		String Vmd5=fileops.jsonDecode(res, "md5");
		Assert.assertEquals(Vmd5, md5,"提取码分享保存失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="文件缩略图存在的情况下")
	public void thumbExists(){
		String fileInfo=upShareFile();
		String FilePath=fileops.jsonDecode(fileInfo, "path");
		//创建一个新的提取码
		String body=Linkcommon.getCode(paramsMap, FilePath, uid);
		String code=fileops.jsonDecode(body, "access_code");
		String copy_ref=fileops.jsonDecode(body, "copy_ref");	
		paramsMap.put("from_copy_ref",copy_ref);
		paramsMap.put("access_code",code);
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String res = vdiskRequest.getVdiskResult().getBody();
		String thumb_exists=fileops.jsonDecode(res, "thumb_exists");
		String thumbnail=fileops.jsonDecode(res, "thumbnail");
		if (thumb_exists.equals("true")){
			Assert.assertTrue(thumbnail.length()>=1,"提取码分享保存失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
		}
	}
	@Test(description="缩略图不存在的情况下")
	public void thumbNoexists(){
		String fileInfo=upShareFile();
		String FilePath=fileops.jsonDecode(fileInfo, "path");
		//创建一个新的提取码
		String body=Linkcommon.getCode(paramsMap, FilePath, uid);
		String code=fileops.jsonDecode(body, "access_code");
		String copy_ref=fileops.jsonDecode(body, "copy_ref");	
		paramsMap.put("from_copy_ref",copy_ref);
		paramsMap.put("access_code",code);
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String res = vdiskRequest.getVdiskResult().getBody();
		String thumb_exists=fileops.jsonDecode(res, "thumb_exists");
		String thumbnail=fileops.jsonDecode(res, "thumbnail");
		if (thumb_exists.equals("false")){
			Assert.assertEquals(thumbnail, "","提取码分享保存失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
		}
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
	