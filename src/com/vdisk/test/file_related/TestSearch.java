package com.vdisk.test.file_related;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestSearch extends VdiskAPITest{
	@Parameters({"uid","search_uri","local_file_path","temp_file_path"})
	@BeforeClass
	public void init (String uid,String search_uri,String local_file_path,String temp_file_path){
		setUid(uid);
		setUri(search_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
//		System.out.println(vDirector);
	}
	@BeforeMethod
	public void cleanParams(){
		paramsMap.clear();
	}
	@Test(description="指定查找完全匹配关键值的文件")
	public void testSearch_file(){
		//按照层级创建目录
		String path = fileops.genPathLevel(vDirector, 1, uid);
		//上传文件并rename
		File file = fileops.fList.get(0);
		fileops.upLoadFile(file, path, uid, paramsMap);
		paramsMap.put("root", "basic");
		paramsMap.put("from_path", path + "/" + file.getName());
		paramsMap.put("to_path", path + "/" + CommonUtils.urlEncode("测试vdisk.txt"));
		fileops.jsonDecode(fileops.rename(paramsMap,uid), "path");
		//根目录按关键字查询文件
		String keyword = "测试vdisk.txt";
		paramsMap.put("query", CommonUtils.urlEncode(keyword));
		String result = fileops.search(vDirector,paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		for (int i = 0; i < arr.size(); i++) {
			String fpath = arr.getJSONObject(i).getString("path");
			Assert.assertTrue(fpath.contains(keyword), "testSearch_file查询文件失败");
		}
	}
	@Test(description="指定查找完全匹配关键值的文件夹")
	public void testSearch_folder(){
		//按照层级创建目录
		String folder = fileops.genPathLevel(vDirector, 1, uid);
		//根目录按关键字查询文件
		String keyword = CommonUtils.getExpireTime(0);
		paramsMap.put("query", keyword);
		String result = fileops.search(vDirector,paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		for (int i = 0; i < arr.size(); i++) {
			String fpath = arr.getJSONObject(i).getString("path");
//			System.out.println(fpath);
			Assert.assertTrue(fpath.contains(keyword), "testSearch_folder查询文件失败");
		}
	}
	@Test(description="不存在的文件")
	public void testSearch_notExistFile(){
		String keyword = "abc123k.txt";
//		String keyword = "linkcommon.txt";
		paramsMap.put("query", keyword);
		String result = fileops.search("/",paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		Assert.assertTrue(arr.size()==0, "testSearch_notExistFile查询不存在文件失败");
//		Assert.assertTrue(fpath.contains(keyword), "testSearch_folder查询文件失败");
	}
	@Test(description="不存在的文件夹")
	public void testSearch_notExistFolder(){
		String keyword = "abc123k";
		paramsMap.put("query", keyword);
		String result = fileops.search("/",paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		Assert.assertTrue(arr.size()==0, "testSearch_notExistFolder查询不存在文件夹失败");
	}
	@Test(description="模糊匹配文件、文件夹")
	public void testSearch_matchFileAndFolder(){
		String file_suffix = ".txt";
		//按照层级创建目录
		String folder = fileops.genPathLevel(vDirector, 1, uid);
		//上传文件并rename
		File file = fileops.fList.get(0);
		fileops.upLoadFile(file, folder, uid, paramsMap);
		paramsMap.put("root", "basic");
		paramsMap.put("from_path", folder + "/" + file.getName());
		String filename = CommonUtils.getExpireTime(0) + file_suffix;
		paramsMap.put("to_path", folder + "/" + filename);
		fileops.rename(paramsMap,uid);
		paramsMap.clear();
		//搜索
		String keyword = CommonUtils.getExpireTime(0).substring(0,3);
		paramsMap.put("query", keyword);
		String result = fileops.search("/", paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		boolean flag = false;
		for (int i = 0; i < arr.size(); i++) {
			String path = arr.getJSONObject(i).getString("path");
			String f_name= path.substring(path.lastIndexOf("/") + 1,path.length());
			if(path.contains(filename) && f_name.contains(filename)){
				flag = true;
				break;
			}
		}
		Assert.assertTrue(flag,"testSearch_matchFileAndFolder未匹配到文件、文件夹失败");
	}
	@Test(description="查询包含删除文件")
	public void testSearch_includeDel(){
		String folder = fileops.genPathLevel(vDirector, 2, uid);
		File file = fileops.fList.get(0);
		String path = fileops.jsonDecode(fileops.upLoadFile(file, folder, uid, paramsMap),"path");
		String filename = path.substring(path.lastIndexOf("/") + 1,path.length());
		paramsMap.put("path", path);
		paramsMap.put("root", "basic");
		fileops.logicToDelete(paramsMap, uid);
		paramsMap.clear();
		paramsMap.put("include_deleted", "true");
		paramsMap.put("query", filename);
		String result = fileops.search(vDirector, paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(result);
		boolean is_del = Boolean.parseBoolean(arr.getJSONObject(0).getString("is_deleted"));
		Assert.assertTrue(is_del, "testSearch_includeDel");
	}
	@Test(description="path不存在_err_code:40403")
	public void testSearch_errCode_40403(){
		File file = fileops.fList.get(0);
		String path = fileops.jsonDecode(fileops.upLoadFile(file, vDirector, uid, paramsMap),"path");
		String filename = path.substring(path.lastIndexOf("/") + 1,path.length());
		fileops.delete(path, uid);
		paramsMap.put("query", filename);
		String result = fileops.search(path, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40403", "testSearch_errCode_40403验证错误码失败");
	}
	@Test(description="path为文件路径_err_code:40605")
	public void testSearch_errCode_40605(){
		File file = fileops.fList.get(0);
		String path = fileops.jsonDecode(fileops.upLoadFile(file, vDirector, uid, paramsMap),"path");
		String filename = path.substring(path.lastIndexOf("/") + 1,path.length());
		paramsMap.put("query", filename);
		String result = fileops.search(path, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40605", "testSearch_errCode_40605验证错误码失败");		
	}
	@Test(description="query必填项验证")
	public void testSearch_required(){
		String result = fileops.search(vDirector, null, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","testSearch_required验证query必填项失败");
	}
	/*************************参数验证**************************/
	/****query参数验证****/
	@Parameters("empty_str")
	@Test
	public void testSearch_query_empty(String param){
		paramsMap.put("query", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","query_空字符串验证失败");
	}
	@Test(description="query长度大于200")
	public void testSearch_query_moreThan200(){
		String param = CommonUtils.getRandStr(201);
		paramsMap.put("query", param);
		String result = fileops.search(vDirector, paramsMap, uid);
//		System.out.println(result);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","query_moreThan200_空字符串验证失败");
	}
	/****file_limit参数验证****/
	@Parameters("empty_str")
	@Test
	public void testSearch_file_limit_empty(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_empty_空字符串验证失败");
	}
	@Parameters("decimal")
	@Test
	public void testSearch_file_limit_decimal(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_decimal验证失败");
	}
	@Parameters("negative")
	@Test
	public void testSearch_file_limit_negative(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_negative验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testSearch_file_limit_chinese(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_chinese验证失败");
	}
	@Parameters("letter")
	@Test
	public void testSearch_file_limit_letter(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_letter验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testSearch_file_limit_spec_str(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_spec_str验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testSearch_file_limit_en_space(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("file_limit", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","file_limit_en_space验证失败");
	}
	/****include_deleted参数验证****/
	@Parameters("empty_str")
	@Test
	public void testSearch_include_deleted_empty(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_empty_空字符串验证失败");
	}
	@Parameters("decimal")
	@Test
	public void testSearch_include_deleted_decimal(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_decimal验证失败");
	}
	@Parameters("negative")
	@Test
	public void testSearch_include_deleted_negative(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_negative验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testSearch_include_deleted_chinese(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_chinese验证失败");
	}
	@Parameters("letter")
	@Test
	public void testSearch_include_deleted_letter(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_letter验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testSearch_include_deleted_spec_str(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_spec_str验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testSearch_include_deleted_en_space(String param){
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", CommonUtils.urlEncode(param));
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_deleted_en_space验证失败");
	}
	@Test(description="1或0验证boolean值")
	public void testSearch_include_delete_OneOrZero(){
		String param = "1";
		paramsMap.put("query", fileops.fList.get(0).getName());
		paramsMap.put("include_deleted", param);
		String result = fileops.search(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_delete_OneOrZero验证失败");
	}
	@AfterClass
	public void cleanup(){
		fileops.delete(vDirector, uid);
	}
}
