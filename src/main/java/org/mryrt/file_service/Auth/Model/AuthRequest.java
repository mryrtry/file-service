package org.mryrt.file_service.Auth.Model;

/**
 * @param username никнейм пользователя.
 * @param password пароль от профиля пользователя.
 */
public record AuthRequest(String username, String password) {}
