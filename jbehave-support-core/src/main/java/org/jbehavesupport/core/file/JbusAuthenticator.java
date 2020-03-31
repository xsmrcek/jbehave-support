package org.jbehavesupport.core.file;

import java.net.PasswordAuthentication;

public class JbusAuthenticator extends java.net.Authenticator {
    private static String authenticatorUsername = "";
    private static String authenticatorPassword = "";

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication (JbusAuthenticator.authenticatorUsername,
            JbusAuthenticator.authenticatorPassword.toCharArray());
    }

    public static void setPasswordAuthentication(String username, String password) {
        JbusAuthenticator.authenticatorUsername = username;
        JbusAuthenticator.authenticatorPassword = password;
    }
}
