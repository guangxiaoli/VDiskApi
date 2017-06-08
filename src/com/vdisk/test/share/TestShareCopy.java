package com.vdisk.test.share;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.*;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.util.CommonUtils;
public class TestShareCopy extends VdiskAPITest{
	@Parameters({"uid","share_copy_uri","local_file_path"})
	@BeforeClass
	public void inti(String uid,String share_copy_uri,String local_file_path){
		setUid(uid);
		setUri(share_copy_uri);	
		fileops.addLocalFile(local_file_path);
	}
	
	@Test(description="sharecopy")
	public void  testShareCopy(){
		
//		String vDirectorShare=createFolder("shareCopy");
//		String vDirectorSrc = createFolder("src");
//		String vDirectorCopy=createFolder("file");
////
////		
//		//上传一个源文件
//		String srcPath=uploadFile(vDirectorSrc);
//		System.out.println(srcPath);
//		//copy 500份
//		createAndcopy(srcPath,vDirectorCopy,500); 
//		String shareID=share(vDirectorCopy);
//		System.out.println(shareID);
////
////
//		//String shareID="A43SvxqE8Xl2h";
//		shareCopy(shareID,vDirectorShare);	
	
		//删除shareCopy 文件
//	fileops.delete(vDirectorShare, uid);	
	
		
		shareCopy.shareCopyInfo("3925816567", "srFiles", "shareCopy", 50, "F:/FileUp",3);
	}
	

	
	//保存share 到指定目录
	public void shareCopy(String shareID,String vDirectorShare){
		paramsMap.put("root", "basic");
		paramsMap.put("from_copy_ref", shareID);		
		paramsMap.put("to_path", vDirectorShare+"/"+"shareCopyFile");
		vdiskRequest.doPost(uri, paramsMap, null, uid);
		String res = vdiskRequest.getVdiskResult().getBody();
		String path=fileops.jsonDecode(res, "path");
//		String path=vDirectorShare+"/"+"shareCopyFile";
		String body=fileops.getInfo(path, null, uid).get("body");
		
		JSONArray arr = new JSONArray().fromObject(fileops.jsonDecode(body, "contents"));
		System.out.println("分享Cpoy的数量为："+arr.size());
		if(arr.size() == 1){
			System.out.println(res);
		}
	}
	
	//分享 首层文件夹 返回短链
	public String   share(String  vDirectorCopy){
	String res=	fileops.shares(vDirectorCopy, "false", uid);
	String  url=fileops.jsonDecode(res, "url");	
	String shareID=url.substring(url.lastIndexOf("/")+1,url.length());	
	return shareID;
	}
	
	//从指定的文件夹中 copy N个文件
	public void createAndcopy(String srcPath,String vDirectorCopy,int number){
		for (int i=1;i<=number;i++){
			 paramsMap.put("root", "basic");
			 paramsMap.put("from_path",srcPath);
			 paramsMap.put("to_path", vDirectorCopy+"/"+i+".docx");
			 fileops.copyFile(paramsMap, uid);	 
		}
	}
	//上传一个文件 返回文件源路径
	public String uploadFile(String vDirectorSrc){
	File file = fileops.fList.get(0);
	String body = fileops.upLoadFile(file, vDirectorSrc, uid, paramsMap);
	//System.out.println(body);
	String srcPath=fileops.jsonDecode(body, "path");
	return srcPath;
	}
	
	
}
