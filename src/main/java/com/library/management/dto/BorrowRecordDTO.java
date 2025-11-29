package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordDTO {

    private Long id;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Borrower ID is required")
    private Long borrowerId;

    private LocalDate borrowDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private String status;

    private Double fineAmount;

    private String notes;

    // Additional fields for response
    private String bookTitle;

    private String borrowerName;
}
