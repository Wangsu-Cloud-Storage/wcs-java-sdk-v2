package com.wos.services.internal;

import com.wos.services.model.*;

import java.util.List;

public interface IConvertor {

    String transCompleteMultipartUpload(List<PartEtag> parts) throws ServiceException;

    String transLifecycleConfiguration(LifecycleConfiguration config) throws ServiceException;

    String transRestoreObjectRequest(RestoreObjectRequest req) throws ServiceException;

    String transKey(String[] objectKeys, boolean isQuiet) throws ServiceException;

    String transStorageClass(StorageClassEnum storageClass);

    String transGroupGrantee(GroupGranteeEnum groupGrantee);
}