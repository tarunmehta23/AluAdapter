package com.charter.provisioning.voice.commercial.alu;

import com.charter.health.HealthStatServlet;
import com.charter.health.HealthStatServletBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.charter.provisioning.voice.commercial.alu.config.DestroyActiveSession;
import com.charter.provisioning.voice.commercial.alu.config.InitializeSession;

@EnableAsync
@SpringBootApplication
public class Application {
	
	@Autowired
	private static DestroyActiveSession destroyActiveSession;
	
	@Autowired
	private static InitializeSession  initializeSession;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        SpringApplication app = new SpringApplication(Application.class); 
        app.addListeners(initializeSession,destroyActiveSession);
    }

    @Bean
    public ServletRegistrationBean healthServlet(ApplicationContext applicationContext) {
        HealthStatServlet servlet = HealthStatServletBuilder.create("alu-adpater")
                .withManifestDetails(Application.class).build();

        return new ServletRegistrationBean(servlet, "/health");
    }


}
