package com.vdisk.test.file_related;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;


public class TestMedia extends VdiskAPITest{
	private String need_ext_folder;
	@Parameters({"uid","media_uri","local_file_path","need_ext"})
	@BeforeClass
	public void init(String uid,String media_uri,String local_file_path,String need_ext_folder){
		setUid(uid);
		setUri(media_uri);
		fileops.addLocalFile(local_file_path);
//		vDirector = createFolder(CommonUtils.getClassName(this));
		this.need_ext_folder = need_ext_folder;
	}
	@Test(description="获取扩展字段下载地址")
	public void testMedia_ExtFields(){
		String s = fileops.getInfo(need_ext_folder, null, uid).get("body");
		System.out.println(s);
		JSONArray arr = new JSONObject().fromObject(s).getJSONArray("contents");
		String file_sha1 = arr.getJSONObject(0).getString("sha1");
		String path = arr.getJSONObject(0).getString("path");
		String need_ext = "audio_mp3";
		String sha1 = media(path, need_ext);
		System.out.println(sha1);
		Assert.assertEquals(file_sha1, sha1,"testMedia_ExtFields验证扩展字段失败");
	}
	private String media(String path,String need_ext){
		String media_uri = uri + path;
		paramsMap.put("need_ext", need_ext);
		vdiskRequest.doGet(media_uri, paramsMap, null, uid);
		String body = vdiskRequest.getVdiskResult().getBody();
		String url = fileops.jsonDecode(body, "url");
		String sha1 = url.substring(url.lastIndexOf("/")+1,url.indexOf("?"));
		return sha1;
		
	}
}
