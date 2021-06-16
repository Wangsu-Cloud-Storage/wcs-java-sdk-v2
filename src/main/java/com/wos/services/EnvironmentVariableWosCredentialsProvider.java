package com.wos.services;

import com.wos.services.internal.WosConstraint;
import com.wos.services.internal.security.BasicSecurityKey;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.model.ISecurityKey;

public class EnvironmentVariableWosCredentialsProvider implements IWosCredentialsProvider {
    volatile private BasicSecurityKey securityKey;

    @Override
    public void setSecurityKey(ISecurityKey securityKey) {
        throw new UnsupportedOperationException(
                "EnvironmentVariableWosCredentialsProvider class does not support this method");
    }

    @Override
    public ISecurityKey getSecurityKey() {
        if (securityKey == null) {
            synchronized (this) {
                if (securityKey == null) {
                    String accessKey = stringTrim(System.getenv(WosConstraint.ACCESS_KEY_ENV_VAR));
                    String secretKey = stringTrim(System.getenv(WosConstraint.SECRET_KEY_ENV_VAR));
                    String securityToken = stringTrim(System.getenv(WosConstraint.SECURITY_TOKEN_ENV_VAR));

                    ServiceUtils.asserParameterNotNull(accessKey, "access key should not be null or empty.");
                    ServiceUtils.asserParameterNotNull(secretKey, "secret key should not be null or empty.");

                    securityKey = new BasicSecurityKey(accessKey, secretKey, securityToken);
                }
            }
        }

        return securityKey;
    }

    private static String stringTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
