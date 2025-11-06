package com.multidb.transaction_demo.mysql.repo;

import com.multidb.transaction_demo.mysql.entity.MysqlPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MysqlPersonRepository extends JpaRepository<MysqlPerson, Long> {
}

