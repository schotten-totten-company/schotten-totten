package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.controllers.LanGameManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Bastien on 19/11/2017.
 */

public class LanGameManagerTest {

    private static LanGameManager manager;

    @BeforeClass
    public static void Before() {
        manager = new LanGameManager();
        manager.start();
    }

    @AfterClass
    public static void After() {
        manager.stop();
    }

    @Test
    public void TestPing() {
        final RestTemplate rest = new RestTemplate();
        System.out.println(rest.getForObject("http://localhost:8080/ping", String.class).toString());
    }


}
