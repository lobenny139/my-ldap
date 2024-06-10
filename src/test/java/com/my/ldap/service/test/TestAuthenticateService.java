package com.my.ldap.service.test;

import com.my.ldap.service.IAuthenticateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.my.ldap.test.TestApplication.class)
@TestPropertySource(locations = "/test-application.properties")
@Rollback(value = false)
public class TestAuthenticateService {

    @Autowired(required = true)
    private IAuthenticateService service;

    @Test
    public void test(){
        String userName = "bennylo";// ElsaWin
        String password = "your pass";// elsa@WIN
        System.out.println(service.authenticateUser(userName, password));
    }
}
