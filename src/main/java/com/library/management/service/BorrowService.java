package com.library.management.service;

import com.library.management.dto.BorrowRecordDTO;
import com.library.management.dto.BorrowRequestDTO;
import com.library.management.dto.ReturnRequestDTO;
import com.library.management.entity.Book;
import com.library.management.entity.Borrower;
import com.library.management.entity.BorrowRecord;
import com.library.management.exception.*;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowerRepository;
import com.library.management.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BookService bookService;

    private static final int MAX_BOOKS_PER_BORROWER = 5;
    private static final double FINE_PER_DAY = 2.0;

    @Transactional
    public BorrowRecordDTO borrowBook(BorrowRequestDTO request) {
        // Fetch book and borrower
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + request.getBorrowerId()));

        // Validate borrower is active
        if (!borrower.getIsActive()) {
            throw new BorrowerNotActiveException("Borrower account is not active");
        }

        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book is currently not available");
        }

        // Check borrower's active borrow count
        Long activeBorrows = borrowRecordRepository.countActiveBorrowsByBorrowerId(borrower.getId());
        if (activeBorrows >= MAX_BOOKS_PER_BORROWER) {
            throw new InvalidOperationException("Borrower has reached maximum borrow limit of " + MAX_BOOKS_PER_BORROWER + " books");
        }

        // Create borrow record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(request.getBorrowDays());

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .borrower(borrower)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(BorrowRecord.BorrowStatus.BORROWED)
                .fineAmount(0.0)
                .build();

        // Decrement available copies
        bookService.decrementAvailableCopies(book.getId());

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return convertToDTO(savedRecord);
    }

    @Transactional
    public BorrowRecordDTO returnBook(ReturnRequestDTO request) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + request.getRecordId()));

        // Validate record is in BORROWED status
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new InvalidOperationException("Book has already been returned or marked as lost");
        }

        LocalDate returnDate = LocalDate.now();
        borrowRecord.setReturnDate(returnDate);

        // Calculate fine if overdue
        if (returnDate.isAfter(borrowRecord.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), returnDate);
            double fine = daysOverdue * FINE_PER_DAY;
            borrowRecord.setFineAmount(fine);
            borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        } else {
            borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        }

        if (request.getNotes() != null) {
            borrowRecord.setNotes(request.getNotes());
        }

        // Increment available copies
        bookService.incrementAvailableCopies(borrowRecord.getBook().getId());

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        return convertToDTO(updatedRecord);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getBorrowRecordsByBorrower(Long borrowerId) {
        return borrowRecordRepository.findByBorrowerId(borrowerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getBorrowRecordsByBook(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(LocalDate.now());

        // Update status to OVERDUE if not already
        overdueRecords.forEach(record -> {
            if (record.getStatus() == BorrowRecord.BorrowStatus.BORROWED) {
                record.setStatus(BorrowRecord.BorrowStatus.OVERDUE);
                borrowRecordRepository.save(record);
            }
        });

        return overdueRecords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getActiveBorrows() {
        return borrowRecordRepository.findByStatus(BorrowRecord.BorrowStatus.BORROWED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BorrowRecordDTO markAsLost(Long recordId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + recordId));

        if (borrowRecord.getStatus() == BorrowRecord.BorrowStatus.RETURNED) {
            throw new InvalidOperationException("Cannot mark returned book as lost");
        }

        borrowRecord.setStatus(BorrowRecord.BorrowStatus.LOST);
        borrowRecord.setReturnDate(LocalDate.now());

        // Apply heavy fine for lost book
        borrowRecord.setFineAmount(100.0);

        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
        return convertToDTO(updatedRecord);
    }

    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .borrowerId(record.getBorrower().getId())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus().name())
                .fineAmount(record.getFineAmount())
                .notes(record.getNotes())
                .bookTitle(record.getBook().getTitle())
                .borrowerName(record.getBorrower().getName())
                .build();
    }
}