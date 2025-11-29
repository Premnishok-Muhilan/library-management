package com.library.management.repository;

import com.library.management.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

    Optional<Borrower> findByEmail(String email);

    Optional<Borrower> findByMembershipId(String membershipId);

    List<Borrower> findByIsActive(Boolean isActive);
}
