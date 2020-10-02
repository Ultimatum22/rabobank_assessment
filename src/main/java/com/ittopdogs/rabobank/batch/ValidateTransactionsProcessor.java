package com.ittopdogs.rabobank.batch;

import com.ittopdogs.rabobank.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ValidateTransactionsProcessor implements ItemProcessor<Transaction, Transaction> {

    private final Set<String> processedReferences = new HashSet<>();

    /**
     * Validate record:
     * - Has to be unique
     * - End balance should match mutation and start balance
     *
     * This process method only returns the transactions that doesn't pass validation. That way only the failed
     * transaction are written to the database.
     */
    @Override
    public Transaction process(Transaction transaction) {
        if(processedReferences.contains(transaction.getReference()) || !isEndBalanceValid(transaction)) {
            return transaction;
        }
        processedReferences.add(transaction.getReference());
        return null;
    }

    /**
     * Validate that the sum of end balance - start balance equals mutation.
     * <p>
     * Return true if valid, false if not
     */
    private boolean isEndBalanceValid(Transaction transaction) {
        BigDecimal endBalance = transaction.getStartBalance().add(transaction.getMutation());
        return endBalance.stripTrailingZeros().equals(transaction.getEndBalance().stripTrailingZeros());
    }
}
