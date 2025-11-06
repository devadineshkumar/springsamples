package com.multidb.transaction_demo;

import com.multidb.transaction_demo.service.DemoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final DemoService demoService;

    public StartupRunner(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("StartupRunner: inserting demo data into MySQL and H2 (if configured)");
        try {
            var p = demoService.savePersonToMysql("Alice");
            System.out.println("Saved person to MySQL: " + p.getId() + " / " + p.getName());
        } catch (Exception e) {
            System.out.println("Could not save to MySQL: " + e.getMessage());
        }

        try {
            var l = demoService.saveLogToH2("Application started");
            System.out.println("Saved log to H2: " + l.getId() + " / " + l.getMessage());
        } catch (Exception e) {
            System.out.println("Could not save to H2: " + e.getMessage());
        }
    }
}

