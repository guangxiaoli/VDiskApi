package com.vdisk.operator;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.entity.VdiskRequest;
import com.vdisk.operator.VdiskFileOperator;
import com.vdisk.util.CommonUtils;
//extends VdiskAPITest


public class VdiskLinkcommonOperator  {
	
	protected VdiskAPITest vdiskAPITest;
	protected VdiskFileOperator fileops;
	protected Map<String,String> paramsMap;
	public List<File> fList;
	private VdiskRequest reqs;
	
	public VdiskLinkcommonOperator(){
		this.reqs = new VdiskRequest();
	}
	public VdiskLinkcommonOperator(VdiskRequest reqs){
		this.reqs = reqs;
	}
	public VdiskRequest getVdiskRequest(){
		return reqs;
	}

	/**
	 * 创建一个提取码分享
	 * @param paramsMap
	 * @param FilePath : 文件路径
	 * @param uid : 用户uid
	 * @return : 返回提取码分享后的信息
	 */
	public String getCode(Map<String,String> paramsMap,String FilePath, String uid){
		String linkcommon_new_uri="/2/linkcommon/new";
		paramsMap.put("path", FilePath);
		paramsMap.put("root", "basic");
		reqs.doPost(linkcommon_new_uri, paramsMap, null, uid);
		String result = reqs.getVdiskResult().getBody();
		return result;
	}
	
//	/**
//	 * 上传文件返回上传后的信息
//	 * @param local_file_path : 本地路径
//	 * @param vDirector : 要上传的指定目录
//	 * @param uid : 用户uid
//	 * @return	上传文件的返回信息
//	 */
//	public String upFile(String local_file_path,String vDirector,String uid ){
//		fileops.addLocalFile(local_file_path);
//		File file = fileops.fList.get(0);
//		String body = fileops.upLoadFile(file, vDirector, uid, paramsMap);
//		return body;
//	}

//	public String shareFile(Map<String,String> paramsMap,String FilePath,String uid){
//		String  result=fileops.shares(FilePath, "false", uid);
//		System.out.println(result);
//		String sharepath=fileops.jsonDecode(result, "url");
//		return sharepath;	
//	}	
	

}
