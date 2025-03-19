package com.evangeliakostop.paymentsystem.persistence;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class PaymentsDBAccess {

    private final JdbcTemplate paymentsDbTemplate;


    @Autowired
    public PaymentsDBAccess(JdbcTemplate paymentsDbTemplate) {
        this.paymentsDbTemplate = paymentsDbTemplate;
    }

    public void insertInitTransaction(String transactionId, TransactionType transactionType, Long amount, String currency) {
        log.info("Method insertInitTransaction entered for transactionId: {}", transactionId);

        try {
            paymentsDbTemplate.update(SqlStatements.INSERT_TRANSACTION, transactionId, PaymentStatus.INITIALIZED.getDescription(), transactionType.getDescription(), amount, transactionType.equals(TransactionType.REFUND) ? amount : 0.0, currency);
        } catch (DataAccessException e) {
            log.error("Method insertInitTransaction - Exception", e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("Method insertInitTransaction exiting successfully for transactionId: {}", transactionId);
    }
}
