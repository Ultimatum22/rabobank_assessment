package com.ittopdogs.rabobank.batch;

import com.ittopdogs.rabobank.RabobankApplication;
import com.ittopdogs.rabobank.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.math.BigDecimal;

@Slf4j
@RunWith(SpringRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class})
@ContextConfiguration(classes = {RabobankApplication.class, BatchTestConfiguration.class})
public class ValidateTransactionsProcessorTest {

    @Autowired
    private ValidateTransactionsProcessor processor;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testSuccess() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setReference("123");
        transaction.setStartBalance(BigDecimal.valueOf(1.00));
        transaction.setEndBalance(BigDecimal.valueOf(2.00));
        transaction.setMutation(BigDecimal.valueOf(1.00));
        transaction.setDescription("Transaction #1");
        transaction.setAccountNumber("NL46INB125874124");

        Assert.assertNull(processor.process(transaction));
    }

    @Test
    public void testSuccessNegativeMutation() {
        Transaction transaction = new Transaction();
        transaction.setId(2);
        transaction.setReference("148");
        transaction.setStartBalance(BigDecimal.valueOf(4.00));
        transaction.setEndBalance(BigDecimal.valueOf(2.25));
        transaction.setMutation(BigDecimal.valueOf(-1.75));
        transaction.setDescription("Transaction #2");
        transaction.setAccountNumber("NL46INB125874124");

        Assert.assertNull(processor.process(transaction));
    }

    @Test
    public void testValidateBalanceIncorrect() {
        Transaction transaction = new Transaction();
        transaction.setId(3);
        transaction.setReference("587");
        transaction.setStartBalance(BigDecimal.valueOf(1.00));
        transaction.setEndBalance(BigDecimal.valueOf(1.50));
        transaction.setMutation(BigDecimal.valueOf(2.00));
        transaction.setDescription("Transaction #3");
        transaction.setAccountNumber("NL46INB125874124");

        Transaction result = processor.process(transaction);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction, result);
    }

    @Test
    public void testValidateDuplicateReference() {
        Transaction transaction = new Transaction();
        transaction.setId(4);
        transaction.setReference("987");
        transaction.setStartBalance(BigDecimal.valueOf(5.00));
        transaction.setEndBalance(BigDecimal.valueOf(5.98));
        transaction.setMutation(BigDecimal.valueOf(0.98));
        transaction.setDescription("Transaction #4");
        transaction.setAccountNumber("NL46INB125874124");
        processor.process(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setId(5);
        transaction2.setReference("987");
        transaction2.setStartBalance(BigDecimal.valueOf(7.00));
        transaction2.setEndBalance(BigDecimal.valueOf(10));
        transaction2.setMutation(BigDecimal.valueOf(3.00));
        transaction2.setDescription("Transaction #5");
        transaction2.setAccountNumber("NL46INB125874124");

        Transaction result = processor.process(transaction2);
        Assert.assertEquals(transaction2, result);
    }

    @Test
    public void testValidateDuplicateReferenceAndBalanceIncorrect() {
        Transaction transaction = new Transaction();
        transaction.setId(6);
        transaction.setReference("45674");
        transaction.setStartBalance(BigDecimal.valueOf(5.00));
        transaction.setEndBalance(BigDecimal.valueOf(5.98));
        transaction.setMutation(BigDecimal.valueOf(0.98));
        transaction.setDescription("Transaction #6");
        transaction.setAccountNumber("NL46INB125874124");
        processor.process(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setId(7);
        transaction2.setReference("45674");
        transaction2.setStartBalance(BigDecimal.valueOf(7.00));
        transaction2.setEndBalance(BigDecimal.valueOf(9));
        transaction2.setMutation(BigDecimal.valueOf(3.00));
        transaction2.setDescription("Transaction #7");
        transaction2.setAccountNumber("NL46INB125874124");

        Transaction result = processor.process(transaction2);
        Assert.assertEquals(transaction2, result);
    }
}
