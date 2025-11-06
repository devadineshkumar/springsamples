package com.multidb.transaction_demo.h2.repo;

import com.multidb.transaction_demo.h2.entity.H2Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface H2LogRepository extends JpaRepository<H2Log, Long> {
}

