package com.library.management.repository;

import com.library.management.entity.BorrowRecord;
import com.library.management.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByBorrowerId(Long borrowerId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :currentDate")
    List<BorrowRecord> findOverdueRecords(LocalDate currentDate);

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.borrower.id = :borrowerId AND br.status = 'BORROWED'")
    Long countActiveBorrowsByBorrowerId(Long borrowerId);
}
