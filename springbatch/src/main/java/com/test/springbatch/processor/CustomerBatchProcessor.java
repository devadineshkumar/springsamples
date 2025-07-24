package com.test.springbatch.processor;

import com.test.springbatch.modal.Customer;
import com.test.springbatch.modal.CustomerCopy;
import org.springframework.batch.item.ItemProcessor;

public class CustomerBatchProcessor implements ItemProcessor<Customer, CustomerCopy> {

    @Override
    public CustomerCopy process(Customer item) throws Exception {
        CustomerCopy customerCopy = new CustomerCopy();
        customerCopy.setName(item.getName());
        customerCopy.setAddress(item.getAddress());
        customerCopy.setContactNo(item.getContactNo());
        return customerCopy;
    }
}
