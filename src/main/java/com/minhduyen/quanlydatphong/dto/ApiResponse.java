package com.minhduyen.quanlydatphong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Annotation này của Jackson, yêu cầu không serialize (chuyển thành JSON) các trường có giá trị null.
// Rất hữu ích để response JSON gọn gàng hơn.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // Mã trạng thái, ví dụ: 200 (thành công), 404 (không tìm thấy), 500 (lỗi server)
    private int statusCode;

    // Thông điệp mô tả kết quả
    private String message;

    // Dữ liệu trả về (có thể là bất kỳ kiểu gì)
    private T data;

}