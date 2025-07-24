package com.test.springbatch.config;

import com.test.springbatch.modal.Customer;
import com.test.springbatch.modal.CustomerCopy;
import com.test.springbatch.processor.CustomerBatchProcessor;
import com.test.springbatch.repo.CustomerRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {

    private EntityManagerFactory entityManagerFactory;
    private JobRepository jobRepository;
    private PlatformTransactionManager platformTransactionManager;
    private CustomerRepository customerRepository;

    @Bean
    public RepositoryItemReader<Customer> batchItemReader() {
        RepositoryItemReader<Customer> reader = new RepositoryItemReader<>();
        reader.setRepository(customerRepository);
        reader.setPageSize(20);
        reader.setMethodName("findAll");
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }


    @Bean
    public JpaItemWriter<CustomerCopy> batchItemWriter() {
        return new JpaItemWriterBuilder<CustomerCopy>().entityManagerFactory(entityManagerFactory)
                        .usePersist(true).build();
    }

    @Bean
    public CustomerBatchProcessor processor() {
        return new CustomerBatchProcessor();
    }

    @Bean
    public Step customerStep() {
        return new StepBuilder("customerStep", jobRepository).<Customer, CustomerCopy>chunk(10, platformTransactionManager)
                .reader(batchItemReader())
                .processor(processor())
                .writer(batchItemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("customerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerStep())
                .build();
    }


}
