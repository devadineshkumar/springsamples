package com.leader_only.bean_initializer.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyLeaderOnlyBean {
    public MyLeaderOnlyBean() {
        log.info("Leader-only bean initialized!");
    }
}