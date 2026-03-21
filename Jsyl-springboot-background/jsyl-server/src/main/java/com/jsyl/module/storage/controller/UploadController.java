package com.jsyl.module.storage.controller;

import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.result.Result;
import com.jsyl.module.user.service.UserService;
import com.jsyl.common.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jsyl/common/upload")
@Api(tags = "文件上传接口")
@Slf4j
public class UploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/avatar")
    @ApiOperation("上传用户头像")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {

        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("图片大小不能超过2MB");
            }

            // 获取当前用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = "avatars/user_" + userId + "_" + System.currentTimeMillis() + extension;

            // 上传到OSS
            String url = aliOssUtil.upload(file.getBytes(), fileName);

            // 更新用户头像
            userService.updateAvatar(userId.intValue(), url);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);

            return Result.success(result);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/post/image")
    @ApiOperation("上传帖子图片")
    public Result<Map<String, String>> uploadPostImage(@RequestParam("file") MultipartFile file) {

        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过5MB");
            }

            // 获取当前用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = "posts/post_" + userId + "_" + System.currentTimeMillis() + extension;

            // 上传到OSS
            String url = aliOssUtil.upload(file.getBytes(), fileName);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);

            return Result.success(result);
        } catch (IOException e) {
            log.error("上传帖子图片失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/post/images")
    @ApiOperation("批量上传帖子图片")
    public Result<Map<String, Object>> uploadPostImages(@RequestParam("files") MultipartFile[] files) {

        try {
            // 验证文件数量
            if (files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 9) {
                return Result.error("最多只能上传9张图片");
            }

            // 获取当前用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId == null) {
                return Result.error("用户未登录");
            }

            List<String> urls = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();

            for (MultipartFile file : files) {
                // 验证文件
                if (file.isEmpty()) {
                    continue;
                }

                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    continue;
                }

                // 验证文件大小（5MB）
                if (file.getSize() > 5 * 1024 * 1024) {
                    continue;
                }

                // 生成文件名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String fileName = "posts/post_" + userId + "_" + System.currentTimeMillis() + "_" + urls.size() + extension;

                // 上传到OSS
                String url = aliOssUtil.upload(file.getBytes(), fileName);
                urls.add(url);
                fileNames.add(fileName);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("urls", urls);
            result.put("fileNames", fileNames);
            result.put("count", urls.size());

            return Result.success(result);
        } catch (IOException e) {
            log.error("批量上传帖子图片失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/image")
    @ApiOperation("通用图片上传")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {

        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("图片大小不能超过10MB");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = "images/img_" + System.currentTimeMillis() + extension;

            // 上传到OSS
            String url = aliOssUtil.upload(file.getBytes(), fileName);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName);

            return Result.success(result);
        } catch (IOException e) {
            log.error("通用图片上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

}
