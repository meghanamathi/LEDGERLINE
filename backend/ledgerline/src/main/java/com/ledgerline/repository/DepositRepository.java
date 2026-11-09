
package com.ledgerline.repository;

import com.ledgerline.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DepositRepository
        extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findByIdempotencyKey(String idempotencyKey);

    List<Deposit> findByAccountId(Long accountId);
}
