package com.starlight.fastdfs.service.impl;

import com.starlight.fastdfs.service.FileService;
import com.starlight.fastdfs.util.FastDFSClient;
import com.starlight.fastdfs.util.Result;
import com.starlight.fastdfs.util.ResultUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xghuang
 * @date 2018/11/14
 * @time 10:36
 * @desc:
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public Result upload(MultipartFile file) throws Exception {
        Object fileId = FastDFSClient.uploadFile(file.getInputStream(),file.getOriginalFilename());
        return ResultUtil.getResultSuccess(fileId);
    }
}
