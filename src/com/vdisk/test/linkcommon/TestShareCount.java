package com.vdisk.test.linkcommon;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.entity.VdiskRequest;
import com.vdisk.util.CommonUtils;

	public class TestShareCount extends VdiskAPITest{
	
	@Parameters({"uid","linkcommon_shareCount_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String linkcommon_shareCount_uri,String local_file_path){
		setUid(uid);
		setUri(linkcommon_shareCount_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
		

	}
	@Test(description="用户的分享计数接口content_id 必选")
	public void testShareCount_id(){
		//取得content_id
		String res=shareGet();
		System.out.println(res);
		String content_id=fileops.jsonDecode(res, "content_id");
		paramsMap.put("sina_uid", uid);
		paramsMap.put("content_id", content_id);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String info=vdiskRequest.getVdiskResult().getBody();
		String Ares=fileops.jsonDecode(info, "success");
		Assert.assertEquals(Ares, "true","用户分享计数接口登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="用户的分享计数接口count_browse: 可选")
	public void testShareCount_browse(){
		//取得 	count_browse 和 count_id
		String res=shareGet();
		String content_id=fileops.jsonDecode(res, "content_id");
		String count_browse=fileops.jsonDecode(res, "count_browse");
//		paramsMap.put("vuid", uid);
		paramsMap.put("content_id", content_id);
		paramsMap.put("count_browse", count_browse);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String info=vdiskRequest.getVdiskResult().getBody();
		String Ares=fileops.jsonDecode(info, "success");
		Assert.assertEquals(Ares, "true","用户分享计数接口登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="用户的分享计数接口count_download 可选")
	public void testShareCount_download(){
		//取得 	count_download 和 count_id
		String res=shareGet();
		String content_id=fileops.jsonDecode(res, "content_id");
		String count_download=fileops.jsonDecode(res, "count_download");
//		paramsMap.put("vuid", uid);
		paramsMap.put("content_id", content_id);
		paramsMap.put("count_download", count_download);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String info=vdiskRequest.getVdiskResult().getBody();
		String Ares=fileops.jsonDecode(info, "success");
		Assert.assertEquals(Ares, "true","用户分享计数接口登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}

	@Test(description="用户的分享计数接口count_copy 可选")
	public void testShareCount_copy(){
		//取得 	count_copy 和 count_id
		String res=shareGet();
		String content_id=fileops.jsonDecode(res, "content_id");
		String count_copy=fileops.jsonDecode(res, "count_copy");
//		paramsMap.put("vuid", uid);
		paramsMap.put("content_id", content_id);
		paramsMap.put("count_copy", count_copy);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String info=vdiskRequest.getVdiskResult().getBody();
		String Ares=fileops.jsonDecode(info, "success");
		Assert.assertEquals(Ares, "true","用户分享计数接口登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	@Test(description="用户的分享计数接口count_like可选")
	public void testShareCount_like(){
		//取得 	count_like和 count_id
		String res=shareGet();
		String content_id=fileops.jsonDecode(res, "content_id");
		String count_like=fileops.jsonDecode(res, "count_like");
//		paramsMap.put("vuid", uid);
		paramsMap.put("content_id", content_id);
		paramsMap.put("count_copy", count_like);
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String info=vdiskRequest.getVdiskResult().getBody();
		String Ares=fileops.jsonDecode(info, "success");
		Assert.assertEquals(Ares, "true","用户分享计数接口登录失败"+CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	
	
	
	// 调用 share get 接口 取得content信息
	private String shareGet(){
		String uri="/2/share/get";
		String FilePath=upShareFile();
		//取得分享短链
		String shareInfo=fileops.shares(FilePath, "false", uid);
		String url=fileops.jsonDecode(shareInfo, "url");
		String shareID=url.substring(url.lastIndexOf("/")+1,url.length());
		//调用share get 接口 取得content信息
		paramsMap.put("copy_ref", shareID);
		vdiskRequest.doGet(uri, paramsMap, null, uid);
		String res=vdiskRequest.getVdiskResult().getBody();
		paramsMap.clear();
		return res;
	} 
	
	
	

	//上传文件并获得	上传文件的路径
	private  String  upShareFile(){
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile(file, vDirector, uid, paramsMap);
		String filePath = fileops.jsonDecode(body, "path");
		return filePath;
	}
	
}
	