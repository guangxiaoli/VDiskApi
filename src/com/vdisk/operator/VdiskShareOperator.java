package com.vdisk.operator;

import java.util.Map;

import com.vdisk.entity.VdiskRequest;

public class VdiskShareOperator {
	private VdiskRequest reqs;
	
	public VdiskShareOperator(){
		this.reqs = new VdiskRequest();
	}
	public VdiskShareOperator(VdiskRequest reqs){
		this.reqs = reqs;
	}
	public VdiskRequest getVdiskRequest(){
		return reqs;
	}

	/**获取分享文件列表
	 * @param Map<String,String> : 参数列表(category_id,type,sort_order,page,page_size,platform,need_ext,needtotal,is_iask)
	 * @param uid : 用户uid
	 * @return String : 分享文件列表详细信息
	 * */
	public String getShareList(Map<String,String> paramsMap,String uid){
		String uri = "/2/share/list";
		reqs.doGet(uri, paramsMap, null, uid);
		StringBuffer sb = new StringBuffer(reqs.getVdiskResult().getBody()); 
		return sb.toString();
	}
	/**获取分享对象列表
	 * @param Map<String,String> : 参数列表(copy_ref,share_id&sina_uid,long_share_id任选其一)
	 * @param uid : 用户uid
	 * @return String : 分享文件详细信息
	 * */
	public String getShareObjecInfo(Map<String,String> paramsMap,String uid){
		String uri = "/2/share/get";
		reqs.doGet(uri, paramsMap, null, uid);
		StringBuffer sb = new StringBuffer(reqs.getVdiskResult().getBody()); 
		return sb.toString();
	}
}
