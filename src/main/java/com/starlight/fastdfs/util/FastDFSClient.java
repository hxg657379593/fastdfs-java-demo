package com.starlight.fastdfs.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * FastDFS分布式文件系统操作客户端 .
 * @author liuyazhuang
 */
public class FastDFSClient {

	private static final String CONF_FILENAME = "config/fastdfs-client.properties";
	private static StorageClient1 storageClient1 = null;
	private static Logger logger = LoggerFactory.getLogger(FastDFSClient.class);
	/**
	 * 只加载一次.
	 */
	static {
		try {
			ClientGlobal.initByProperties(CONF_FILENAME);
			logger.info("=== config:" + ClientGlobal.configInfo());
			TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
			TrackerServer trackerServer = trackerClient.getConnection();
			StorageServer storageServer = null;
			storageClient1 = new StorageClient1(trackerServer, storageServer);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	/**
	 * 
	 * @param file
	 *            文件
	 * @param fileName
	 *            文件名
	 * @return 返回Null则为失败
	 */
	public static String uploadFile(File file, String fileName) {
		FileInputStream fis = null;
		try {
			NameValuePair[] meta_list = null;
			fis = new FileInputStream(file);
			byte[] file_buff = null;
			if (fis != null) {
				int len = fis.available();
				file_buff = new byte[len];
				fis.read(file_buff);
			}

			String fileid = storageClient1.upload_file1(file_buff, getFileExt(fileName), meta_list);
			return fileid;
		} catch (Exception ex) {
			logger.error("",ex);
			return null;
		}finally{
			if (fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}
	}

	/**
	 * 上传文件
	 * @param inputStream
	 * @param fileName
	 * @return
	 */
	public static String uploadFile(InputStream inputStream, String fileName) {
		try {
			NameValuePair[] meta_list = new NameValuePair[0];
			byte[] file_buff = null;
			if (inputStream != null) {
				int len = inputStream.available();
				file_buff = new byte[len];
				inputStream.read(file_buff);
			}

			String fileid = storageClient1.upload_file1(file_buff, getFileExt(fileName), meta_list);
			return fileid;
		} catch (Exception ex) {
			logger.error("",ex);
			return null;
		}finally{
			if (inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}
	}

	/**
	 * 根据组名和远程文件名来删除一个文件
	 * 
	 * @param groupName
	 *            例如 "group1" 如果不指定该值，默认为group1
	 * @param fileName
	 *            例如"M00/00/00/wKgxgk5HbLvfP86RAAAAChd9X1Y736.jpg"
	 * @return 0为成功，非0为失败，具体为错误代码
	 */
	public static int deleteFile(String groupName, String fileName) {
		try {
			int result = storageClient1.delete_file(groupName == null ? "group1" : groupName, fileName);
			return result;
		} catch (Exception ex) {
			logger.error("",ex);
			return 0;
		}
	}

	/**
	 * 根据fileId来删除一个文件（我们现在用的就是这样的方式，上传文件时直接将fileId保存在了数据库中）
	 * 
	 * @param fileId
	 *            file_id源码中的解释file_id the file id(including group name and filename);例如 group1/M00/00/00/ooYBAFM6MpmAHM91AAAEgdpiRC0012.xml
	 * @return 0为成功，非0为失败，具体为错误代码
	 */
	public static int deleteFile(String fileId) {
		try {
			int result = storageClient1.delete_file1(fileId);
			return result;
		} catch (Exception ex) {
			logger.error("",ex);
			return 0;
		}
	}

	/**
	 * 修改一个已经存在的文件
	 * 
	 * @param oldFileId
	 *            原来旧文件的fileId, file_id源码中的解释file_id the file id(including group name and filename);例如 group1/M00/00/00/ooYBAFM6MpmAHM91AAAEgdpiRC0012.xml
	 * @param file
	 *            新文件
	 * @param filePath
	 *            新文件路径
	 * @return 返回空则为失败
	 */
	public static String modifyFile(String oldFileId, File file, String filePath) {
		String fileid = null;
		try {
			// 先上传
			fileid = uploadFile(file, filePath);
			if (fileid == null) {
				return null;
			}
			// 再删除
			int delResult = deleteFile(oldFileId);
			if (delResult != 0) {
				return null;
			}
		} catch (Exception ex) {
			logger.error("",ex);
			return null;
		}
		return fileid;
	}

	/**
	 * 文件下载
	 * 
	 * @param fileId
	 * @return 返回一个流
	 */
	public static InputStream downloadFile(String fileId) {
		try {
			byte[] bytes = storageClient1.download_file1(fileId);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			return inputStream;
		} catch (Exception ex) {
			logger.error("",ex);
			return null;
		}
	}

	/**
	 * 获取文件后缀名（不带点）.
	 * 
	 * @return 如："jpg" or "".
	 */
	private static String getFileExt(String fileName) {
		if (StringUtils.isEmpty(fileName) || !fileName.contains(".")) {
			return "";
		} else {
			// 不带最后的点
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
	}
}
