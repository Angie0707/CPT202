package com.heritage.platform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "评论内容不能为空")
        String content
) {
}
