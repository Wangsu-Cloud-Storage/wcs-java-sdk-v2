package com.wos.services.internal;

import com.jamesmurty.utils.XMLBuilder;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.internal.utils.UrlCodecUtil;
import com.wos.services.model.*;
import com.wos.services.model.LifecycleConfiguration.NoncurrentVersionTransition;
import com.wos.services.model.LifecycleConfiguration.Rule;
import com.wos.services.model.LifecycleConfiguration.Transition;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WosConvertor implements IConvertor {

    private static IConvertor instance = new WosConvertor();

    protected WosConvertor() {

    }

    public static IConvertor getInstance() {
        return instance;
    }

    @Override
    public String transCompleteMultipartUpload(List<PartEtag> parts) throws ServiceException {
        try {
            XMLBuilder builder = XMLBuilder.create("CompleteMultipartUpload");
            Collections.sort(parts, new Comparator<PartEtag>() {
                @Override
                public int compare(PartEtag o1, PartEtag o2) {
                    if (o1 == o2) {
                        return 0;
                    }
                    if (o1 == null) {
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    return o1.getPartNumber().compareTo(o2.getPartNumber());
                }

            });
            for (PartEtag part : parts) {
                builder.e("Part").e("PartNumber").t(part.getPartNumber() == null ? "" : part.getPartNumber().toString())
                        .up().e("ETag").t(ServiceUtils.toValid(part.geteTag()));
            }
            return builder.asString();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public String transLifecycleConfiguration(LifecycleConfiguration config) throws ServiceException {
        try {
            XMLBuilder builder = XMLBuilder.create("LifecycleConfiguration");
            for (Rule rule : config.getRules()) {
                XMLBuilder b = builder.elem("Rule");
                if (ServiceUtils.isValid2(rule.getId())) {
                    b.elem("ID").t(rule.getId());
                }
                if (rule.getPrefix() != null) {
                    b.elem("Prefix").t(ServiceUtils.toValid(rule.getPrefix()));
                }
                b.elem("Status").t(rule.getEnabled() ? "Enabled" : "Disabled");

                if (rule.getTransitions() != null) {
                    for (Transition transition : rule.getTransitions()) {
                        if (transition.getObjectStorageClass() != null) {
                            XMLBuilder tBuilder = b.elem("Transition");
                            if (transition.getDate() != null) {
                                tBuilder.elem("Date").t(ServiceUtils.formatIso8601MidnightDate(transition.getDate()));
                            } else if (transition.getDays() != null) {
                                tBuilder.elem("Days").t(transition.getDays().toString());
                            }
                            tBuilder.elem("StorageClass").t(this.transStorageClass(transition.getObjectStorageClass()));
                        }
                    }
                }

                if (rule.getExpiration() != null) {
                    XMLBuilder eBuilder = b.elem("Expiration");
                    if (rule.getExpiration().getDate() != null) {
                        eBuilder.elem("Date").t(ServiceUtils.formatIso8601MidnightDate(rule.getExpiration().getDate()));
                    } else if (rule.getExpiration().getDays() != null) {
                        eBuilder.elem("Days").t(rule.getExpiration().getDays().toString());
                    }
                }


                if (rule.getFilter() != null) {
                    XMLBuilder eBuilder = b.elem("Filter");
                    if (rule.getFilter().getPrefix() != null) {
                        eBuilder.elem("Prefix").t(rule.getFilter().getPrefix());
                    }
                }

                if (rule.getNoncurrentVersionTransitions() != null) {
                    for (NoncurrentVersionTransition noncurrentVersionTransition : rule
                            .getNoncurrentVersionTransitions()) {
                        if (noncurrentVersionTransition.getObjectStorageClass() != null
                                && noncurrentVersionTransition.getDays() != null) {
                            XMLBuilder eBuilder = b.elem("NoncurrentVersionTransition");
                            eBuilder.elem("NoncurrentDays").t(noncurrentVersionTransition.getDays().toString());
                            eBuilder.elem("StorageClass")
                                    .t(this.transStorageClass(noncurrentVersionTransition.getObjectStorageClass()));
                        }
                    }
                }

                if (rule.getNoncurrentVersionExpiration() != null
                        && rule.getNoncurrentVersionExpiration().getDays() != null) {
                    XMLBuilder eBuilder = b.elem("NoncurrentVersionExpiration");
                    eBuilder.elem("NoncurrentDays").t(rule.getNoncurrentVersionExpiration().getDays().toString());
                }
            }
            return builder.asString();
        } catch (ParserConfigurationException e) {
            throw new ServiceException("Failed to build XML document for lifecycle", e);
        } catch (TransformerException e) {
            throw new ServiceException("Failed to build XML document for lifecycle", e);
        } catch (Exception e) {
            throw new ServiceException("Failed to build XML document for lifecycle", e);
        }
    }

    @Override
    public String transRestoreObjectRequest(RestoreObjectRequest req) throws ServiceException {
        try {
            XMLBuilder builder = XMLBuilder.create("RestoreRequest").elem("Days").t(String.valueOf(req.getDays())).up();
            return builder.asString();
        } catch (Exception e) {
            throw new ServiceException("Failed to build XML document for restoreobject", e);
        }
    }

    public String transKey(String[] objectKeys, boolean isQuiet) throws ServiceException {
        try {
            XMLBuilder builder = XMLBuilder.create("Delete").elem("Quiet").text(String.valueOf(isQuiet)).up();
            for (String nav : objectKeys) {
                XMLBuilder objectBuilder = builder.elem("Object").elem("Key").text(ServiceUtils.toValid(UrlCodecUtil.dataEncode(nav, "UTF-8")))
                        .up();
            }
            return builder.asString();
        } catch (Exception e) {
            throw new ServiceException("Failed to build XML document", e);
        }
    }

    @Override
    public String transStorageClass(StorageClassEnum storageClass) {
        String storageClassStr = "";
        if (storageClass != null) {
            switch (storageClass) {
                case standard:
                    storageClassStr = "standard";
                    break;
                case ia:
                    storageClassStr = "ia";
                    break;
                case archive:
                    storageClassStr = "archive";
                    break;
                default:
                    break;
            }
        }
        return storageClassStr;
    }

    @Override
    public String transGroupGrantee(GroupGranteeEnum groupGrantee) {
        String groupGranteeStr = "";
        if (groupGrantee != null) {
            switch (groupGrantee) {
                case ALL_USERS:
                    groupGranteeStr = Constants.ALL_USERS_URI;
                    break;
                default:
                    break;
            }
        }
        return groupGranteeStr;
    }

}
