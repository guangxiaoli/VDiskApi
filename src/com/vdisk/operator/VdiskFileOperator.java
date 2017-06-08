package com.vdisk.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import com.vdisk.entity.VdiskRequest;
import com.vdisk.util.CommonUtils;

public class VdiskFileOperator {
	public static final String UPLOAD_FILE = "https://upload-vdisk.sina.com.cn";
//	public static final long filePartSize = 4194304;  //4m 
	public static final long filePartSize = 1024 * 1024 * 2;  //4m 
	public List<File> fList;
	private VdiskRequest reqs;

	public VdiskFileOperator(){
		this.reqs = new VdiskRequest();
	}
	public VdiskFileOperator(VdiskRequest reqs){
		this.reqs = reqs;
	}
	public VdiskRequest getVdiskRequest(){
		return reqs;
	}
	//添加本地上传文件
	public void addLocalFile(String up_file_path){
		fList = new ArrayList<File>();
		File file = new File(up_file_path);
		File[] fileArr = file.listFiles();
		for (int i = 0; i < fileArr.length; i++) {
			fList.add(fileArr[i]);
		}
	}
	/**大文件上传初始化并批量获取分片签名
	 * @param paramsMap : 参数列表(*root,*path,*part_total,*size,sha1,overwrite,parent_rev)
	 * @param uid : 用户uid
	 * @return Map : 如果带秒传参数且成功秒传，返回上传文件信息，否则返回upload_id,upload_key
	 * */
	public Map<String,String> multiPartInit(Map<String,String> paramsMap,String uid){
		String uri = "/2/multipart/init";
		reqs.doPost(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
//		System.out.println(body);
		JSONObject json = new JSONObject().fromObject(body);
		Map map = new HashMap();
		if(json.containsKey("upload_id") && json.containsKey("upload_key")){
			map.put("upload_id", json.getString("upload_id"));
			map.put("upload_key", json.getString("upload_key"));
			return map;	
		}else{
			map.put("path", json.getString("path"));
			map.put("md5", json.getString("md5"));
			map.put("sha1", json.getString("sha1"));
			map.put("size", json.getString("bytes"));
			return map;
		}
	}
	/**批量获取大文件上传分片签名
	 * @param Map : 文件upload_id、upload_key、part_range
	 * @param uid : 用户uid
	 * @return Map 文件分片签名
	 * */
	public Map<Integer,String> multiPartSign(Map<String,String> multiPartInit,String uid){
		String uri = "/2/multipart/sign";
		multiPartInit.put("root", "basic");
		reqs.doPost(uri, multiPartInit, null, uid);
		String body = reqs.getVdiskResult().getBody();
//		System.out.println("签名" + body);
		JSONObject jo = new JSONObject().fromObject(body);
		String part_sign = jo.getString("part_sign");
		JSONObject json = new JSONObject().fromObject(part_sign);
		Iterator it = json.keys();
		Map<Integer,String> map = new HashMap<Integer, String>();
		while(it.hasNext()){
			String key = (String)it.next();
			String value = json.getString(key);
//			System.out.println(key);
//			System.out.println(value);
			JSONObject obj = new JSONObject().fromObject(value);
			map.put(Integer.valueOf(key), obj.getString("uri"));
		}
		return map;
	}
	
	/**上传文件分片
	 * @param splitFiles : 分片文件列表
	 * @param multiPartSign : 文件分片的签名
	 * @param uid : 用户uid
	 * @return String : 文件分片上传返回信息
	 * */
	public void uploadFilePart(List<File> splitFiles,Map<Integer,String> multiPartSign,String uid){
		String host = "http://up.sinastorage.com" ;
		Iterator<Entry<Integer, String>> it  = multiPartSign.entrySet().iterator();
		for (int i = 0; i < splitFiles.size(); i++) {
			String url = host + multiPartSign.get(i+1);
			String res = putMultiPart(url, splitFiles.get(i), uid);
		//	System.out.println(res);
		}
	}
	/**文件合并
	 * @param splitFiles : 分片文件列表
	 * @param paramsMap : 参数列表
	 * @param uid : 用户uid
	 * */
	public String multiPartCombined(List<File> splitFiles,Map<String,String> paramsMap,String uid){
		String uri = "/2/multipart/complete";
		reqs.doPost(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
		return body;
	}
	/**大文件分片上传
	 * @param file : 待上传的文件对象
	 * @param paramsMap : 参数列表(*root,*path,*part_total,*size,sha1,overwrite,parent_rev)
	 * @param blockSize : 文件分片大小
	 * @param uid : 用户uid
	 * @return String ：上传文件信息
	 * */
	public String multiFileUpload(File file,String path,long blockSize,boolean overwrite,String uid){
		Map<String,String> paramsMap = new HashMap<String, String>();
		//文件分片列表
		List<File> splitFiles = CommonUtils.splitFile(file, file.getParent(), blockSize);
		//添加获取upload_id,upload_key请求参数
		paramsMap.put("root", "basic");
		paramsMap.put("path", path);
		paramsMap.put("part_total", String.valueOf(splitFiles.size()));
		paramsMap.put("size", String.valueOf(file.length()));
		paramsMap.put("overwrite", String.valueOf(overwrite));
		//获取upload_id,upload_key
		Map<String,String> multiPartInit = multiPartInit(paramsMap, uid);
//		System.out.println("初始化：" +multiPartInit);
		//添加获取分片签名请求参数
		multiPartInit.put("root", "basic");
		multiPartInit.put("part_range", 1 + "-" + splitFiles.size());
		//获取分片签名
		Map<Integer,String> multiPartSign = multiPartSign(multiPartInit, uid);
		multiPartInit.remove("part_range");
//		System.out.println("签名：" +multiPartSign);
		//分片上传
		uploadFilePart(splitFiles, multiPartSign, uid);
		//拼接分片md5
		StringBuffer md5_list = new StringBuffer();
		for (int i = 0; i < splitFiles.size(); i++) {
			String fileMd5 = CommonUtils.fileMd5(splitFiles.get(i));
			md5_list.append(fileMd5);
			if(i < (splitFiles.size() - 1))
				md5_list.append(",");
		}
		System.out.println(md5_list.toString());
		//添加合并分片参数
		multiPartInit.put("path", path);
		multiPartInit.put("md5_list",md5_list.toString());
		multiPartInit.put("overwrite", String.valueOf(overwrite));
		String body = "";
		//合并文件
		body = multiPartCombined(splitFiles, multiPartInit, uid);
		System.out.println("合并：" + body);
		//删除本地文件分片
		System.out.println(splitFiles);
//		for (int i = 0; i < splitFiles.size(); i++) {
//			splitFiles.get(0).delete();
//		}
		return body;
	}
	/**put上传文件分片
	 * @param url : 请求地址
	 * @param File : 待上传文件
	 * @param uid : 用户uid
	 * @return String 分片文件上传返回信息
	 * 不需要微盘签名认证
	 * */
	private String putMultiPart(String url,File file,String uid){
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/octet-stream");
//		String uriForSign = "/upload.vdisk.me" + "?x-vdisk-cuid=" + uid;
//		String expire = CommonUtils.getExpireTime(0);
//		String commonParam = reqs.generateCommonParam("PUT",null,uriForSign,expire);
//		url = url + commonParam;
//		System.out.println(url);
		HttpPut put = new HttpPut(url);
		HttpResponse response  = null;
    	MultipartEntity multiEntity = new MultipartEntity();
    	multiEntity.addPart("file", new FileBody(file,"multipart/form-data"));
    	put.setEntity(multiEntity);
    	put.setHeader("Content-Type","application/octet-stream");
    	try {
			response = reqs.CLIENT.execute(put);
			if(response != null){
				reqs.setReturnInfo(response);
				String body = reqs.getVdiskResult().getBody();
				return body;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch blocks
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	//Post上传文件
	/**
	 * @param file : 文件对象
	 * @param filePath : 文件路径
	 * @param uid : 用户uid
	 * @param paramsMap : 参数列表
	 * @return String : 上传文件后的相关信息
	 * */
	public String upLoadFile(File file,String filePath,String uid,Map<String,String> paramsMap){
		String sha1 = CommonUtils.fileSha1(file);
		String size = String.valueOf(file.length());
		String uriForSign = null;
		if(paramsMap != null && paramsMap.size() != 0){
			uriForSign = "/2/files/basic" + filePath + "/"  + file.getName() + "?x-vdisk-cuid=" + uid
					+ "&sha1=" + sha1 + "&size=" + size + CommonUtils.getRequestParams(paramsMap);
		}else{
			uriForSign = "/2/files/basic" + filePath + "/"  + file.getName() + "?x-vdisk-cuid=" + uid
							+ "&sha1=" + sha1 + "&size=" + size;
		}
		String expire = CommonUtils.getExpireTime(3600);
		String commonParam = reqs.generateCommonParam("POST",null,uriForSign,expire);
		String url = UPLOAD_FILE + uriForSign + commonParam;
//		String url = "http://upload-vdisk.sina.com.cn" + uriForSign + commonParam;
//		System.out.println(url);
		HttpPost post = new HttpPost(url);
		HttpResponse response  = null;
    	MultipartEntity multiEntity = new MultipartEntity();	
    	multiEntity.addPart("file", new FileBody(file,"multipart/form-data"));
    	post.setEntity(multiEntity);
    	
    	try {
			response = reqs.CLIENT.execute(post);
//			System.out.println("success");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		if(response != null){
			reqs.setReturnInfo(response);
			String body = reqs.getVdiskResult().getBody();
//			System.out.println("==============" + body);
//			JSONObject json = JSONObject.fromObject(body);
//			Map<String,String> retInfoMap = new HashMap<String, String>();
//			retInfoMap.put("bytes", json.getString("bytes"));
//			retInfoMap.put("path", json.getString("path"));
//			retInfoMap.put("revision", json.getString("revision"));
//			retInfoMap.put("md5", json.getString("md5"));
//			retInfoMap.put("sha1", json.getString("sha1"));
//			retInfoMap.put("retCode", json.getString("sha1"));
//			return retInfoMap;
			reqs.getReqsInfo().put(url, body);//保存请求url及对应的body
			return body;
		}
		return null;
	}
	
	//Put上传文件
	/**
	 * @param file
	 *  上传文件
	 * @param filePath
	 *  上传文件路径
	 * @param uid
	 *  用户uid
	 * @return Map<String,String>
	 *  上传文件后的相关信息
	 * */
	public String upLoadFile_put(File file,String filePath,String uid,Map<String,String> paramMap){
		String sha1 = CommonUtils.fileSha1(file);
		String size = String.valueOf(file.length());
		String uriForSign = null;
		if(paramMap != null){
			uriForSign = "/2/files_put/basic" + filePath + "/"  + file.getName() + "?x-vdisk-cuid=" + uid
					+ "&sha1=" + sha1 + "&size=" + size + CommonUtils.getRequestParams(paramMap);	
		}else{
			uriForSign = "/2/files_put/basic" + filePath + "/"  + file.getName() + "?x-vdisk-cuid=" + uid
							+ "&sha1=" + sha1 + "&size=" + size;
		}
		String expire = CommonUtils.getExpireTime(3600);
		String commonParam = reqs.generateCommonParam("PUT",null,uriForSign,expire);
		String url = UPLOAD_FILE + uriForSign + commonParam;
//		System.out.println(url);
		HttpPut put = new HttpPut(url);
		HttpResponse response  = null;
//    	MultipartEntity multiEntity = new MultipartEntity();	
//    	multiEntity.addPart("HTTP", new FileBody(file,"multipart/form-data"));
//    	put.setEntity(multiEntity);
    	
//		FileEntity fileEntity = new FileEntity(file, "binary/octet-stream");
		FileEntity fileEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM );
		put.setEntity(fileEntity);
		
//    	//请求秒传参数sha1、size
//		HttpEntity paramEntity = null;
//		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
//    	String sha1 = CommonUtils.fileSha1(file);
//    	String size = String.valueOf(file.length());
//    	Map<String,String> params = new HashMap<String, String>();
//    	params.put("sha1", sha1);
//    	params.put("size", size);
//    	for(Map.Entry<String, String> myEntry : params.entrySet()){
//    		paramList.add(new BasicNameValuePair(myEntry.getKey(), myEntry.getValue()));
//    	}		
//    	try {
//			paramEntity = new UrlEncodedFormEntity(paramList,HTTP.UTF_8);
//			post.setEntity(paramEntity);
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	try {
			response = reqs.CLIENT.execute(put);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(response != null){
			reqs.setReturnInfo(response);
			String body = reqs.getVdiskResult().getBody();
			reqs.getReqsInfo().put(url, body);//保存请求url及对应的body
			return body;
		}
		return null;

	}	
	
	//下载文件
	/**
	 * @param srcFilePath
	 *  源文件路径(包含文件名)
	 * @param destFilePath
	 *  本地文件路径(包含文件名)
	 * @param uid
	 *  用户uid
	 * @param params
	 *  请求参数
	 * @return
	 *  下载文件对象
	 * */
    public File dowLoadFileToLocal(String srcFilePath,String destFilePath,String uid,Map<String,String> params){
		String uri = "/2/files/basic" + srcFilePath;
		reqs.doGet(uri, params, null, uid);
    	int retCode = reqs.getVdiskResult().getRetCode();
    	if(retCode == 302){
    		String downLoadUrl = reqs.getVdiskResult().getRespHeaders("location");
    		HttpGet httpGet = new HttpGet(downLoadUrl);
    		FileOutputStream fos = null;
    		try {
				HttpResponse response = reqs.CLIENT.execute(httpGet);
				HttpEntity entity = response.getEntity();
				File file  = new File(destFilePath);
				if(!file.exists()){
					file.createNewFile();
				}
				InputStream ins = entity.getContent();
				fos = new FileOutputStream(file);
				int len = -1;
				byte buffer[] = new byte[1024];
				while((len=ins.read(buffer)) != -1){
					fos.write(buffer, 0, len);
				}
				fos.flush();
				ins.close();
//				BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
//				String str = null;
//				while((str=br.readLine()) != null){
//					bw.write(str);
//					bw.newLine();
//				}
//				br.close();bw.flush();bw.close();
				return file;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//    		finally{
//				if(fos != null){
//					try {
//						fos.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
    	}
		return null;
    }
	/**
	 * 获取文件对象信息
	 *  @param path
	 *  文件对象路径
	 *  @param paramsMap
	 *  参数map
	 *  @param uid
	 *  用户uid
	 *  @return
	 *  Map (包括body、retCode)
	 * */
   public Map<String,String> getInfo(String path, Map<String,String> paramsMap,String uid){
		String uri = "/2/metadata/basic" + path;
		reqs.doGet(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
		String redCode = String.valueOf(reqs.getVdiskResult().getRetCode());
//		JSONObject json = new JSONObject().fromObject(body);
		Map<String, String> respMap = new HashMap<String, String>();
		respMap.put("body", body);
		respMap.put("retCode", redCode);
		return respMap;
   }
    /**复制文件
     *  @param paramsMap : 参数列表(root,from_path,to_path)
     *  @param uid : 用户uid
     *  @return String
     * */
   	public String copyFile(Map<String,String> paramsMap,String uid){
    	String uri = "/2/fileops/copy";
    	reqs.doPost(uri, paramsMap, null, uid);
    	String body = reqs.getVdiskResult().getBody();
    	return body;
    }
	//创建文件夹
	/**
	 * @param folderPath
	 * 文件夹路径
	 * @param uid
	 * 用户uid
	 * @return
	 *  String
	 * */
	public String createFolder(String folderPath,String uid){
		String uri = "/2/fileops/create_folder";
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("root", "basic");
		paramsMap.put("path", folderPath);
		reqs.doPost(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
//		JSONObject json = JSONObject.fromObject(body);
//		System.out.println(body);
//		return json.get("path").toString();
		return body;
	}
	/**物理删除文件对象
	 * @param path
	 *  文件对象路径
	 * @param uid
	 *  用户uid
	 * */
	public boolean delete(String path,String uid){
		String uri = "/2/fileops/delete";
		Map<String,String> paramsMap = new HashMap<String, String>();
//		paramsMap.put("path", urlEncodePath(folder_path));
		paramsMap.put("path", path);
		paramsMap.put("act", "erase");
		paramsMap.put("root", "basic");
		reqs.doPost(uri, paramsMap, null, uid);
		JSONObject json = JSONObject.fromObject(reqs.getVdiskResult().getBody());
		return json.getBoolean("is_deleted");
	}
	/**
	 * 逻辑删除文件或文件夹
	 * @param paramsMap
	 * 参数列表(root,path,parent_rev,act)
	 * @param uid
	 * 用户uid
	 * @return
	 * boolean
	 * */
	public boolean logicToDelete(Map<String,String> paramsMap, String uid){
		String uri = "/2/fileops/delete";
		reqs.doPost(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
		JSONObject json = new JSONObject().fromObject(body);
		return Boolean.parseBoolean(json.getString("is_deleted"));
	}
	/**
	 * 重命名文件对象
	 *  @param paramsMap
	 *  参数列表(root,from_path,to_path)
	 *  @param uid
	 *  用户uid
	 *  @return
	 *  String
	 * */
	public String rename(Map<String,String> paramsMap,String uid){
		String uri = "/2/fileops/move";		
		reqs.doPost(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
		return body;
	}
	/**按文件类型分组
	 *  @param path
	 *  文件夹路径
	 *  @param paramsMap
	 *  参数列表(include_item)
	 *  @param uid
	 *  用户uid
	 * */
	public String groupByFileType(String path,Map<String, String> paramsMap,String uid){
		String uri = "/2/metadata/basic" + path;
		reqs.doGet(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
//		System.out.println(body);
		return body;
	}
	/**json_decode
	 * @param json_str
	 * @param key
	 * 
	 * @return String
	 * */
	public String jsonDecode(String json_str,String key){
		if(json_str.contains("contents")){
			JSONArray arr = new JSONObject().fromObject(json_str).getJSONArray("contents");
			for (int i = 0; i < arr.size(); i++) {
				if(arr.getJSONObject(i).containsKey(key)){
					return arr.getJSONObject(i).getString(key);
				}	
			}
		}
		JSONObject json = new JSONObject().fromObject(json_str);
		return json.getString(key);
	}
	/**获取文件历史版本号
	 * @param path : 文件路径
	 * @param uid : 用户uid
	 * @param rev_limit : 读取版本个数
	 * @return String
	 * */
	public String getFileRev(String path, String uid, String rev_limit){
		String uri = "/2/revisions/basic" + path;
		String result = "";
		if(rev_limit.equals("10")){
			reqs.doGet(uri, null, null, uid);
		}else{
			Map<String,String> map = new HashMap<String, String>();
			map.put("rev_limit", rev_limit+"");
			reqs.doGet(uri, map, null, uid);
		}
		result = reqs.getVdiskResult().getBody();
		return result;
	}
	/**按照指定值生成file_revision
	 * @param path : 文件路径
	 * @param download_path : 文件下载路径
	 * @param times : file_revision数量
	 * @param uid : 用户uid
	 * @reurn void
	 * */
	public void generateFileRevision(String path,String download_path,int times,String uid){
		String upfile_path = path.substring(0,path.lastIndexOf("/"));
//		BufferedWriter bw = null;
		Map<String,String> map = new HashMap<String, String>();
		map.put("overwrite", "true");
		int i = 0;
		File file = dowLoadFileToLocal(path, download_path, uid, null);;
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(i < times){
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
				bw.write(i+"_" + CommonUtils.getExpireTime(0));
				bw.newLine();
				bw.flush();
				bw.close();
				upLoadFile(file, upfile_path, uid, map);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			i++;
		}
		//清除本地文件
		file.deleteOnExit();
	}
	/**
	 * 按照指定层级创建目录
	 * @param String parent : 父文件夹
	 * @param int level : 层级
	 * @return String : path
	 * */
	public String genPathLevel(String parent,int level,String uid){
		String delimiter = "/";
		StringBuffer sb = new StringBuffer(parent + delimiter);
		for(int i=0;i<level;i++){
			sb.append(CommonUtils.getExpireTime(i) + delimiter);
		}
		String path = sb.substring(0, sb.lastIndexOf(delimiter)).toString();
		return jsonDecode(createFolder(path, uid), "path");
//		return sb.substring(0, sb.lastIndexOf(delimiter));
	}
	//获取文件夹成员数量
	public int getFolderItemsNum(String folderPath,String uid){
		String uri = "/2/metadata/basic" + folderPath;
		reqs.doGet(uri, null, null, uid);
		String body = reqs.getVdiskResult().getBody();
		JSONArray arr = new JSONObject().fromObject(body).getJSONArray("contents");
		return arr.size();
	}
	/**查询文件或文件夹
	 * @param path : 文件或文件夹地址
	 * @param query : 关键字
	 * @param uid : 用户uid
	 * @return body
	 * */
	public String search(String path, Map<String,String> paramsMap, String uid){
		String uri = "/2/search/basic" + path;
		reqs.doGet(uri, paramsMap, null, uid);
		String body = reqs.getVdiskResult().getBody();
		return body;
	}
	/**获取用户分享列表
	 * @param paramsMap : 参数列表(page,page_size,type,sort_by,sort_order,ina_uid,need_total)
	 * @param uid : 用户uid
	 * @return String 
	 * */
	public String list_user_items(Map<String,String> paramsMap, String uid){
		String uri = "/2/share/list_user_items";
		reqs.doGet(uri, paramsMap, null, uid);
		StringBuffer sb = new StringBuffer(reqs.getVdiskResult().getBody());
		return sb.toString();
	}
	/**文件分享
	 * @param path : 文件对象路径
	 * @param cancel : 是否分享
	 * @param uid : 用户uid
	 * @return String分享信息
	 * */
	public String shares(String path, String cancel, String uid ){
		String uri = "/2/shares/basic" + path;
		
		if("".equals(cancel) || cancel == null){
			reqs.doPost(uri, null, null, uid);
		}else{
			Map<String,String> map = new HashMap<String, String>();
			map.put("cancel", cancel);
			reqs.doPost(uri, map, null, uid);
		}
		String body = reqs.getVdiskResult().getBody();
//		System.out.println(body);
		return body;
	}
		
//	private String urlEncodePath(String path){
//		String[] strArr = path.split("/");
//		System.out.println(strArr.length);
//		String str = "";
//		if(strArr.length > 1){
//			for (int i = 1; i < strArr.length; i++) {
//				str += strArr[i] + "%2F";
//				str += CommonUtils.urlEncode(strArr[i]) + "%2F";
//				System.out.println(str);
//			}
//			return "/" + str.substring(0,str.length() - 3);
//		}
//		return path;
//	}
//	public void createRecursionForder(String folder,String uid, int num){
//		String uri = "/2/fileops/create_folder";
//		Map<String,String> paramsMap = new HashMap<String,String>();
//		paramsMap.put("root", "basic");
//		paramsMap.put("path", folder);
//		int i = 0;
//		do{
//			String subFolder = folder + "/" + i;
//			paramsMap.put("path", subFolder);
//			reqs.doPost(uri, paramsMap, null, uid);
//			System.out.println(subFolder);
//			if(i > num){
//				String body = reqs.getVdiskResult().getBody();
//				System.out.println(body);
//			}
//			
//		}while(i++ <= num);
//		reqs.doPost(uri, paramsMap, null, uid);
//	}
    public static void main(String[] args) throws Exception{
    	VdiskFileOperator operate = new VdiskFileOperator();
//    	System.out.println("四舍五入取整:(2.1)=" + new BigDecimal("2.55631313").setScale(0, BigDecimal.ROUND_HALF_UP)); 
//    	File parent = new File("d:/DSC_0534.JPG");
//    	File file = new File(parent.getParentFile(),"1221.jpg");
//    	System.out.println(file.getName());
//    	System.out.println(file.exists());
//    	new FileInputStream(file);

    }
}
