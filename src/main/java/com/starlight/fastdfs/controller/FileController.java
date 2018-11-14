package com.starlight.fastdfs.controller;

import com.starlight.fastdfs.service.FileService;
import com.starlight.fastdfs.util.FastDFSClient;
import com.starlight.fastdfs.util.Result;
import com.starlight.fastdfs.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xghuang
 * @date 2018/11/14
 * @time 10:35
 * @desc:
 */
@RestController
@RequestMapping("/file/mgn/")
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Result handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                return fileService.upload(file);
            } catch (Exception e) {
                e.printStackTrace();
                ResultUtil.getResultError();
            }
        }
        return ResultUtil.getResultSuccess();
    }
}
