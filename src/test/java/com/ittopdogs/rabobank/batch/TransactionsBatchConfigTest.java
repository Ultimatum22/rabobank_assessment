package com.ittopdogs.rabobank.batch;

import com.ittopdogs.rabobank.RabobankApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RabobankApplication.class, BatchTestConfiguration.class})
public class TransactionsBatchConfigTest {

    @Autowired
    private JobLauncherTestUtils testUtils;

    @Autowired
    private TransactionsBatchConfig config;

    @Autowired
    private JobCompletionNotificationListener listener;

    @Test
    public void testEntireJob() throws Exception {
        final JobExecution result = testUtils.getJobLauncher().run(config.transactionBatchJob(listener), testUtils.getUniqueJobParameters());
        Assert.assertNotNull(result);
        Assert.assertEquals(BatchStatus.COMPLETED, result.getStatus());
    }

    @Test
    public void testProcessFilesStep() {
        Assert.assertEquals(BatchStatus.COMPLETED, testUtils.launchStep("processFilesStep").getStatus());
    }

    @Test
    public void validateRecordsStep() {
        Assert.assertEquals(BatchStatus.COMPLETED, testUtils.launchStep("validateRecordsStep").getStatus());
    }
}