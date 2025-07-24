package com.test.springbatch.repo;

import com.test.springbatch.modal.CustomerCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCopyRepository extends JpaRepository<CustomerCopy, Long> {
}
