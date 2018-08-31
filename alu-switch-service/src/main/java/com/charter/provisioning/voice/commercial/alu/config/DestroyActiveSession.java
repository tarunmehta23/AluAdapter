package com.charter.provisioning.voice.commercial.alu.config;

import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.session.SessionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

@Configuration
@Slf4j
public class DestroyActiveSession implements ApplicationListener<ContextClosedEvent> {

    private final SessionHandler sessionHandler;

    @Autowired
    public DestroyActiveSession(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            if (sessionHandler.getSessionId() != null) {
                sessionHandler.logout();
            } else {
                log.info("No Active session to terminate");
            }
        } catch (NetworkException e) {
            log.error(String.format("Failed to terminate the session %s", sessionHandler.getSessionId()));
        }
    }
}