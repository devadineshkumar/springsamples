package com.test.springbatch.reader;

import com.test.springbatch.modal.CustomerDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.stereotype.Component;


public class CustomReader extends JdbcPagingItemReader<CustomerDto> {

    @Override
    public CustomerDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {



        return null;
    }
}
