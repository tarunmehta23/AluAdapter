package com.charter.provisioning.voice.commercial.alu.config;

import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.session.SessionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
@Slf4j
public class InitializeSession implements ApplicationListener<ContextRefreshedEvent> {

    private final SessionHandler sessionHandler;

    @Autowired
    public InitializeSession(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info("event started");
            sessionHandler.establishSession("INITIALIZE_SESSION");
        } catch (ProvisioningServiceException | NetworkException e) {
            log.error("Failed to Initialize the session " + e.getMessage());
        }
    }
}