package com.heritage.platform.service;

import com.heritage.platform.common.BadRequestException;
import com.heritage.platform.dto.response.UploadResponse;
import jakarta.annotation.PostConstruct;
import com.heritage.platform.service.AuthContextService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private final AuthContextService authContextService;

    public UploadService(AuthContextService authContextService) {
        this.authContextService = authContextService;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(uploadDir));
    }

    public UploadResponse uploadImage(MultipartFile file) {
        authContextService.requireContributor();
        if (file.isEmpty()) {
            throw new BadRequestException("上传文件不能为空");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BadRequestException("只允许上传图片文件");
        }

        String extension = extractExtension(file.getOriginalFilename());
        String generatedName = UUID.randomUUID() + extension;
        Path target = Path.of(uploadDir).resolve(generatedName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("图片保存失败: " + ex.getMessage());
        }

        return new UploadResponse(generatedName, "/uploads/" + generatedName);
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
