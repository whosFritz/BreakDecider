package com.whosfritz.breakdecider;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("break-decider")
@PWA(name = "Break Decider", shortName = "Break Decider", description = "Voting Web App")
public class BreakDeciderApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(BreakDeciderApplication.class, args);
    }

}
