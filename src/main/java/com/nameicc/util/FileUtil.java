package com.nameicc.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class FileUtil {

    @Value("${oss.ak}")
    private String accessKey;

    @Value("${oss.sk}")
    private String secretKey;

    // 命名空间
    @Value("${oss.bucket}")
    private String bucket;

    // 外链域名
    @Value("${oss.domain}")
    private String domain;

    public String upload(MultipartFile file) {
        try {
            // 配置
            Configuration cfg = new Configuration(Region.region1());
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
            UploadManager uploadManager = new UploadManager(cfg);
            byte[] uploadBytes = file.getBytes();
            String key = file.getOriginalFilename();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(uploadBytes, key, upToken);
            // 解析结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("upload result: [{}]", new Gson().toJson(putRet));
            return putRet.key;
        } catch (QiniuException ex) {
            log.error("文件上传异常", ex);
            log.error(new Gson().toJson(ex.response));
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return null;
        }
        return null;
    }

    public String getDownloadUrl(String key) {
        try {
            DownloadUrl url = new DownloadUrl(domain, false, key);
            String urlString = url.buildURL();
            return urlString;
        } catch (Exception e) {
            log.error("获取下载地址异常", e);
        }
        return null;
    }

}
