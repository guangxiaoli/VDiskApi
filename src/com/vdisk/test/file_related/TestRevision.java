package com.vdisk.test.file_related;
import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestRevision extends VdiskAPITest{
	@Parameters({"uid","revision_uri","local_file_path","temp_file_path"})
	@BeforeClass
	public void init (String uid,String revision_uri,String local_file_path,String temp_file_path){
		setUid(uid);
		setUri(revision_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
//		vDirector = "/2013_11_20_18_52_53";
		//生成指定的文件revision
		genRevs(temp_file_path,11);
	}
	/*************************逻辑验证**************************/
	@Parameters({"temp_file_path"})
	@Test
	public void testRevision_rev_limit_默认值(String param){
		String file_path = vDirector + "/" + fileops.fList.get(0).getName();
		//验证返回内容,文件rev的数量=第一次上传rev+times
		String result = fileops.getFileRev(file_path, uid, 10+"");
//		System.out.println(result);
		JSONArray arr = new JSONArray().fromObject(result);
//		System.out.println(arr.size());
//		fileops.delete(file_path, uid);
		Assert.assertEquals(arr.size(), 10, "testRevision_rev_limit_验证返回revision数量失败");
	}
	@Test
	public void testRevision_rev_limit_指定值(){
		int times = 11;
		String result = fileops.getFileRev(vDirector+"/"+fileops.fList.get(0).getName(), uid, times+"");
		JSONArray arr = new JSONArray().fromObject(result);
		Assert.assertEquals(arr.size(), times, "testRevision_rev_limit_验证返回revision数量失败");
	}

	@Test
	public void testRevision_rev_limit_文件夹路径(){
		String result = fileops.getFileRev(vDirector, uid, 15+"");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		
		Assert.assertEquals(errID, "40604","testRevision_rev_limit_验证文件夹路径失败");
	}
	@Test
	public void testRevision_rev_limit_ParentPath不存在(){
		String times = "15";
		String notExistPath = vDirector + CommonUtils.getExpireTime(0) + "/" + fileops.fList.get(0).getName();
		String result = fileops.getFileRev(notExistPath, uid, 15+"");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40402","testRevision_rev_limit_验证ParentPath失败");
	}
	@Test
	public void testRevision_rev_limit_错误路径(){
		String res = fileops.createFolder(CommonUtils.getExpireTime(0), uid);
		String filePath = fileops.jsonDecode(res, "path") + fileops.fList.get(0).getName();
		String result = fileops.getFileRev(filePath, uid, 15+"");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40403","testRevision_rev_limit_验证错误路径失败");
	}
	/*************************参数验证**************************/
	@Test
	public void testRevision_rev_limit_小于默认值(){
		int times = 0;
		String result = fileops.getFileRev(vDirector+"/"+fileops.fList.get(0).getName(), uid, times+"");
//		System.out.println(result);
//		JSONArray arr = new JSONArray().fromObject(result);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001", "rev_limit_验证返回revision数量失败");
	}
	@Parameters("en_space")
	@Test
	public void testRevision_rev_limit_半角空格(String param){
		param = CommonUtils.urlEncode(param);
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
//		System.out.println(result);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_半角空格验证失败");
	}
//
	@Parameters("empty_str")
	@Test
	public void testPutFile_rev_limit_空字符串(String param){
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_空字符串验证失败");
//		System.out.println(result);
	}
	@Parameters("decimal")
	@Test
	public void testPutFile_rev_limit_小数(String param){
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_小数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testPutFile_rev_limit_负数(String param){
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_负数验证失败");
	}	

	@Parameters("chinese")
	@Test
	public void testPutFile_rev_limit_汉字(String param){
		param = CommonUtils.urlEncode(param);
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_汉字验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testPutFile_rev_limit_特殊字符(String param){
		param = CommonUtils.urlEncode(param);
		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_limit_特殊字符验证失败");
	}
//	@Parameters("letter")返回40404
//	@Test
//	public void testPutFile_rev_limit_字母(String param){
//		String result = fileops.getFileRev(vDirector + "/" + fileops.fList.get(0).getName(), uid, param);
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40404","rev_limit_字母验证失败");
//	}
	@AfterClass
	private void cleanup(){
		fileops.delete(vDirector, uid);
	}
	private void genRevs(String local_path,int times){
		File file = fileops.fList.get(0);
		//指定文件夹上传文件
		String res = fileops.upLoadFile(file, vDirector, uid, paramsMap);
		String file_path = fileops.jsonDecode(res, "path");
		//生成多个文件的revision
		String srcPath = file_path;
		String destPath = local_path + "/" + file.getName();
		fileops.generateFileRevision(srcPath, destPath, times, uid);
	}
}
