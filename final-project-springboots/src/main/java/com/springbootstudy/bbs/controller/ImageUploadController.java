package com.springbootstudy.bbs.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ImageUploadController {

    private static final String UPLOAD_DIR = "C:/upload/boardImages/";
    private static final String WEB_PATH   = "/upload/boardImages/";

    // Summernote 이미지 업로드
    @PostMapping("/summernoteImageUpload")
    @ResponseBody
    public Map<String, Object> imageUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IllegalStateException, IOException {

        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("error", "파일이 없습니다.");
            return result;
        }

        String fileName = file.getOriginalFilename();

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        File dest = new File(UPLOAD_DIR, fileName);
        if (dest.exists()) {
            fileName = System.currentTimeMillis() + "_" + fileName;
            dest = new File(UPLOAD_DIR, fileName);
        }
        file.transferTo(dest);

        String baseUrl = request.getScheme() + "://" + request.getServerName()
                       + ":" + request.getServerPort();
        result.put("url", baseUrl + WEB_PATH + fileName);
        return result;
    }
}
