package com.leader_only.bean_initializer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaderEventLogger {

    @EventListener
    public void onGranted(OnGrantedEvent event) {
        log.info("👑 Leadership granted to this pod.");
        // You can set a flag or initialize beans here
    }

    @EventListener
    public void onRevoked(OnRevokedEvent event) {
        log.info("👋 Leadership revoked from this pod.");
    }
}