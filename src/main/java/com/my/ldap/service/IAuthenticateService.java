package com.my.ldap.service;

public interface IAuthenticateService {

    public boolean authenticateUser(String userId, String password);
}
