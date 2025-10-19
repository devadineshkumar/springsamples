package com.leader_only.bean_initializer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.kubernetes.commons.leader.Leader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LeaderOnlyBeanConfig {

    private static final String DEFAULT_ROLE = "leader";

    @Autowired
    private Leader leader;

    @Bean
    public MyLeaderOnlyBean myLeaderOnlyBean() {
        log.info("Checking leadership status for bean initialization...");
        if (leader != null && leader.getRole().equals(DEFAULT_ROLE)) {
            return new MyLeaderOnlyBean();
        } else {
            log.info("This node is not the leader. Bean creation aborted.");
            throw new IllegalStateException("This node is not the leader. Bean creation aborted.");
        }
    }
}