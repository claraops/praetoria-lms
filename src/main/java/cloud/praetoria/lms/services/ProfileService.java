package cloud.praetoria.lms.services;

import cloud.praetoria.lms.dtos.PasswordUpdateRequest;
import cloud.praetoria.lms.dtos.ProfileUpdateRequest;
import cloud.praetoria.lms.dtos.UserResponse;

public interface ProfileService {

    UserResponse getMyProfile(Long userId);

    UserResponse updateMyProfile(Long userId, ProfileUpdateRequest request);

    void updateMyPassword(Long userId, PasswordUpdateRequest request);
}