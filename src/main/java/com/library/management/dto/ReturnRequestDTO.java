package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {

    @NotNull(message = "Record ID is required")
    private Long recordId;

    private String notes;
}
