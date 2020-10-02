package com.ittopdogs.rabobank.batch;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.util.List;

/**
 * Takes a list of item readers, and returns the output of every reader.
 * <p>
 * All readers need to return the same object, but the item readers can have different implementation (e.g. csv and
 * xml reader).
 *
 * @param <T>
 */
@Slf4j
class CompositeItemReader<T> implements ItemReader<T>, ItemStream {
    private int delegateIndex;
    private ItemReader<T> currentDelegate;
    private ExecutionContext stepExecutionContext;
    @Setter
    private List<AbstractItemStreamItemReader<T>> delegates;

    @BeforeStep
    private void beforeStep(StepExecution stepExecution) {
        stepExecutionContext = stepExecution.getExecutionContext();
    }

    public T read() throws Exception {
        T item = null;
        if (currentDelegate != null) {
            item = currentDelegate.read();

            if (item == null) {
                ((ItemStream) currentDelegate).close();
                currentDelegate = null;
            }
        }

        // When previous delegate is done (item equals null), increment index and read data from next delegate
        if (item == null && delegateIndex < delegates.size()) {
            currentDelegate = delegates.get(delegateIndex++);
            ((ItemStream) currentDelegate).open(stepExecutionContext);
            update(stepExecutionContext);
            item = read();
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        if (currentDelegate != null) {
            ((ItemStream) currentDelegate).close();
        }
    }
}
