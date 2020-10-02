package com.ittopdogs.rabobank.controllers;

import com.ittopdogs.rabobank.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final JdbcTemplate jdbcTemplate;

    @SuppressWarnings("SqlResolve") // Because no datasource is specified the query will throw errors, but there is nothing wrong with the code
    @ResponseBody
    @RequestMapping(value = "/processed",method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String  getProcessedTransaction() {
        List<Transaction> results = jdbcTemplate.query("SELECT id, reference, account_number, description, start_balance, mutation, end_balance FROM transactions_processed", rowMapper());
        return printTable("Processed transactions", results);
    }

    @SuppressWarnings("SqlResolve") // Because no datasource is specified the query will throw errors, but there is nothing wrong with the code
    @RequestMapping(value = "/failed",method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getFailedTransaction() {
        List<Transaction> results = jdbcTemplate.query("SELECT id, reference, account_number, description, start_balance, mutation, end_balance FROM transactions_failed", rowMapper());

        return printTable("Failed transactions", results);
    }

    private RowMapper<Transaction> rowMapper() {
        return (resultSet, row) -> new Transaction(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getBigDecimal(5),
                resultSet.getBigDecimal(6),
                resultSet.getBigDecimal(7)
        );
    }

    private String printTable(String headerText, List<Transaction> results) {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();

        sb.append("+-----------+----------------------------------------------------+").append(lineSeparator);
        sb.append(String.format("| %-62s |", headerText)).append(lineSeparator);
        sb.append("+-----------+----------------------------------------------------+").append(lineSeparator);
        sb.append("| Reference | Description                                        |").append(lineSeparator);
        sb.append("+-----------+----------------------------------------------------+").append(lineSeparator);
        results.forEach(recordDto -> sb.append(String.format("| %-9s | %-50s |", recordDto.getReference(), recordDto.getDescription())).append(lineSeparator));
        sb.append("+-----------+----------------------------------------------------+").append(lineSeparator);

        return sb.toString();
    }
}


