package com.library.management.service;

import com.library.management.dto.BorrowerDTO;
import com.library.management.entity.Borrower;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Transactional
    public BorrowerDTO createBorrower(BorrowerDTO borrowerDTO) {
        // Check if email already exists
        if (borrowerRepository.findByEmail(borrowerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Borrower with email " + borrowerDTO.getEmail() + " already exists");
        }

        Borrower borrower = Borrower.builder()
                .name(borrowerDTO.getName())
                .email(borrowerDTO.getEmail())
                .phone(borrowerDTO.getPhone())
                .membershipId(generateMembershipId())
                .membershipType(borrowerDTO.getMembershipType() != null ?
                        Borrower.MembershipType.valueOf(borrowerDTO.getMembershipType()) :
                        Borrower.MembershipType.REGULAR)
                .isActive(true)
                .build();

        Borrower savedBorrower = borrowerRepository.save(borrower);
        return convertToDTO(savedBorrower);
    }

    @Transactional(readOnly = true)
    public BorrowerDTO getBorrowerById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
        return convertToDTO(borrower);
    }

    @Transactional(readOnly = true)
    public List<BorrowerDTO> getAllBorrowers() {
        return borrowerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowerDTO> getActiveBorrowers() {
        return borrowerRepository.findByIsActive(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BorrowerDTO updateBorrower(Long id, BorrowerDTO borrowerDTO) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!borrower.getEmail().equals(borrowerDTO.getEmail())) {
            if (borrowerRepository.findByEmail(borrowerDTO.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Borrower with email " + borrowerDTO.getEmail() + " already exists");
            }
        }

        borrower.setName(borrowerDTO.getName());
        borrower.setEmail(borrowerDTO.getEmail());
        borrower.setPhone(borrowerDTO.getPhone());

        if (borrowerDTO.getMembershipType() != null) {
            borrower.setMembershipType(Borrower.MembershipType.valueOf(borrowerDTO.getMembershipType()));
        }

        if (borrowerDTO.getIsActive() != null) {
            borrower.setIsActive(borrowerDTO.getIsActive());
        }

        Borrower updatedBorrower = borrowerRepository.save(borrower);
        return convertToDTO(updatedBorrower);
    }

    @Transactional
    public void deactivateBorrower(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
        borrower.setIsActive(false);
        borrowerRepository.save(borrower);
    }

    @Transactional
    public void activateBorrower(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
        borrower.setIsActive(true);
        borrowerRepository.save(borrower);
    }

    @Transactional
    public void deleteBorrower(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
        borrowerRepository.delete(borrower);
    }

    private String generateMembershipId() {
        return "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BorrowerDTO convertToDTO(Borrower borrower) {
        return BorrowerDTO.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .phone(borrower.getPhone())
                .membershipId(borrower.getMembershipId())
                .membershipType(borrower.getMembershipType().name())
                .isActive(borrower.getIsActive())
                .build();
    }
}