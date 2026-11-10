
package com.ledgerline.repository;

import com.ledgerline.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    // Find accounts belonging to a specific user
    List<Account> findByOwnerEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM Account a
        WHERE a.accountNumber = :accountNumber
    """)
    Optional<Account> findByAccountNumberForUpdate(
        @Param("accountNumber") String accountNumber
    );
}
