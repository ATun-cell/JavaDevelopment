package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    public AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> uploadFile(MultipartFile file) {
        log.info("文件上传:{}", file);
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        try {
            String path = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(path);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
        return null;
    }
}
