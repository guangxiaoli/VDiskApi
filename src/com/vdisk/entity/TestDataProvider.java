package com.vdisk.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;


public class TestDataProvider {
	//扩展字段类型
	@DataProvider(name="ext_fileds")
	public static Iterator<Object[]> ss(){
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		List<String> ext_fileds = new ArrayList<String>();
		ext_fileds.add("read");
		ext_fileds.add("video_flv");
		ext_fileds.add("video_mp4");
		ext_fileds.add("audio_mp3");
		ext_fileds.add("doc_swf");
		ext_fileds.add("share_status");
		ext_fileds.add("sharefriend");
		ext_fileds.add("linkcommon");
		ext_fileds.add("thumbnail");
		for (int i = 0; i < ext_fileds.size(); i++) {
			dataToBeReturned.add(new Object[]{ext_fileds.get(i)});
		}
		return dataToBeReturned.iterator();
		//read, video_flv, video_mp4, audio_mp3, doc_swf, share_status, sharefriend, linkcommon, thumbnail
	}
	//metadata类型
	@DataProvider(name="include_item")
	public static Iterator<Object[]> fileType(){
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		List<String> ext_fileds = new ArrayList<String>();
		ext_fileds.add("all");
		ext_fileds.add("folder");
		ext_fileds.add("file");
		ext_fileds.add("img");
		ext_fileds.add("doc");
		ext_fileds.add("video");
		ext_fileds.add("audio");
		ext_fileds.add("else");
		for (int i = 0; i < ext_fileds.size(); i++) {
			dataToBeReturned.add(new Object[]{ext_fileds.get(i)});
		}
		return dataToBeReturned.iterator();
	}
	@DataProvider(name="file_suffix")
	public static Iterator<Object[]> fileSuffix(){
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		List<String> ext_fileds = new ArrayList<String>();
		ext_fileds.add(".txt");
		ext_fileds.add(".doc");
		ext_fileds.add(".pdf");
		ext_fileds.add(".wmv");
		ext_fileds.add(".swf");
		ext_fileds.add(".mp3");
		ext_fileds.add(".rar");
		for (int i = 0; i < ext_fileds.size(); i++) {
			dataToBeReturned.add(new Object[]{ext_fileds.get(i)});
		}
		return dataToBeReturned.iterator();
	}
	//分享文件类型
	@DataProvider(name="vdisk_shareFileType")
	public static Iterator<Object[]> shareFileType(){
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();
		List<String> shareFileType = new ArrayList<String>();
		shareFileType.add("v_doc");
		shareFileType.add("v_video");
		shareFileType.add("v_img");
		shareFileType.add("v_audio");
		shareFileType.add("v_folder");
		shareFileType.add("else");
//		shareFileType.add("v_title");
		for (int i = 0; i < shareFileType.size(); i++) {
			dataToBeReturned.add(new Object[]{shareFileType.get(i)});
		}
		return dataToBeReturned.iterator();
	}
}
