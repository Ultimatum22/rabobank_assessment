package com.ittopdogs.rabobank.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@PropertySource("classpath:global.yml")
public class GlobalProperties {

    @Getter
    @Value("${transactions.location}")
    private Path transactionsLocation;
}
