package com.heritage.platform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateContributorApplicationRequest(
        @NotBlank(message = "申请理由不能为空")
        String applicationReason
) {
}