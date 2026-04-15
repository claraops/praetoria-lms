package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Long timestamp;
    
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Succès");
    }
    
    public static <T> ApiResponse<T> successMessage(String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(null)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static ApiResponse<Void> successVoid(String message) {
        return ApiResponse.<Void>builder()
            .success(true)
            .message(message)
            .data(null)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}