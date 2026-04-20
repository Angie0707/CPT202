package com.heritage.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 150, message = "标题不能超过150个字符")
        String title,

        @NotBlank(message = "正文不能为空")
        String content,

        @NotNull(message = "分类不能为空")
        Long categoryId,

        @Size(max = 255, message = "封面链接不能超过255个字符")
        String coverImageUrl,

        @Size(max = 100, message = "文化遗产名称不能超过100个字符")
        String heritageName,

        @Size(max = 100, message = "地区不能超过100个字符")
        String region,

        List<String> imageUrls
) {
}
