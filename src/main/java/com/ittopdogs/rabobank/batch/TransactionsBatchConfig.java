package com.ittopdogs.rabobank.batch;

import com.ittopdogs.rabobank.config.GlobalProperties;
import com.ittopdogs.rabobank.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TransactionsBatchConfig {
    private final DataSource dataSource;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final GlobalProperties globalProperties;

    @Bean
    public Job transactionBatchJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("transactionBatchJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(processFilesStep())
                .next(validateRecordsStep())
                .build();
    }

    @Bean
    public Step processFilesStep() {
        return stepBuilderFactory.get("processFilesStep")
                .<Transaction, Transaction>chunk(1)
                .reader(processAllFilesReader())
                .writer(processAllFilesWriter())
                .build();
    }

    @Bean
    public Step validateRecordsStep() {
        return stepBuilderFactory.get("validateRecordsStep")
                .<Transaction, Transaction>chunk(1)
                .reader(validateRecordsReader())
                .processor(validateRecordsProcessor())
                .writer(validateRecordsWriter())
                .build();
    }

    @Bean
    public CompositeItemReader<Transaction> processAllFilesReader() {
        CompositeItemReader<Transaction> reader = new CompositeItemReader<>();
        reader.setDelegates(Arrays.asList(csvRecordReader(), xmlRecordReader()));
        return reader;
    }

    @Bean
    public FlatFileItemReader<Transaction> csvRecordReader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("csvRecordReader")
                .resource(new PathResource(globalProperties.getTransactionsLocation() + "/records.csv"))
                .delimited().names("reference", "accountNumber", "description", "startBalance", "mutation", "endBalance")
                .linesToSkip(1)
                .targetType(Transaction.class)
                .strict(false)
                .build();
    }

    @Bean
    public StaxEventItemReader<Transaction> xmlRecordReader() {
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("xmlRecordReader")
                .resource(new PathResource(globalProperties.getTransactionsLocation() + "/records.xml"))
                .addFragmentRootElements("record")
                .unmarshaller(recordUnmarshaller())
                .strict(false)
                .build();
    }

    @Bean
    public Jaxb2Marshaller recordUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Transaction.class);

        return marshaller;
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> processAllFilesWriter() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO transactions_processed(reference, account_number, description, start_balance, mutation, end_balance) values(:reference, :accountNumber, :description, :startBalance, :mutation, :endBalance)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Transaction> validateRecordsReader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("validateRecordsReader")
                .sql("SELECT id, reference, account_number, description, start_balance, mutation, end_balance FROM transactions_processed")
                .rowMapper((resultSet, row) -> new Transaction(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getBigDecimal(5),
                        resultSet.getBigDecimal(6),
                        resultSet.getBigDecimal(7)
                ))
                .dataSource(dataSource)
                .build();
    }

    @Bean
    ValidateTransactionsProcessor validateRecordsProcessor() {
        return new ValidateTransactionsProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> validateRecordsWriter() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO transactions_failed(reference, account_number, description, start_balance, mutation, end_balance) values(:reference, :accountNumber, :description, :startBalance, :mutation, :endBalance)")
                .dataSource(dataSource)
                .build();
    }
}