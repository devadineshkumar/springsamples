package com.test.springbatch.listener;

import com.test.springbatch.modal.Customer;
import com.test.springbatch.modal.CustomerCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * Listener to react to skipped items during a batch step.
 * Implements SkipListener<Customer, CustomerCopy> so it can be registered with a chunk-oriented step.
 *
 * You can register it as:
 *  .faultTolerant()
 *  .skip(Exception.class)
 *  .skipLimit(100)
 *  .listener(new MySkipListener())
 *
 * or register it as a Spring bean and reference it from your config.
 */
@Component
public class MySkipListener implements SkipListener<Customer, CustomerCopy> {

    private static final Logger log = LoggerFactory.getLogger(MySkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        // Called when an exception occurs during reading that results in a skip
        log.warn("onSkipInRead - exception while reading, skipping record: {}", t.toString());
    }

    @Override
    public void onSkipInProcess(Customer item, Throwable t) {
        // Called when an exception occurs during processing a specific item
        log.warn("onSkipInProcess - skipping item={} due to {}", item, t.toString());
        // You can persist the item to a "dead-letter" table here for later inspection
    }

    @Override
    public void onSkipInWrite(CustomerCopy item, Throwable t) {
        // Called when an exception occurs during writing a specific item
        log.warn("onSkipInWrite - skipping item={} due to {}", item, t.toString());
        // You might save the failed write record to a recovery store here
    }
}

