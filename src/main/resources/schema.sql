DROP TABLE IF EXISTS PUBLIC.TRANSACTIONS_PROCESSED;
DROP TABLE IF EXISTS PUBLIC.TRANSACTIONS_FAILED;

CREATE TABLE PUBLIC.TRANSACTIONS_PROCESSED
(
    ID             INTEGER AUTO_INCREMENT,
    REFERENCE      VARCHAR,
    ACCOUNT_NUMBER VARCHAR,
    DESCRIPTION    VARCHAR,
    START_BALANCE  DECIMAL,
    MUTATION       DECIMAL,
    END_BALANCE    DECIMAL
);


CREATE TABLE PUBLIC.TRANSACTIONS_FAILED
(
    ID             INTEGER AUTO_INCREMENT,
    REFERENCE      VARCHAR,
    ACCOUNT_NUMBER VARCHAR,
    DESCRIPTION    VARCHAR,
    START_BALANCE  DECIMAL,
    MUTATION       DECIMAL,
    END_BALANCE    DECIMAL
);

