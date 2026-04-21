package com.heritage.platform.dto.response;

import com.heritage.platform.enums.UserRole;

public record AuthResponse(
        Long id,
        String username,
        String nickname,
        UserRole role,
        String token
) {
}
