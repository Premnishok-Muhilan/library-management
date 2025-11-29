package com.library.management.controller;

import com.library.management.dto.BorrowerDTO;
import com.library.management.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    public ResponseEntity<BorrowerDTO> createBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) {
        return new ResponseEntity<>(borrowerService.createBorrower(borrowerDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerDTO> getBorrowerById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowerService.getBorrowerById(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowerDTO>> getAllBorrowers() {
        return ResponseEntity.ok(borrowerService.getAllBorrowers());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BorrowerDTO>> getActiveBorrowers() {
        return ResponseEntity.ok(borrowerService.getActiveBorrowers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowerDTO> updateBorrower(@PathVariable Long id, @Valid @RequestBody BorrowerDTO borrowerDTO) {
        return ResponseEntity.ok(borrowerService.updateBorrower(id, borrowerDTO));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBorrower(@PathVariable Long id) {
        borrowerService.deactivateBorrower(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateBorrower(@PathVariable Long id) {
        borrowerService.activateBorrower(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        borrowerService.deleteBorrower(id);
        return ResponseEntity.noContent().build();
    }
}
