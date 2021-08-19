package com.wos.services.internal.security;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import com.wos.services.BasicWosCredentialsProvider;
import com.wos.services.IWosCredentialsProvider;
import com.wos.services.internal.WosConstraint;
import com.wos.services.model.AuthTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class ProviderCredentials {
    protected static final ILogger log = LoggerBuilder.getLogger(ProviderCredentials.class);

    protected AuthTypeEnum authType;
    private static ThreadLocal<AuthTypeEnum> threadLocalAuthType;
    private IWosCredentialsProvider wosCredentialsProvider;
    private String regionName;


    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = StringUtils.isEmpty(regionName) ? WosConstraint.DEFAULT_BUCKET_LOCATION_VALUE : regionName;
    }

    public ProviderCredentials(String accessKey, String secretKey) {
        this.setWosCredentialsProvider(new BasicWosCredentialsProvider(accessKey, secretKey));
    }

    public ProviderCredentials(String accessKey, String secretKey, String securityToken) {
        this.setWosCredentialsProvider(new BasicWosCredentialsProvider(accessKey, secretKey, securityToken));
    }

    public AuthTypeEnum getAuthType() {
        return (threadLocalAuthType == null) ? authType : threadLocalAuthType.get();
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    public void setWosCredentialsProvider(IWosCredentialsProvider wosCredentialsProvider) {
        this.wosCredentialsProvider = wosCredentialsProvider;
    }

    public IWosCredentialsProvider getWosCredentialsProvider() {
        return this.wosCredentialsProvider;
    }

    public BasicSecurityKey getSecurityKey() {
        return (BasicSecurityKey) this.wosCredentialsProvider.getSecurityKey();
    }

    public void setThreadLocalAuthType(AuthTypeEnum authType) {
        if (threadLocalAuthType != null) {
            threadLocalAuthType.set(authType);
        }
    }

    public void removeThreadLocalAuthType() {
        if (threadLocalAuthType != null) {
            threadLocalAuthType.remove();
        }
    }

    public void initThreadLocalAuthType() {
        if (threadLocalAuthType == null) {
            threadLocalAuthType = new ThreadLocal<AuthTypeEnum>() {
                @Override
                protected AuthTypeEnum initialValue() {
                    return ProviderCredentials.this.authType;
                }
            };
        }
    }
}
