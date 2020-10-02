# Context
This application processes a provided xml and csv file with Spring Batch. This batch is configured with a scheduled task to run periodically.

# Requirements
###### Tested application on Java 11 and Java 14 and Intellij
- Java 11 or 14
- If used IntelliJ then Lombok plugin is required
- Maven (A wrapper has been provided)

Run ```mvn clean install``` within directory, to run use ```mvn spring-boot:run```

# Application working
This batch contains multiple steps
- Read both records.csv and records.xml at the same time to be able to insert into a h2 database
- Process the transaction and validate for unique reference and correct end balance

To simulate actual work the batch will trigger every 10 seconds and process both files over and over.

Two rest endpoints are available:
- view all processed transactions (http://localhost:7777/transactions/processed)
- View all failed transactions (http://localhost:7777/transactions/failed)

# Assumptions
Only two files with specific names and extensions are provided to the input folder. There are no checks if a file is present.

When a duplicate reference is found only the second record is shown in the table. This is a design choice to not show
the first record with reference (which is technically also not unique, but it was at the point is was first processed).

# Configuration
The file global.yml contains the global app configuration, for now only the directory in which the record files are stored is configurable.
Change the value of transactions.location. (This is only tested on Linux and not on Windows, but an absolute value should work)

# Extra
H2 console is accessible via http://localhost:7777/h2-console/

# Known issues
- The application crashes when the input folder is not created
- Existing data is never removed while application is running, thus adding duplicates that already exists in the database