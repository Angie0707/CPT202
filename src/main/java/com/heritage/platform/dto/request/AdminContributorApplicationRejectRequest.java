package com.heritage.platform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminContributorApplicationRejectRequest(
        @NotBlank(message = "驳回原因不能为空")
        String reason
) {
}
