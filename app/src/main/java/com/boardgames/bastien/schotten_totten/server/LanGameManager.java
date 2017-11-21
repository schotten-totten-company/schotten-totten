package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.controllers.AbstractGameManager;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Bastien on 19/10/2017.
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.boardgames.bastien.schotten_totten.server")
public class LanGameManager {

    private AbstractGameManager gameManager;
    private ConfigurableApplicationContext context;

    public void start() {
        //context = SpringApplication.run(LanGameManager.class);
        context.isActive();
    }

    public boolean isActive() {
        return context.isActive();
    }

    public void stop() {
        //SpringApplication.exit(context);
    }

}
