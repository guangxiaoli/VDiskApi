package com.vdisk.test.file_related;

import java.io.File;
import java.util.Map;

import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestDownLoadFile extends VdiskAPITest{
//	private String vDirector;//创建vdisk文件夹
	
	@BeforeClass
	@Parameters({"uid","local_file_path","downLoadFile_uri"})
	public void init(String uid,String up_file_path,String downLoadFile_uri){
		setUid(uid);
		setUri(downLoadFile_uri);
//		fileOperate.addLocalFile(up_file_path);
		fileops.addLocalFile(up_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
	}
	
	/*************************逻辑验证**************************/
	
	//文件下载
	@Parameters({"download_file_path"})
	@Test
	public void fileDownLoad(String path){
		CommonUtils.cleanFile(path);
		for(int i=0;i<fileops.fList.size();i++){
//			File file = fileOperate.fList.get(i);
			File file = fileops.fList.get(i);
			//上传文件
			String content = fileops.upLoadFile(file, vDirector, uid,paramsMap);
			JSONObject json = new JSONObject().fromObject(content);
			setFile_md5(json.get("md5").toString());
			//下载文件
			String srcFilePath = vDirector + "/" + file.getName();
			String destFilePath = path + "/" + file.getName();
			File downLoadFile = fileops.dowLoadFileToLocal(srcFilePath, destFilePath, uid, paramsMap);
			if(downLoadFile != null){
				String file_md5 = CommonUtils.fileMd5(downLoadFile);
				Assert.assertEquals(file_md5, getFile_md5(),"上传、下载文件对比md5失败");
			}else{
//				Assert.assertEquals(destFilePath==null, true,"下载文件失败");
				Assert.assertFalse(destFilePath!=null,srcFilePath + "下载失败");
			}
			
		}
	}
	
	@Parameters("error_rev")//提交错误的版本号则返回当前版本号
	@Test
	public void testDownLoadFile_rev_错误版本号(@Optional("1A2B3C")String param){
		paramsMap.put("rev", param);
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40404","rev=" + param);
	}
	
	@Parameters("folder_path")//提交文件夹路径
	@Test
	public void testDownLoadFile_path_文件夹路径(@Optional("/")String param){
		String body = dowLoadFileParamsVerified(param);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40604",param);	
	}
	
	@Parameters("error_path")//提交不存在的文件路径
	@Test
	public void testDownLoadFile_path_错误的文件路径(@Optional("test")String param){
		String filepath = "/" + param + "/" + fileops.fList.get(0).getName();
//		System.out.println("------" + filepath);
		String body = dowLoadFileParamsVerified(param);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40403",filepath);
	}
	@Parameters("error_filename")//提交不存在的文件名
	@Test
	public void testDownLoadFile_path_不存在的文件名(@Optional("DEzNzc0ODgxMjA12345.txt")String param){
		String filepath = "/" + param;
		String body = dowLoadFileParamsVerified(param);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40403",filepath);
	}


	/*************************参数验证**************************/
	
	@Parameters("decimal")
	@Test
	public void testDownLoadFile_rev_小数(String param){
		paramsMap.put("rev", param);
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}
	
	@Parameters("negative")
	@Test
	public void testDownLoadFile_rev_负数(String param){
		paramsMap.put("rev", param);
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}
	
	@Parameters("chinese")
	@Test
	public void testDownLoadFile_rev_汉字(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}
	
	@Parameters("letter")
	@Test
	public void testDownLoadFile_rev_字母(String param){
		paramsMap.put("rev", param);
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40404","rev=" + param);
	}	
	
	@Parameters("spec_str")
	@Test
	public void testDownLoadFile_rev_特殊字符(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}	
	
	@Parameters("empty_str")
	@Test
	public void testDownLoadFile_rev_空字符串(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}
	
	@Parameters("en_space")
	@Test
	public void testDownLoadFile_rev_半角空格(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String filepath = "/" + fileops.fList.get(0).getName();
		String body = dowLoadFileParamsVerified(filepath);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev=" + param);
	}
	
	@AfterMethod
	public void cleanParams(){
		paramsMap.clear();
	}
	
	@AfterClass
	public void cleanResource(){
		fileops.delete(vDirector, uid);
	}
	
    //下载文件参数格式验证
	/**
	 * @param 请求参数
	 * @param 用户uid
	 * */
    public String dowLoadFileParamsVerified(String filePath){
//    	setUri(uri + vDirector + filePath);
    	String test_uri = uri + vDirector + filePath;
//    	System.out.println(test_uri);
		vdiskRequest.doGet(test_uri, paramsMap, headersMap, uid);
		return vdiskRequest.getVdiskResult().getBody();
    }

//	public String createFolder(){
//		long timeStamp = System.currentTimeMillis();
//		String folder = CommonUtils.timeStampCovertDate(timeStamp);
//		return folderOperate.createFolder(folder, uid);
//	}
	
}
