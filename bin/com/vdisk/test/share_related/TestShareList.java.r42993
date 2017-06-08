package com.vdisk.test.share_related;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;

public class TestShareList extends VdiskAPITest{
	@Parameters({"uid","sharelist_uri","local_file_path"})
	@BeforeClass
	public void init(String uid,String sharelist_uri,String local_file_path){
		setUid(uid);
		setUri(sharelist_uri);
		fileops.addLocalFile(local_file_path);
	}
	@BeforeMethod
	public void cleanParams(){
		paramsMap.clear();
		vdiskRequest.getReqsInfo().clear();
	}
//	@Test(description="按照分类获取分享列表")
//	public void testShareList_getShareListByCategory() {
//		String cid = CommonUtils.getCategoryID();
//		paramsMap.put("category_id", cid);
//		String res = shareops.getShareList(paramsMap, uid);
//		System.out.println(res);
//		JSONArray arr = new JSONArray().fromObject(res);
//		List<String> equalsCid = new ArrayList<String>();
//		for (int i = 0; i < arr.size(); i++) {
//			String category_id =arr.getJSONObject(i).getString("category_id");
//			if(cid.equals(category_id))
//				equalsCid.add(category_id);
//		}
//		Assert.assertTrue(equalsCid.size() == arr.size(),"testShareList_按照分类分享文件列表失败\n"
//				+ CommonUtils.getRequestURL(vdiskRequest.getReqsURL()));
//	}
	@Test(dataProvider="vdisk_shareFileType",dataProviderClass=com.vdisk.entity.TestDataProvider.class,
			description="按照文件类型获取分享列表")
	public void testShareList_getShareListByFileType(String fileType){
		paramsMap.put("type", fileType);
		String res = shareops.getShareList(paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(res);
		List<String> equalsFileType = new ArrayList<String>();
		for (int i = 0; i < arr.size(); i++) {
			String fileName = arr.getJSONObject(i).getString("name");
			String suffix = CommonUtils.getFiletype(fileName);
			if(fileType.equals(suffix))
				equalsFileType.add(suffix);
		}
		Assert.assertTrue(equalsFileType.size() == arr.size(),"testShareList_按照文件类型获取分享列表失败\n"
				+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(dataProvider="vdisk_shareFileType",dataProviderClass=com.vdisk.entity.TestDataProvider.class,
			description="按照分类及文件类型获取分享文件列表")
	public void testShareList_getShareListByCategoryAndFileTyp(String fileType){
		String cid = CommonUtils.getCategoryID();
		paramsMap.put("category_id", cid);
		paramsMap.put("type", fileType);
		paramsMap.put("pagesize", "10");
		String res = shareops.getShareList(paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(res);
		List<String> equalsSuffix = new ArrayList<String>();
		for (int i = 0; i < arr.size(); i++) {
			String name = arr.getJSONObject(i).getString("name");
//			System.out.println(name);
			String file_suffix = CommonUtils.getFiletype(name);
			if(file_suffix.equals(fileType))
				equalsSuffix.add(file_suffix);
		}
		Assert.assertTrue(equalsSuffix.size() == arr.size(),"testShareList_按照分类及文件类型获取分享文件列表失败\n"
				+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="指定每页文档数量返回分享文件列表")
	public void testShareList_page_size() {
		String cid = CommonUtils.getCategoryID();
		int page_size = 5;
		paramsMap.put("category_id", cid);
		paramsMap.put("page_size", page_size+"");
		String res = shareops.getShareList(paramsMap, uid);
		JSONArray arr = new JSONArray().fromObject(res);
		Assert.assertTrue(arr.size() == page_size, "testShareList_指定每页文档数量失败"
						+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="文件列表包含结果总数")
	public void testShareList_getTotalNum() {
		String cid = CommonUtils.getCategoryID();
		int page_size = 1;
		int page = 2;
		paramsMap.put("category_id", cid);
		paramsMap.put("page_size", page_size+"");
		paramsMap.put("page", page+"");
		paramsMap.put("needtotal", true+"");
		String res = shareops.getShareList(paramsMap, uid);
//		System.out.println(res);
		int totalnum = Integer.valueOf(fileops.jsonDecode(res, "totalnum"));
		Assert.assertTrue(totalnum >= page * page_size, "testShareList_获取文件总数失败\n"
						+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
	@Test(description="不带任何参数")
	public void testShareList_noneParams(){
		String res = shareops.getShareList(null, uid);
		JSONArray arr = new JSONArray().fromObject(res);
		Assert.assertTrue(arr.size() >= 20,"testShareList_无参请求失败\n"
				+ CommonUtils.getRequestURL(vdiskRequest.getReqsInfo()));
	}
}
