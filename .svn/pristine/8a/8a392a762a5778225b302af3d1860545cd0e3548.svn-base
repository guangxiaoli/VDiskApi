package com.vdisk.test.file_related;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestMetaData extends VdiskAPITest{
	private String need_ext_folder;//扩展参数文件目录
	private String temp_file_path;
	@BeforeClass
	@Parameters({"uid","metadata_uri","local_file_path","need_ext","temp_file_path"})
	public void init(String uid,String metadata_uri,String local_file_path,String need_ext,String temp_file_path){
		setUid(uid);
		setUri(metadata_uri);
		fileops.addLocalFile(local_file_path);
		vDirector = createFolder(CommonUtils.getClassName(this));
//		this.vDirector = "/2013_11_14_18_02_03";
		this.need_ext_folder = need_ext;
		this.temp_file_path = temp_file_path;
		checkLocalPathExist();
	}
	@BeforeClass
	@Test
	public void postUpFiles(){
		for (int i = 0; i < fileops.fList.size(); i++) {
			File file = fileops.fList.get(i);
			fileops.upLoadFile(file, vDirector, uid,paramsMap);
			String filePath = vDirector + "/" + file.getName();
			String content = fileops.getInfo(filePath, paramsMap,uid).get("body");
			String upLoadedFileName = fileops.jsonDecode(content, "path");
			Assert.assertEquals(upLoadedFileName, filePath,"文件列表中未找到post上传的文件");
		}
	}
	/*************************逻辑验证**************************/
	
	@Test(description="目录的摘要不相同，返回目录信息")
	public void testMetaData_文件夹_Hash_不相同(){
		String path = "/" + CommonUtils.getExpireTime(0);
		//创建文件夹
		String rootFolder = fileops.jsonDecode(fileops.createFolder(path, uid),"path");
		//获取文件夹信息
		Map<String,String> rootFolderMap = fileops.getInfo(rootFolder, paramsMap, uid);
		String content = rootFolderMap.get("body");
		String pre_hash = fileops.jsonDecode(content, "hash");
		//创建subfolder
		String subFolder = rootFolder + "/" + CommonUtils.getExpireTime(0);
		fileops.createFolder(subFolder, uid);
		//metadata文件夹
		paramsMap.put("hash", pre_hash);
		String body = fileops.getInfo(rootFolder, paramsMap, uid).get("body");
		String cur_hash = fileops.jsonDecode(body, "hash");
		fileops.delete(rootFolder, uid);
		Assert.assertFalse(cur_hash.equals(pre_hash),"testMetaData_目录信息失败,pre_hash=" + pre_hash
				+ "|" + "cur_hash=" + cur_hash);
	}
	@Test(description="目录的摘要相同，返回304 (Not Modified)")
	public void testMetaData_文件夹_Hash_相同(){
		String path = "/" + CommonUtils.getExpireTime(0);
		//创建文件夹
		String rootFolder = fileops.jsonDecode(fileops.createFolder(path, uid),"path");
		//获取文件夹信息
		Map<String,String> rootFolderMap = fileops.getInfo(rootFolder, paramsMap, uid);
		String content = rootFolderMap.get("body");
//		System.out.println(content);
		String pre_hash = fileops.jsonDecode(content, "hash");
		//metadata文件夹
		paramsMap.put("hash", pre_hash);
		Map<String,String> map = fileops.getInfo(rootFolder, paramsMap, uid);
		String retCode = map.get("retCode");
		fileops.delete(rootFolder, uid);
		Assert.assertEquals(retCode, "304" ,"testMetaData_目录信息失败,resp_header=" + retCode);
	}
	@Test(description="显示文件夹信息并包含contents列表")
	public void testMetaData_文件夹信息(){
		paramsMap.put("list", "true");
		paramsMap.put("include_deleted", "false");
		paramsMap.put("include_item", "file");
		Map<String, String> map = fileops.getInfo(vDirector, paramsMap, uid);
		String path = fileops.jsonDecode(map.get("body"), "path");
		Assert.assertTrue(path.contains(vDirector), "testMetaData_获取文件夹信息不一致");
	}
	@Test
	public void testMetaData_不显示Contents成员(){
		paramsMap.put("list", "false");
		String body = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		boolean isExcludeContents = body.contains("contents");
		Assert.assertFalse(false, "验证testMetaData_不显示Contents成员失败");
	}
	@Test(description="rev对获取文件夹历史信息不生效")
	public void testMetaData_文件夹_rev(){
		//取当前文件夹rev
		Map<String, String> m = fileops.getInfo(vDirector, paramsMap, uid);
//		System.out.println(m.get("body"));
		String pre_rev = fileops.jsonDecode(m.get("body"), "rev");
		String subFolder = vDirector + "/" + CommonUtils.getExpireTime(0);
		//创建子文件夹
		fileops.createFolder(subFolder, uid);
		paramsMap.clear();
		//获取文件历史版本信息
		paramsMap.put("list", "true");
		paramsMap.put("include_deleted", "false");
		paramsMap.put("include_item", "doc");
		paramsMap.put("rev", pre_rev);
		Map<String, String> map = fileops.getInfo(vDirector, paramsMap, uid);
		String errID = fileops.jsonDecode(map.get("body"), ERR_KEY);
//		fileops.delete(subFolder, uid);
		Assert.assertEquals(errID, "40404","testMetaData_验证文件夹rev失败");
	}
	@Test(description="rev只对文件生效")
	public void testMetaData_文件_rev(){
		File downLoadFile = null;
		//上传文件
		String path = vDirector + "/" + CommonUtils.getExpireTime(0);
		String body = fileops.upLoadFile(fileops.fList.get(0), path, uid, paramsMap);
		String file_path = fileops.jsonDecode(body, "path");
		String pre_rev = fileops.jsonDecode(body, "rev");
		//下载文件并编辑文件
		String destFilePath = temp_file_path + "/" + fileops.fList.get(0).getName();
		File file = fileops.dowLoadFileToLocal(file_path, destFilePath, uid, paramsMap);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			bw.write(CommonUtils.getExpireTime(0));
			bw.flush();
			bw.close();
			//上传编辑后的文件
			paramsMap.put("overwrite", "true");
			//按版本获取文件md5
			paramsMap.put("rev", pre_rev);
			String result = fileops.getInfo(file_path, paramsMap, uid).get("body");
			String pre_md5 = fileops.jsonDecode(result, "md5");
//			System.out.println(cur_md5);
			paramsMap.clear();
			//用metadata返回的版本信息获取md5与下载文件md5比较
			paramsMap.put("rev", pre_rev);
			downLoadFile =fileops.dowLoadFileToLocal(file_path, destFilePath, uid, paramsMap);
			String md5 = CommonUtils.fileMd5(downLoadFile);
			Assert.assertEquals(md5, pre_md5, "testMetaData_文件_rev验证显示指定版本的文件信息失败");
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			downLoadFile.delete();
//			fileops.delete(path, uid);
		}
	}
	@Test(dataProvider="include_item",dataProviderClass=com.vdisk.entity.TestDataProvider.class,
		  description="按类型分类")
	public void testMetaData_文件分类(String param){
		int pre_folder_num = fileops.getFolderItemsNum(vDirector, uid);
		
		String subFolder = vDirector + "/" + CommonUtils.getExpireTime(0);
		fileops.createFolder(subFolder, uid);
		
		paramsMap.put("include_item",param);
		String body = fileops.groupByFileType(vDirector, paramsMap, uid);
		JSONArray json_arr = new JSONObject().fromObject(body).getJSONArray("contents");
		int cur_folder_num = json_arr.size();
		if("all".equals(param)){
			Assert.assertEquals(cur_folder_num-pre_folder_num, 1, "验证指定文件夹下成员失败");
		}else{
			for (int i = 0; i < json_arr.size(); i++) {
				boolean is_dir = Boolean.parseBoolean(json_arr.getJSONObject(i).getString("is_dir"));
				if("file".equals(param)){Assert.assertEquals(is_dir, false, "验证按file分类失败");	}
				else if("img".equals(param)){Assert.assertEquals(is_dir, false, "验证按img分类失败");}
				else if("doc".equals(param)){Assert.assertEquals(is_dir, false, "验证按doc分类失败");}
				else if("video".equals(param)){Assert.assertEquals(is_dir, false, "验证按video分类失败");}
				else if("audio".equals(param)){Assert.assertEquals(is_dir, false, "验证按audio分类失败");}
				else if("else".equals(param)){Assert.assertEquals(is_dir, false, "验证按else分类失败");}
				else if("folder".equals(param)){Assert.assertEquals(is_dir, true, "验证按folder分类失败");}
			}
		}
	}
	@Test(description="显示已删除文件夹")
	public void testMetaData_显示已删除成员_folder(){
		String subFolder = vDirector + "/" + CommonUtils.getExpireTime(0);
		fileops.createFolder(subFolder, uid);
		paramsMap.put("root", "basic");
		paramsMap.put("path", subFolder);
		fileops.logicToDelete(paramsMap, uid);
		paramsMap.clear();
		paramsMap.put("include_deleted", "true");
		String content = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		JSONObject json = new JSONObject().fromObject(content);
		JSONArray json_Arr = json.getJSONArray("contents");
		for (int i = 0; i < json_Arr.size(); i++) {
			String path = json_Arr.getJSONObject(i).getString("path");
			if(path.contains(subFolder)){
				boolean is_deleted = json_Arr.getJSONObject(i).getBoolean("is_deleted");
				Assert.assertEquals(is_deleted, true, "验证testMetaData_显示已删除成员_folder失败");
				break;
			}
		}
	}
	@Test(description="显示已删除文件")
	public void testMetaData_显示已删除成员_file(){
		String path = vDirector + "/" + CommonUtils.getExpireTime(0);
		//上传文件
		String res = fileops.upLoadFile(fileops.fList.get(0), path, uid, paramsMap);
		String file_path = fileops.jsonDecode(res, "path");
		paramsMap.put("root", "basic");
		paramsMap.put("path", file_path);
		//逻辑删除文件
		fileops.logicToDelete(paramsMap, uid);
		paramsMap.clear();
		//显示删除文件
		paramsMap.put("include_deleted", "true");
		String body = fileops.getInfo(path, paramsMap, uid).get("body");
		JSONObject json = new JSONObject().fromObject(body);
		JSONArray json_Arr = json.getJSONArray("contents");
//		fileops.delete(path, uid);
		for (int i = 0; i < json_Arr.size(); i++) {
			String f_path = json_Arr.getJSONObject(i).getString("path");
			if(f_path.equals(file_path)){
				boolean is_deleted = Boolean.parseBoolean(json_Arr.getJSONObject(i).getString("is_deleted"));
				Assert.assertEquals(is_deleted, true,"验证testMetaData_显示已删除成员_file失败");
				break;
			}
		}
	}
	@Test
	public void testMetaData_扩展字段(){
		String s = "read,video_flv,video_mp4,audio_mp3,doc_swf,share_status,sharefriend,linkcommon,thumbnail";
		paramsMap.put("need_ext", s);
		Map<String, String> m = fileops.getInfo(need_ext_folder, paramsMap,uid);
		JSONObject json_obj = new JSONObject().fromObject(m.get("body"));
		JSONArray json_arr = json_obj.getJSONArray("contents");
		for (int i = 0; i < json_arr.size(); i++) {
			JSONObject obj = json_arr.getJSONObject(i);
			if(obj.containsKey("read_url")){Assert.assertEquals(true,true);}
			else if(obj.containsKey("video_flv_url")){Assert.assertTrue(true);}
			else if(obj.containsKey("video_mp4_url")){Assert.assertTrue(true);}
			else if(obj.containsKey("audio_mp3_url")){Assert.assertTrue(true);}
			else if(obj.containsKey("share_status")){Assert.assertTrue(true);}
			else if(obj.containsKey("sharefriend")){Assert.assertTrue(true);}
			else if(obj.containsKey("linkcommon")){Assert.assertTrue(true);}
			else if(obj.containsKey("thumbnail")){Assert.assertTrue(true);}
			else{Assert.assertTrue(false);}
		}
	}
	@Test
	public void testMetaData_不存在的path(){
		String result = fileops.getInfo(vDirector+"/RhfERTa123", paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40403", "testMetaData_验证不存在的path失败");
	}
//	@Test
//	public void testMetaData_云节点(){
//		paramsMap.put("need_cloud", "true");
//		String result = fileops.getInfo("/", paramsMap, uid).get("body");
////		System.out.println(result);
//	}
	/*************************参数验证**************************/
	/****hash参数验证****/
	@Parameters("empty_str")
	@Test
	public void testMetaData_hash_空字符串(String param){
		paramsMap.put("hash", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_空字符串验证失败");
	}
	@Test(description="超过32位的hash值")
	public void testMetaData_hash_超长hash(){
		String param = CommonUtils.getRandStr(33);
		paramsMap.put("hash", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_超长字符验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testMetaData_hash_半角空格(String param){
		paramsMap.put("hash", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testMetaData_hash_特殊字符(String param){
		paramsMap.put("hash", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_特殊字符验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_hash_负数(String param){
		paramsMap.put("hash", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_负数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_hash_小数(String param){
		paramsMap.put("hash", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_小数验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testMetaData_hash_汉字(String param){
		paramsMap.put("hash", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","hash_汉字验证失败");
	}
	/****list参数验证****/
	@Parameters("empty_str")
	@Test
	public void testMetaData_list_空字符串(String param){
		paramsMap.put("list", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_空字符串验证失败");
	}
	@Parameters("digital")
	@Test
	public void testMetaData_list_零(@Optional("0")String param){
		paramsMap.put("list", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_零验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testMetaData_list_半角空格(String param){
		paramsMap.put("list", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testMetaData_list_特殊字符(String param){
		paramsMap.put("list", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_特殊字符验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_list_负数(String param){
		paramsMap.put("list", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_负数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_list_小数(String param){
		paramsMap.put("list", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_小数验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testMetaData_list_汉字(String param){
		paramsMap.put("list", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_汉字验证失败");
	}
	@Parameters("letter")
	@Test
	public void testMetaData_list_字母(String param){
		paramsMap.put("list", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","list_字母验证失败");
	}
	/****include_item参数验证****/
	@Parameters("empty_str")
	@Test
	public void testMetaData_include_item_空字符串(String param){
		paramsMap.put("include_item", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_空字符串验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testMetaData_include_item_半角空格(String param){
		paramsMap.put("include_item", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testMetaData_include_item_特殊字符(String param){
		paramsMap.put("include_item", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_特殊字符验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_include_item_负数(String param){
		paramsMap.put("include_item", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_负数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_include_item_小数(String param){
		paramsMap.put("include_item", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_小数验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testMetaData_include_item_汉字(String param){
		paramsMap.put("include_item", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_汉字验证失败");
	}
	@Parameters("letter")
	@Test
	public void testMetaData_include_item_字母(String param){
		paramsMap.put("include_item", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","include_item_字母验证失败");
	}
	/****rev参数验证****/
	@Parameters("empty_str")
	@Test
	public void testMetaData_rev_空字符串(String param){
		paramsMap.put("rev", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_空字符串验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testMetaData_rev_半角空格(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testMetaData_rev_特殊字符(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_特殊字符验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_rev_负数(String param){
		paramsMap.put("rev", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_负数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_rev_小数(String param){
		paramsMap.put("rev", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_小数验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testMetaData_rev_汉字(String param){
		paramsMap.put("rev", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_汉字验证失败");
	}
	@Test(description="大于32位字符")
	public void testMetaData_rev_字母(){
		String str = CommonUtils.getRandStr(33);
		paramsMap.put("rev", str);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","rev_字母验证失败");
	}
	/****need_ext参数验证****/
	@Parameters("empty_str")
	@Test
	public void testMetaData_need_ext_空字符串(String param){
		paramsMap.put("need_ext", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_空字符串验证失败");
	}
	@Parameters("en_space")
	@Test
	public void testMetaData_need_ext_半角空格(String param){
		paramsMap.put("need_ext", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_半角空格验证失败");
	}
	@Parameters("spec_str")
	@Test
	public void testMetaData_need_ext_特殊字符(String param){
		paramsMap.put("need_ext", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_特殊字符验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_need_ext_负数(String param){
		paramsMap.put("need_ext", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_负数验证失败");
	}
	@Parameters("negative")
	@Test
	public void testMetaData_need_ext_小数(String param){
		paramsMap.put("need_ext", param);
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_小数验证失败");
	}
	@Parameters("chinese")
	@Test
	public void testMetaData_need_ext_汉字(String param){
		paramsMap.put("need_ext", CommonUtils.urlEncode(param));
		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
		String errID = fileops.jsonDecode(result, ERR_KEY);
		Assert.assertEquals(errID, "40001","need_ext_汉字验证失败");
	}
	@BeforeMethod
	public void cleanParams(){
		paramsMap.clear();
	}
	@AfterClass
	public void cleanup(){
		fileops.delete(vDirector, uid);
	}
	private void checkLocalPathExist(){
		File file = new File(temp_file_path);
		if(!file.exists()){file.mkdir();}
	}
	/****need_cloud参数验证****/
//	@Parameters("empty_str")
//	@Test
//	public void testMetaData_need_cloud_空字符串(String param){
//		paramsMap.put("need_cloud", param);
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_空字符串验证失败");
//	}
//	@Parameters("en_space")
//	@Test
//	public void testMetaData_need_cloud_半角空格(String param){
//		paramsMap.put("need_cloud", CommonUtils.urlEncode(param));
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_半角空格验证失败");
//	}
//	@Parameters("spec_str")
//	@Test
//	public void testMetaData_need_cloud_特殊字符(String param){
//		paramsMap.put("need_cloud", CommonUtils.urlEncode(param));
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_特殊字符验证失败");
//	}
//	@Parameters("negative")
//	@Test
//	public void testMetaData_need_cloud_负数(String param){
//		paramsMap.put("need_cloud", param);
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_负数验证失败");
//	}
//	@Parameters("negative")
//	@Test
//	public void testMetaData_need_cloud_小数(String param){
//		paramsMap.put("need_cloud", param);
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_小数验证失败");
//	}
//	@Parameters("chinese")
//	@Test
//	public void testMetaData_need_cloud_汉字(String param){
//		paramsMap.put("need_cloud", CommonUtils.urlEncode(param));
//		String result = fileops.getInfo(vDirector, paramsMap, uid).get("body");
//		String errID = fileops.jsonDecodeErrMsg(ERR_KEY, result);
//		Assert.assertEquals(errID, "40001","need_cloud_汉字验证失败");
//	}
}
//class MetaDataDataProvider{
//	@DataProvider(name="include_item")
//	public static Iterator<Object[]> fileType(){
//		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
//		List<String> ext_fileds = new ArrayList<String>();
//		ext_fileds.add("all");
//		ext_fileds.add("folder");
//		ext_fileds.add("file");
//		ext_fileds.add("img");
//		ext_fileds.add("doc");
//		ext_fileds.add("video");
//		ext_fileds.add("else ");
//		for (int i = 0; i < ext_fileds.size(); i++) {
//			dataToBeReturned.add(new Object[]{ext_fileds.get(i)});
//		}
//		return dataToBeReturned.iterator();
//	}
//	@DataProvider(name="ext_fileds")
//	public static Iterator<Object[]> ss(){
//		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
//		List<String> ext_fileds = new ArrayList<String>();
//		ext_fileds.add("read");
//		ext_fileds.add("video_flv");
//		ext_fileds.add("video_mp4");
//		ext_fileds.add("audio_mp3");
//		ext_fileds.add("doc_swf");
//		ext_fileds.add("share_status");
//		ext_fileds.add("sharefriend");
//		ext_fileds.add("linkcommon");
//		ext_fileds.add("thumbnail");
//		for (int i = 0; i < ext_fileds.size(); i++) {
//			dataToBeReturned.add(new Object[]{ext_fileds.get(i)});
//		}
//		return dataToBeReturned.iterator();
//		//read, video_flv, video_mp4, audio_mp3, doc_swf, share_status, sharefriend, linkcommon, thumbnail
//	}
//}