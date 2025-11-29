package com.library.management.controller;

import com.library.management.dto.BorrowRecordDTO;
import com.library.management.dto.BorrowRequestDTO;
import com.library.management.dto.ReturnRequestDTO;
import com.library.management.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    public ResponseEntity<BorrowRecordDTO> borrowBook(@Valid @RequestBody BorrowRequestDTO request) {
        return new ResponseEntity<>(borrowService.borrowBook(request), HttpStatus.CREATED);
    }

    @PostMapping("/return")
    public ResponseEntity<BorrowRecordDTO> returnBook(@Valid @RequestBody ReturnRequestDTO request) {
        return ResponseEntity.ok(borrowService.returnBook(request));
    }

    @GetMapping("/borrower/{borrowerId}")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowRecordsByBorrower(@PathVariable Long borrowerId) {
        return ResponseEntity.ok(borrowService.getBorrowRecordsByBorrower(borrowerId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowRecordsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(borrowService.getBorrowRecordsByBook(bookId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecordDTO>> getOverdueRecords() {
        return ResponseEntity.ok(borrowService.getOverdueRecords());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BorrowRecordDTO>> getActiveBorrows() {
        return ResponseEntity.ok(borrowService.getActiveBorrows());
    }

    @PatchMapping("/{recordId}/mark-lost")
    public ResponseEntity<BorrowRecordDTO> markAsLost(@PathVariable Long recordId) {
        return ResponseEntity.ok(borrowService.markAsLost(recordId));
    }
}
