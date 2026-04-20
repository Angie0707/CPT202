package com.heritage.platform.controller;

import com.heritage.platform.common.ApiResponse;
import com.heritage.platform.dto.request.AdminUserRoleUpdateRequest;
import com.heritage.platform.dto.response.AdminUserSummaryResponse;
import com.heritage.platform.enums.UserRole;
import com.heritage.platform.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserSummaryResponse>> listUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) UserRole role
    ) {
        return ApiResponse.success(adminUserService.listUsers(username, role));
    }

    @PostMapping("/{userId}/role")
    public ApiResponse<AdminUserSummaryResponse> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequest request
    ) {
        return ApiResponse.success("用户角色更新成功", adminUserService.updateRole(userId, request));
    }

    @PostMapping("/{userId}/activate")
    public ApiResponse<AdminUserSummaryResponse> activate(@PathVariable Long userId) {
        return ApiResponse.success("用户已启用", adminUserService.activate(userId));
    }

    @PostMapping("/{userId}/deactivate")
    public ApiResponse<AdminUserSummaryResponse> deactivate(@PathVariable Long userId) {
        return ApiResponse.success("用户已停用", adminUserService.deactivate(userId));
    }
}
