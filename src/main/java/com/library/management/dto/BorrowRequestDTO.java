package com.library.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestDTO {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Borrower ID is required")
    private Long borrowerId;

    @Min(value = 1, message = "Borrow days must be at least 1")
    @Max(value = 90, message = "Borrow days cannot exceed 90")
    private Integer borrowDays = 14;  // Default = 14 days
}
