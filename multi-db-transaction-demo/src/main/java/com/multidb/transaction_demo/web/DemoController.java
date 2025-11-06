package com.multidb.transaction_demo.web;

import com.multidb.transaction_demo.h2.repo.H2LogRepository;
import com.multidb.transaction_demo.mysql.repo.MysqlPersonRepository;
import com.multidb.transaction_demo.service.DemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final DemoService demoService;
    private final MysqlPersonRepository mysqlRepo;
    private final H2LogRepository h2Repo;

    public DemoController(DemoService demoService, MysqlPersonRepository mysqlRepo, H2LogRepository h2Repo) {
        this.demoService = demoService;
        this.mysqlRepo = mysqlRepo;
        this.h2Repo = h2Repo;
    }

    @GetMapping("/demo/combined")
    public ResponseEntity<?> combined(@RequestParam String name,
                                      @RequestParam String message,
                                      @RequestParam(defaultValue = "false") boolean fail) {
        try {
            if (fail) {
                demoService.savePersonAndLogWithFailure(name, message);
            } else {
                demoService.savePersonAndLog(name, message);
            }
            return ResponseEntity.ok("Committed both databases");
        } catch (Exception e) {
            // after rollback, return counts to show state
            long mysqlCount = mysqlRepo.count();
            long h2Count = h2Repo.count();
            return ResponseEntity.status(500).body("Operation failed: " + e.getMessage() + "; mysqlCount=" + mysqlCount + ", h2Count=" + h2Count);
        }
    }
}

