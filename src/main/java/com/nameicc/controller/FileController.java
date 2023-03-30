package com.nameicc.controller;

import com.nameicc.util.FileUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileUtil fileUtil;

    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file) {
        return fileUtil.upload(file);
    }

    @GetMapping("/getDownloadUrl")
    public String getDownloadUrl(@RequestParam("key") String key) {
        return fileUtil.getDownloadUrl(key);
    }

}
