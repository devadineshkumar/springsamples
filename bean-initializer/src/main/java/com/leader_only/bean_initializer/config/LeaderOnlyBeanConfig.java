package com.leader_only.bean_initializer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.kubernetes.commons.leader.Leader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(Leader.class)
public class LeaderOnlyBeanConfig {

    private static final String DEFAULT_ROLE = "leader";

    @Autowired
    private Leader leader;

    @Bean
    public MyLeaderOnlyBean myLeaderOnlyBean() {
        if (leader != null && leader.getRole().equals(DEFAULT_ROLE)) {

            return new MyLeaderOnlyBean();
        } else {
            throw new IllegalStateException("This node is not the leader. Bean creation aborted.");
        }
    }
}