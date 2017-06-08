package com.vdisk.test.file_related;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestPutUpFile extends VdiskAPITest {
	
	@BeforeClass
	@Parameters({"uid","files_put_uri","local_file_path"})
	public void init(String uid,String files_put_uri,String up_file_path){
		setUid(uid);
		setUri(files_put_uri);
		fileops.addLocalFile(up_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
	}
	@BeforeClass
	@Test
	public void putUpFiles(){
		for (int i = 0; i < fileops.fList.size(); i++) {
			File file = fileops.fList.get(i);
			fileops.upLoadFile_put(file, vDirector, uid,paramsMap);
			String filePath = vDirector + "/" + file.getName();
			String content = fileops.getInfo(filePath, paramsMap, uid).get("body");
			JSONObject json = new JSONObject().fromObject(content);
			String upLoadedFileName = json.get("path").toString();	
			Assert.assertEquals(upLoadedFileName, filePath,"文件列表中未找到put上传的文件");
		}
	}
	/*************************逻辑验证**************************/
	@Test
	@AfterClass
	public void fastUpfileVerified(){
		boolean isFastUpFile = isFastUpFile();
		Assert.assertEquals(isFastUpFile, true,"文件未秒传");
	}
	@Test
	public void testPutFile_重复上传(){
		paramsMap.put("overwrite", "true");
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		JSONObject json = new JSONObject().fromObject(body);
		String arr[] = json.get("path").toString().split("/");
		String fileName = arr[arr.length - 1];
		Assert.assertEquals(fileName, file.getName(), "文件未被覆盖");
	}
	@Test
	public void testPutFile_重命名(){
		paramsMap.put("overwrite", "false");
		paramsMap.put("uid", uid);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		JSONObject json = new JSONObject().fromObject(body);
		String arr[] = json.get("path").toString().split("/");
		String fileName = arr[arr.length - 1];
		Assert.assertEquals(fileName.equals(file.getName()), false,"文件重命名失败");
	}
	@Parameters("outdated_rev")
	@Test
	public void testPutFile_非当前版本(@Optional("833988837") String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40616","非当前版本验证失败");
	}
	@Parameters("invalid_rev")
	@Test
	public void testPutFile_错误版本(@Optional("1a2b3c") String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40404","错误版本验证失败");
	}
	@Test
	public void testPutFile_文件夹路径(){
		String uriForSign = uri + vDirector + "?x-vdisk-cuid=" + uid + CommonUtils.getRequestParams(paramsMap);
		String commonParam = vdiskRequest.generateCommonParam("PUT",null,uriForSign,CommonUtils.getExpireTime(3600));
		String url = "http://upload-vdisk.sina.com.cn" + uriForSign + commonParam;
//		System.out.println(url);
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(1);
		HttpClient httpClient = new DefaultHttpClient(cm);
		HttpPut put = new HttpPut(url);
		PutFile putFile = new PutFile(put, httpClient);
		putFile.put_file();
//		System.out.println(putFile.body);
		String errID = fileops.jsonDecode(putFile.body, ERR_KEY);
		Assert.assertEquals(errID, "40315", "验证文件夹路径失败");
	}	
//	@Parameters("folder_path")
//	@Test
//	public void testPutFile_多线程(@Optional("/abc") String param){
//		String fileName = CommonUtils.urlEncode(fileops.fList.get(0).getName());
//		String uriForSign = uri + vDirector + "/" + fileName + "?x-vdisk-cuid=" + uid + CommonUtils.getRequestParams(paramsMap);
//		String commonParam = vdiskRequest.generateCommonParam("PUT",null,uriForSign,CommonUtils.getExpireTime(3600));
//		String url = "http://upload-vdisk.sina.com.cn" + uriForSign + commonParam;
//		System.out.println(url);
//		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
//		cm.setMaxTotal(1);
//		HttpClient httpClient = new DefaultHttpClient(cm);
//		HttpPut put = new HttpPut(url);
//		PutFile putFile = new PutFile(put, httpClient);
//		putFile.start();
//		fileops.deleteFolder(vDirector, uid);
//		
//		try {
//			putFile.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		httpClient.getConnectionManager().shutdown();
//	}
//	@Parameters("superSizeFile")
//	@Test
//	public void superSizeFileUpLoadVerified(String param){
//		File superSizeFile = new File(param);
//		String body = fileops.upLoadFile_put(superSizeFile, vDirector,uid,paramsMap);
//		String errID = fileops.jsonDecodeErrMsg(VdiskAPITest.ERR_KEY, body);
//		System.out.println(errID);
//	}

	/*************************参数验证**************************/
	
	/****overwrite参数验证****/
	@Parameters("empty_str")
	@Test
	public void testPutFile_overwrite_空字符串(String param){
		paramsMap.put("overwrite", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_空字符串验证失败");
	}
	@Parameters("digital")
	@Test
	public void testPutFile_overwrite_数字(@Optional("0")String param){
		paramsMap.put("overwrite", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_数字验证失败");
	}
	@Parameters("decimal")
	@Test
	public void testPutFile_overwrite_小数(String param){
		paramsMap.put("overwrite", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_小数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testPutFile_overwrite_负数(String param){
		paramsMap.put("overwrite", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_负数验证失败");
	}	
	@Parameters("letter")
	@Test
	public void testPutFile_overwrite_字母(String param){
		paramsMap.put("overwrite", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_字母验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testPutFile_overwrite_汉字(String param){
		paramsMap.put("overwrite", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_汉字验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testPutFile_overwrite_半角空格(String param){
		paramsMap.put("overwrite", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testPutFile_overwrite_特殊字符(String param){
		paramsMap.put("overwrite", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","overwrite_特殊字符验证失败");
	}
	/****parent_rev参数验证****/
	@Parameters("empty_str")
	@Test
	public void testPutFile_parent_rev_空字符串(String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_空字符串验证失败");
	}
	@Parameters("decimal")
	@Test
	public void testPutFile_parent_rev_小数(String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_小数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testPutFile_parent_rev_负数(String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_负数验证失败");
	}	
	@Parameters("letter")
	@Test
	public void testPutFile_parent_rev_字母(String param){
		paramsMap.put("parent_rev", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40404","parent_rev_字母验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testPutFile_parent_rev_汉字(String param){
		paramsMap.put("parent_rev", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_汉字验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testPutFile_parent_rev_半角空格(String param){
		paramsMap.put("overwrite", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testPutFile_parent_rev_特殊字符(String param){
		paramsMap.put("parent_rev", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_特殊字符验证失败");
	}
	/****size参数验证****/
	@Parameters("en_space")
	@Test
	public void testPutFile_size_半角空格(String param){
		paramsMap.put("overwrite", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","size_半角空格验证失败");
	}
	@Parameters("empty_str")
	@Test
	public void testPutFile_size_空字符串(String param){
		paramsMap.put("size", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		System.out.println(body);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","size_空字符串验证失败");
	}
	@Parameters("decimal")
	@Test
	public void testPutFile_size_小数(String param){
		paramsMap.put("size", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		System.out.println(body);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","size_小数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testPutFile_size_负数(String param){
		paramsMap.put("size", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","size_负数验证失败");
	}	
	@Parameters("letter")
	@Test
	public void testPutFile_size_字母(String param){
		paramsMap.put("size", param);
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40404","size_字母验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testPutFile_size_汉字(String param){
		paramsMap.put("size", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","size_汉字验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testPutFile_size_特殊字符(String param){
		paramsMap.put("size", CommonUtils.urlEncode(param));
		File file = fileops.fList.get(0);
		String body = fileops.upLoadFile_put(file, vDirector,uid,paramsMap);
		String errID = fileops.jsonDecode(body, ERR_KEY);
		Assert.assertEquals(errID, "40001","parent_rev_特殊字符验证失败");
	}
	@AfterMethod
	public void cleanParams(){
		paramsMap.clear();
	}
	@AfterSuite
	public void cleanUpFile(){
		fileops.delete(vDirector, uid);
	}
	//判断是否秒传文件
	public boolean isFastUpFile(){
		File file = fileops.fList.get(0);
		fileops.upLoadFile_put(file, vDirector, uid,paramsMap);
		int retCode = vdiskRequest.getVdiskResult().getRetCode();
		if(retCode != 100){
			return true;
		}
		return false;
	}
	
	class PutFile extends Thread{
		private final HttpPut httpPut;
		private final HttpContext context;
		private final HttpClient httpClient;
		private String body;
		private PutFile(HttpPut httpPut,HttpClient httpClient) {
			this.httpClient = httpClient;
			this.context = new BasicHttpContext();
			this.httpPut = httpPut;
		}
		
		private void put_file(){
			FileEntity fileEntity = new FileEntity(fileops.fList.get(0), ContentType.APPLICATION_OCTET_STREAM );
			httpPut.setEntity(fileEntity);
			HttpResponse response = null;
			HttpEntity entity = null;
			try {
				response = httpClient.execute(httpPut,context);
//				Thread.yield();
				entity = response.getEntity();
//				body = EntityUtils.toString(entity);
				byte[] b = EntityUtils.toByteArray(entity);
				body = new String(b);
//				System.out.println("已上传:" + new String(EntityUtils.toByteArray(entity)));
				EntityUtils.consume(entity);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void run(){
			put_file();
		}
	}
}
