package com.starlight.fastdfs.service;

import com.starlight.fastdfs.util.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xghuang
 * @date 2018/11/14
 * @time 10:36
 * @desc:
 */
public interface FileService {

    Result upload(MultipartFile file) throws Exception;
}
