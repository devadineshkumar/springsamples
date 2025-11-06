package com.multidb.transaction_demo.service;

import com.multidb.transaction_demo.h2.entity.H2Log;
import com.multidb.transaction_demo.h2.repo.H2LogRepository;
import com.multidb.transaction_demo.mysql.entity.MysqlPerson;
import com.multidb.transaction_demo.mysql.repo.MysqlPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoService {

    private final MysqlPersonRepository mysqlRepo;
    private final H2LogRepository h2Repo;

    @Autowired
    public DemoService(MysqlPersonRepository mysqlRepo, H2LogRepository h2Repo) {
        this.mysqlRepo = mysqlRepo;
        this.h2Repo = h2Repo;
    }

    @Transactional
    public MysqlPerson savePersonToMysql(String name) {
        return mysqlRepo.save(new MysqlPerson(name));
    }

    @Transactional
    public H2Log saveLogToH2(String message) {
        return h2Repo.save(new H2Log(message));
    }

    // run both operations inside a single global JTA transaction
    @Transactional
    public void savePersonAndLog(String name, String message) {
        mysqlRepo.save(new MysqlPerson(name));
        h2Repo.save(new H2Log(message));
    }

    // helper to force rollback for testing
    @Transactional
    public void savePersonAndLogWithFailure(String name, String message) {
        mysqlRepo.save(new MysqlPerson(name));
        h2Repo.save(new H2Log(message));
        throw new RuntimeException("forced failure to test rollback");
    }
}
