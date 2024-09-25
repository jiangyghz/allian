package com.bond.allianz.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * 配置代理授权
 */
public class BasicAuthenticator extends Authenticator {
    String userName;
    String password;

    public BasicAuthenticator(String userName,String password){
        this.userName=userName;
        this.password=password;
    }
    @Override
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName,password.toCharArray());
    }
}
