package gng.learning.accountapi.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IComingTransactionRepository extends JpaRepository<IncomingDebitTransaction, UUID> {

    List<IncomingDebitTransaction> findByAccountId(UUID accountId);
}
