package com.vdisk.operator;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.vdisk.entity.VdiskAPITest;
import com.vdisk.entity.VdiskRequest;
import com.vdisk.operator.VdiskFileOperator;
import com.vdisk.util.CommonUtils;

public class VdiskShareCopyOperator{
	protected VdiskAPITest vdiskAPITest;
	protected VdiskFileOperator fileops;
	protected Map<String,String> paramsMap=new HashMap<>();
	public List<File> fList;
	private VdiskRequest vdiskRequest;
	public VdiskShareCopyOperator(){
		this.vdiskRequest = new VdiskRequest();
		this.fileops = new VdiskFileOperator();
	}
	public VdiskShareCopyOperator(VdiskRequest reqs){
		this.vdiskRequest = reqs;
		this.fileops = new VdiskFileOperator(vdiskRequest);
	}
	public VdiskRequest getVdiskRequest(){
		return vdiskRequest;
	}

	
	/**
	 * shareCopy 分享信息
	 * @param uid ： 用户uid
	 * @param FromFolderName : 上传文件的指定文件夹
	 * @param ToFolderName ： 指定copyshare文件夹
	 * @param copyTimes : 文件copy数量
	 * @param local_file_path ：本地文件路径
	 * @param num ：copyshare 次数
	 */
	public  void  shareCopyInfo(String uid,String srcFolder, String shareFolder,int copyTimes,String local_file_path,int num){
			//上传本地路径下的所有文件，取得所有上传文件的信息
			String filesInfo[]=uploadFile(srcFolder,uid,local_file_path);
			//把所有的上传文件复制到指定数量
			String copyFloder=createAndcopy(filesInfo,copyTimes,uid,paramsMap);
			//分享复制的所有文件，取得分享短链
			String sharID=share(copyFloder,uid);
			// 循环不停的sharecopy 
			for (int i=1;i<=num;i++){
			int copy=shareCopy(sharID,shareFolder,uid);
			System.out.println("第"+i+"次,分享COPY的数量为："+copy);
		}
	}
	
	
	
	
	
	/**
	 * 分享copy  
	 * @param shareID : 分享短链
	 * @param shareFolder ：copy到指定文件夹
	 * @param uid
	 * @return
	 */
	protected int shareCopy(String shareID,String shareFolder,String uid){
		String shareCopyUri="/2/share/copy";
		String shareFolderName=createFolder(shareFolder,uid);
		
		paramsMap.put("root", "basic");
		paramsMap.put("from_copy_ref", shareID);		
		paramsMap.put("to_path", shareFolderName+"/"+"ShareCopy");
		vdiskRequest.doPost(shareCopyUri, paramsMap, null, uid);
		//获取shareCopy 信息
		String res = vdiskRequest.getVdiskResult().getBody();
		String path=fileops.jsonDecode(res, "path");
		String body=fileops.getInfo(path, null, uid).get("body");
		
		//取得shareCopy数量
		JSONArray arr = new JSONArray().fromObject(fileops.jsonDecode(body, "contents"));
		int shareCopyNum=arr.size();
		return shareCopyNum;
		//System.out.println("分享Cpoy的数量为："+arr.size());
	}
	
	
	
	/**
	 * 分享文件夹，取得分享短链
	 * @param fileCopy ：要分享的文件夹
	 * @param uid ：用户uid
	 * @return
	 */
	protected String share(String fileCopy,String uid){
	String shareInfo=fileops.shares(fileCopy, "false", uid);
	String shareUrl=fileops.jsonDecode(shareInfo, "url");	
	String shareID=shareUrl.substring(shareUrl.lastIndexOf("/")+1,shareUrl.length());	
	return shareID;
	}
	
	/**
	 *  从源文件夹中copy指定数量的文件 
	 * @param fileInfo ：所有上传文件的信息
	 * @param copyTimes : 每个文件copy次数 
	 * @param uid ： 用户uid
	 * @param paramsMap
	 * @return
	 */
	protected String createAndcopy(String[] filesInfo,int copyTimes,String uid,Map<String,String> paramsMap){
		//指定一个存放copy文件的文件夹
		String fileCopy=createFolder("CopyFiles", uid);
		//取得filesInfo[] 中 每个文件的path 
		for (int i=0;i<filesInfo.length;i++){
			String filePath=fileops.jsonDecode(filesInfo[i], "path");
			String lastName=filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
			//copy指定数量的文件
			for (int j=1;j<=copyTimes;j++){
				 paramsMap.put("root", "basic");
				 paramsMap.put("from_path",filePath);
				 paramsMap.put("to_path", fileCopy+"/"+j+"."+lastName);
				 fileops.copyFile(paramsMap, uid);	
			}
			
		}
		return fileCopy;
	}
	
	
	/**
	 * 上传多个文件到指定目录， 并且 返回所有上传文件的信息
	 * @param FromFolderName ： 上传文件的指定目录
	 * @param uid ：用户uid
	 * @param local_file_path ： 上传文件的本地路径
	 * @return
	 */
	protected String[] uploadFile(String srcFolder,String uid,String local_file_path){
		fileops.addLocalFile(local_file_path);
		String folderPath=createFolder(srcFolder, uid);
		//循环上传本地路径内的所有文件  返回信息放入 filesInfo[]
		String[] filesInfo=new String[fileops.fList.size()];
		System.out.println(fileops.fList.size());
		for(int i=0;i<fileops.fList.size();i++){
		File file = fileops.fList.get(i);
		filesInfo[i] = fileops.upLoadFile(file, folderPath, uid, paramsMap);
	}
	return filesInfo;
	}
	
	//创建文件夹  返回path
	protected String createFolder(String folderName, String uid){
		long timeStamp = System.currentTimeMillis();
		String folder = folderName + "_" + CommonUtils.timeStampCovertDate(timeStamp);
		String body = fileops.createFolder(folder, uid);
		return fileops.jsonDecode(body, "path");

	}
}
