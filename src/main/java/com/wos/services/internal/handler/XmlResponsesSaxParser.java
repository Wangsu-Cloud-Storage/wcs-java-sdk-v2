package com.wos.services.internal.handler;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import com.wos.services.internal.Constants;
import com.wos.services.internal.ServiceException;
import com.wos.services.internal.io.HttpMethodReleaseInputStream;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.model.*;
import com.wos.services.model.avOperation.AudioAndVideoTaskDetailResult;
import com.wos.services.model.avOperation.AvOperationTypeEnum;
import com.wos.services.model.avOperation.QueryAvconcatResult;
import com.wos.services.model.avOperation.QueryAvthumbResult;
import com.wos.services.model.avOperation.QueryGetapicResult;
import com.wos.services.model.avOperation.QueryVframeResult;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlResponsesSaxParser {

    private static final ILogger log = LoggerBuilder.getLogger(XmlResponsesSaxParser.class);

    private XMLReader xmlReader;

    public XmlResponsesSaxParser() throws ServiceException {
        this.xmlReader = ServiceUtils.loadXMLReader();
    }

    protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream) throws ServiceException {
        if (inputStream == null) {
            return;
        }
        try {
            xmlReader.setErrorHandler(handler);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(inputStream));
        } catch (Exception t) {
            throw new ServiceException("Failed to parse XML document with handler " + handler.getClass(), t);
        } finally {
            ServiceUtils.closeStream(inputStream);
        }
    }


    protected InputStream sanitizeXmlDocument(InputStream inputStream) throws ServiceException {
        if (inputStream == null) {
            return null;
        }
        BufferedReader br = null;
        try {
            StringBuilder listingDocBuffer = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));

            char[] buf = new char[8192];
            int read = -1;
            while ((read = br.read(buf)) != -1) {
                listingDocBuffer.append(buf, 0, read);
            }

            String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");
            if (log.isTraceEnabled()) {
                log.trace("Response entity: " + listingDoc);
            }
            return new ByteArrayInputStream(listingDoc.getBytes(Constants.DEFAULT_ENCODING));
        } catch (Throwable t) {
            throw new ServiceException("Failed to sanitize XML document destined", t);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e);
                    }
                }
            }
            ServiceUtils.closeStream(inputStream);
        }
    }

    public <T> T parse(InputStream inputStream, Class<T> handlerClass, boolean sanitize) throws ServiceException {
        try {
            T handler = null;
            if (SimpleHandler.class.isAssignableFrom(handlerClass)) {
                Constructor<T> c = handlerClass.getConstructor(XMLReader.class);
                handler = c.newInstance(this.xmlReader);
            } else {
                handler = handlerClass.getConstructor().newInstance();
            }
            if (handler instanceof DefaultHandler) {
                if (sanitize) {
                    inputStream = sanitizeXmlDocument(inputStream);
                }
                parseXmlInputStream((DefaultHandler) handler, inputStream);
            }
            return handler;
        } catch (NoSuchMethodException e) {
            throw new ServiceException(e);
        } catch (InvocationTargetException e) {
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public static AudioAndVideoTaskDetailResult parseAvTaskDetailXml(Response response) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(QueryVframeResult.class, QueryAvthumbResult.class, QueryAvconcatResult.class, QueryGetapicResult.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(false);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            SAXSource source = new SAXSource(xmlReader, new InputSource(new HttpMethodReleaseInputStream(response)));
            xmlObject = unmarshaller.unmarshal(source);
        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            throw new ServiceException(e);
        }
        if (null == xmlObject) {
            return null;
        }

        AudioAndVideoTaskDetailResult audioAndVideoTaskDetailResult = (AudioAndVideoTaskDetailResult)xmlObject;
        if (xmlObject instanceof QueryAvthumbResult) {
            audioAndVideoTaskDetailResult.setOperationType(AvOperationTypeEnum.Avthumb.getValue());
        } else if (xmlObject instanceof QueryGetapicResult) {
            audioAndVideoTaskDetailResult.setOperationType(AvOperationTypeEnum.Getapic.getValue());
        } else if (xmlObject instanceof QueryAvconcatResult) {
            audioAndVideoTaskDetailResult.setOperationType(AvOperationTypeEnum.Avconcat.getValue());
        } else if (xmlObject instanceof QueryVframeResult) {
            audioAndVideoTaskDetailResult.setOperationType(AvOperationTypeEnum.Vframe.getValue());
        }
        return audioAndVideoTaskDetailResult;
    }



    /**
     * xml -> Object
     */
    public static Object convertResponseToObject(Class clazz, Response response) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(false);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            SAXSource source = new SAXSource(xmlReader, new InputSource(new HttpMethodReleaseInputStream(response)));
            xmlObject = unmarshaller.unmarshal(source);
        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            throw new ServiceException(e);
        }
        return xmlObject;
    }


    public static class ListObjectsHandler extends DefaultXmlHandler {
        private WosObject currentObject;

        private Owner currentOwner;

        private boolean insideCommonPrefixes = false;

        private final List<WosObject> objects = new ArrayList<WosObject>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private WosObject currentExtenedCommonPrefix;

        private final List<WosObject> extenedCommonPrefixes = new ArrayList<WosObject>();

        private String bucketName;

        private String requestPrefix;

        private String requestMarker;

        private String requestDelimiter;

        private int requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String lastKey;

        private String nextMarker;

        public String getMarkerForNextListing() {
            return listingTruncated ? nextMarker == null ? lastKey : nextMarker : null;
        }

        public String getBucketName() {
            return bucketName;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public List<WosObject> getObjects() {
            return this.objects;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public List<WosObject> getExtenedCommonPrefixes() {
            return extenedCommonPrefixes;
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getRequestMarker() {
            return requestMarker;
        }

        public String getNextMarker() {
            return nextMarker;
        }

        public int getRequestMaxKeys() {
            return requestMaxKeys;
        }

        public String getRequestDelimiter() {
            return requestDelimiter;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Contents")) {
                currentObject = new WosObject();
                currentObject.setBucketName(bucketName);
            } else if (name.equals("Owner")) {
                currentOwner = new Owner();
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = true;
                currentExtenedCommonPrefix = new WosObject();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Name")) {
                bucketName = elementText;
            } else if (!insideCommonPrefixes && name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                requestMarker = elementText;
            } else if (name.equals("NextMarker")) {
                nextMarker = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Integer.parseInt(elementText);
            } else if (name.equals("Delimiter")) {
                requestDelimiter = elementText;
            } else if (name.equals("IsTruncated")) {
                listingTruncated = Boolean.valueOf(elementText);
            } else if (name.equals("Contents")) {
                objects.add(currentObject);
            } else if (name.equals("DisplayName")) {
                if (currentOwner != null) {
                    currentOwner.setDisplayName(elementText);
                }
            }

            if (null != currentObject) {
                if (name.equals("Key")) {
                    currentObject.setObjectKey(elementText);
                    lastKey = elementText;
                } else if (name.equals("LastModified")) {
                    try {
                        currentObject.getMetadata().setLastModified(ServiceUtils.parseIso8601Date(elementText));
                    } catch (ParseException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Non-ISO8601 date for LastModified in bucket's object listing output: "
                                    + elementText, e);
                        }
                    }
                } else if (name.equals("ETag")) {
                    currentObject.getMetadata().setEtag(elementText);
                } else if (name.equals("Size")) {
                    currentObject.getMetadata().setContentLength(Long.parseLong(elementText));
                } else if (name.equals("StorageClass")) {
                    currentObject.getMetadata().setObjectStorageClass(StorageClassEnum.getValueFromCode(elementText));
                } else if (name.equals("ID")) {
                    if (currentOwner == null) {
                        currentOwner = new Owner();
                    }
                    currentObject.setOwner(currentOwner);
                    currentOwner.setId(elementText);
                    for (WosObject wo: this.objects) {
                        wo.setOwner(currentOwner);
                    }
                }
            }

            if (null != currentExtenedCommonPrefix) {
                if (insideCommonPrefixes && name.equals("Prefix")) {
                    commonPrefixes.add(elementText);
                    currentExtenedCommonPrefix.setObjectKey(elementText);
                } else if (insideCommonPrefixes && name.equals("MTime")) {
                    currentExtenedCommonPrefix.getMetadata()
                            .setLastModified(new Date(Long.parseLong(elementText) * 1000));
                }
            }

            if (name.equals("CommonPrefixes")) {
                extenedCommonPrefixes.add(currentExtenedCommonPrefix);
                insideCommonPrefixes = false;
            }
        }
    }

    public static class ListObjectsV2Handler extends DefaultXmlHandler {
        private WosObject currentObject;

        private Owner currentOwner;

        private boolean insideCommonPrefixes = false;

        private final List<WosObject> objects = new ArrayList<WosObject>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private WosObject currentExtenedCommonPrefix;

        private final List<WosObject> extenedCommonPrefixes = new ArrayList<WosObject>();

        private String bucketName;

        private String requestPrefix;

        private String startAfter;

        private String requestDelimiter;

        private int requestMaxKeys = 0;

        private boolean listingTruncated = false;

        private String lastKey;

        private String nextContinuationToken;

        private String keyCount;

        private String encodingType;

        private String continuationToken;

        public String getBucketName() {
            return bucketName;
        }

        public boolean isListingTruncated() {
            return listingTruncated;
        }

        public List<WosObject> getObjects() {
            return this.objects;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public List<WosObject> getExtenedCommonPrefixes() {
            return extenedCommonPrefixes;
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getStartAfter() {
            return startAfter;
        }

        public int getRequestMaxKeys() {
            return requestMaxKeys;
        }

        public String getRequestDelimiter() {
            return requestDelimiter;
        }

        public String getNextContinuationToken() {
            return nextContinuationToken;
        }

        public String getKeyCount() {
            return keyCount;
        }

        public String getEncodingType() {
            return encodingType;
        }

        public String getContinuationToken() {
            return continuationToken;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Contents")) {
                currentObject = new WosObject();
                currentObject.setBucketName(bucketName);
            } else if (name.equals("Owner")) {
                currentOwner = new Owner();
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = true;
                currentExtenedCommonPrefix = new WosObject();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("Name")) {
                bucketName = elementText;
            } else if (!insideCommonPrefixes && name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                startAfter = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Integer.parseInt(elementText);
            } else if (name.equals("Delimiter")) {
                requestDelimiter = elementText;
            } else if (name.equals("IsTruncated")) {
                listingTruncated = Boolean.valueOf(elementText);
            } else if (name.equals("Contents")) {
                objects.add(currentObject);
            } else if (name.equals("NextContinuationToken")) {
                nextContinuationToken = elementText;
            } else if (name.equals("KeyCount")) {
                keyCount = elementText;
            } else if (name.equals("EncodingType")) {
                encodingType = elementText;
            } else if (name.equals("ContinuationToken")) {
                continuationToken = elementText;
            } else if (name.equals("DisplayName")) {
                if (currentOwner != null) {
                    currentOwner.setDisplayName(elementText);
                }
            }

            if (null != currentObject) {
                if (name.equals("Key")) {
                    currentObject.setObjectKey(elementText);
                    lastKey = elementText;
                } else if (name.equals("LastModified")) {
                    try {
                        currentObject.getMetadata().setLastModified(ServiceUtils.parseIso8601Date(elementText));
                    } catch (ParseException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Non-ISO8601 date for LastModified in bucket's object listing output: "
                                    + elementText, e);
                        }
                    }
                } else if (name.equals("ETag")) {
                    currentObject.getMetadata().setEtag(elementText);
                } else if (name.equals("Size")) {
                    currentObject.getMetadata().setContentLength(Long.parseLong(elementText));
                } else if (name.equals("StorageClass")) {
                    currentObject.getMetadata().setObjectStorageClass(StorageClassEnum.getValueFromCode(elementText));
                } else if (name.equals("ID")) {
                    if (currentOwner == null) {
                        currentOwner = new Owner();
                    }
                    currentObject.setOwner(currentOwner);
                    currentOwner.setId(elementText);
                    for (WosObject wo: this.objects) {
                        wo.setOwner(currentOwner);
                    }
                }
            }

            if (null != currentExtenedCommonPrefix) {
                if (insideCommonPrefixes && name.equals("Prefix")) {
                    commonPrefixes.add(elementText);
                    currentExtenedCommonPrefix.setObjectKey(elementText);
                } else if (insideCommonPrefixes && name.equals("MTime")) {
                    currentExtenedCommonPrefix.getMetadata()
                            .setLastModified(new Date(Long.parseLong(elementText) * 1000));
                }
            }

            if (name.equals("CommonPrefixes")) {
                extenedCommonPrefixes.add(currentExtenedCommonPrefix);
                insideCommonPrefixes = false;
            }
        }
    }

    public static class ListBucketsHandler extends DefaultXmlHandler {
        private Owner bucketsOwner;

        private WosBucket currentBucket;

        private final List<WosBucket> buckets = new ArrayList<WosBucket>();

        public List<WosBucket> getBuckets() {
            return this.buckets;
        }

        public Owner getOwner() {
            return bucketsOwner;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Bucket")) {
                currentBucket = new WosBucket();
            } else if (name.equals("Owner")) {
                bucketsOwner = new Owner();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if (null != bucketsOwner) {
                if (name.equals("ID")) {
                    bucketsOwner.setId(elementText);
                } else if (name.equals("DisplayName")) {
                    bucketsOwner.setDisplayName(elementText);
                    for (WosBucket bucket: buckets) {
                        bucket.setOwner(bucketsOwner);
                    }
                }
            }

            if (null != currentBucket) {
                if (name.equals("Bucket")) {
                    buckets.add(currentBucket);
                } else if (name.equals("Name")) {
                    currentBucket.setBucketName(elementText);
                } else if (name.equals("Endpoint")) {
                    currentBucket.setEndpoint(elementText);
                } else if (name.equals("Region")) {
                    currentBucket.setRegion(elementText);
                } else if (name.equals("CreationDate")) {
                    elementText += ".000Z";
                    try {
                        currentBucket.setCreationDate(ServiceUtils.parseIso8601Date(elementText));
                    } catch (ParseException e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Non-ISO8601 date for CreationDate in list buckets output: " + elementText, e);
                        }
                    }
                }
            }
        }
    }

    public static class CopyObjectResultHandler extends DefaultXmlHandler {
        private String etag;

        private Date lastModified;

        public Date getLastModified() {
            return ServiceUtils.cloneDateIgnoreNull(lastModified);
        }

        public String getETag() {
            return etag;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("LastModified")) {
                try {
                    lastModified = ServiceUtils.parseIso8601Date(elementText);
                } catch (ParseException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Non-ISO8601 date for LastModified in copy object output: " + elementText, e);
                    }
                }
            } else if (name.equals("ETag")) {
                etag = elementText;
            }
        }
    }

    public static class OwnerHandler extends SimpleHandler {
        private String id;

        private String displayName;

        public OwnerHandler(XMLReader xr) {
            super(xr);
        }

        public Owner getOwner() {
            Owner owner = new Owner();
            owner.setId(id);
            owner.setDisplayName(displayName);
            return owner;
        }

        public void endID(String content) {
            this.id = content;
        }

        public void endDisplayName(String content) {
            this.displayName = content;
        }

        public void endOwner(String content) {
            returnControlToParentHandler();
        }

        public void endInitiator(String content) {
            returnControlToParentHandler();
        }
    }

    public static class InitiateMultipartUploadHandler extends SimpleHandler {
        private String uploadId;

        private String bucketName;

        private String objectKey;

        public InitiateMultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public InitiateMultipartUploadResult getInitiateMultipartUploadResult() {
            InitiateMultipartUploadResult result = new InitiateMultipartUploadResult(bucketName, objectKey, uploadId);
            return result;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }
    }

    public static class MultipartUploadHandler extends SimpleHandler {
        private String uploadId;

        private String objectKey;

        private String storageClass;

        private Owner owner;

        private Owner initiator;

        private Date initiatedDate;

        private boolean isInInitiator = false;

        public MultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public MultipartUpload getMultipartUpload() {
            MultipartUpload multipartUpload = new MultipartUpload(uploadId, objectKey, initiatedDate,
                    StorageClassEnum.getValueFromCode(storageClass), owner, initiator);
            return multipartUpload;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endStorageClass(String content) {
            this.storageClass = content;
        }

        public void endInitiated(String content) {
            try {
                this.initiatedDate = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void startOwner() {
            isInInitiator = false;
            transferControl(new OwnerHandler(xr));
        }

        public void startInitiator() {
            isInInitiator = true;
            transferControl(new OwnerHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (isInInitiator) {
                this.initiator = ((OwnerHandler) childHandler).getOwner();
            } else {
                this.owner = ((OwnerHandler) childHandler).getOwner();
            }
        }

        public void endUpload(String content) {
            returnControlToParentHandler();
        }
    }

    public static class ListMultipartUploadsHandler extends SimpleHandler {

        private final List<MultipartUpload> uploads = new ArrayList<MultipartUpload>();

        private final List<String> commonPrefixes = new ArrayList<String>();

        private boolean insideCommonPrefixes;

        private String bucketName;

        private String keyMarker;

        private String uploadIdMarker;

        private String nextKeyMarker;

        private String nextUploadIdMarker;

        private String delimiter;

        private int maxUploads;

        private String prefix;

        private boolean isTruncated = false;

        public ListMultipartUploadsHandler(XMLReader xr) {
            super(xr);
        }

        public List<MultipartUpload> getMultipartUploadList() {
            for (MultipartUpload upload : uploads) {
                upload.setBucketName(bucketName);
            }
            return uploads;
        }

        public String getBucketName() {
            return this.bucketName;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getKeyMarker() {
            return keyMarker;
        }

        public String getUploadIdMarker() {
            return uploadIdMarker;
        }

        public String getNextKeyMarker() {
            return nextKeyMarker;
        }

        public String getNextUploadIdMarker() {
            return nextUploadIdMarker;
        }

        public int getMaxUploads() {
            return maxUploads;
        }

        public List<String> getCommonPrefixes() {
            return commonPrefixes;
        }

        public String getDelimiter() {
            return this.delimiter;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void startUpload() {
            transferControl(new MultipartUploadHandler(xr));
        }

        public void startCommonPrefixes() {
            insideCommonPrefixes = true;
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            uploads.add(((MultipartUploadHandler) childHandler).getMultipartUpload());
        }

        public void endDelimiter(String content) {
            this.delimiter = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKeyMarker(String content) {
            this.keyMarker = content;
        }

        public void endUploadIdMarker(String content) {
            this.uploadIdMarker = content;
        }

        public void endNextKeyMarker(String content) {
            this.nextKeyMarker = content;
        }

        public void endNextUploadIdMarker(String content) {
            this.nextUploadIdMarker = content;
        }

        public void endMaxUploads(String content) {
            try {
                this.maxUploads = Integer.parseInt(content);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Response xml is not well-format", e);
                }
            }
        }

        public void endIsTruncated(String content) {
            this.isTruncated = Boolean.parseBoolean(content);
        }

        public void endPrefix(String content) {
            if (insideCommonPrefixes) {
                commonPrefixes.add(content);
            } else {
                this.prefix = content;
            }
        }

        public void endCommonPrefixes() {
            insideCommonPrefixes = false;
        }

    }

    public static class CopyPartResultHandler extends SimpleHandler {
        private Date lastModified;

        private String etag;

        public CopyPartResultHandler(XMLReader xr) {
            super(xr);
        }

        public CopyPartResult getCopyPartResult(int partNumber) {
            CopyPartResult result = new CopyPartResult(partNumber, etag, lastModified);
            return result;
        }

        public void endLastModified(String content) {
            try {
                this.lastModified = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void endETag(String content) {
            this.etag = content;
        }

    }

    public static class PartResultHandler extends SimpleHandler {
        private int partNumber;

        private Date lastModified;

        private String etag;

        private long size;

        public PartResultHandler(XMLReader xr) {
            super(xr);
        }

        public Multipart getMultipartPart() {
            return new Multipart(partNumber, lastModified, etag, size);
        }

        public void endPartNumber(String content) {
            this.partNumber = Integer.parseInt(content);
        }

        public void endLastModified(String content) {
            try {
                this.lastModified = ServiceUtils.parseIso8601Date(content);
            } catch (ParseException e) {
            }
        }

        public void endETag(String content) {
            this.etag = content;
        }

        public void endSize(String content) {
            this.size = Long.parseLong(content);
        }

        public void endPart(String content) {
            returnControlToParentHandler();
        }
    }

    public static class ListPartsHandler extends SimpleHandler {
        private final List<Multipart> parts = new ArrayList<Multipart>();

        private String bucketName;

        private String objectKey;

        private String uploadId;

        private Owner initiator;

        private Owner owner;

        private String storageClass;

        private String partNumberMarker;

        private String nextPartNumberMarker;

        private int maxParts;

        private boolean isTruncated = false;

        private boolean isInInitiator = false;

        public ListPartsHandler(XMLReader xr) {
            super(xr);
        }

        public List<Multipart> getMultiPartList() {
            return parts;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getUploadId() {
            return uploadId;
        }

        public Owner getInitiator() {
            return initiator;
        }

        public Owner getOwner() {
            return owner;
        }

        public String getStorageClass() {
            return storageClass;
        }

        public String getPartNumberMarker() {
            return partNumberMarker;
        }

        public String getNextPartNumberMarker() {
            return nextPartNumberMarker;
        }

        public int getMaxParts() {
            return maxParts;
        }

        public void startPart() {
            transferControl(new PartResultHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (childHandler instanceof PartResultHandler) {
                parts.add(((PartResultHandler) childHandler).getMultipartPart());
            } else {
                if (isInInitiator) {
                    initiator = ((OwnerHandler) childHandler).getOwner();
                } else {
                    owner = ((OwnerHandler) childHandler).getOwner();
                }
            }
        }

        public void startInitiator() {
            isInInitiator = true;
            transferControl(new OwnerHandler(xr));
        }

        public void startOwner() {
            isInInitiator = false;
            transferControl(new OwnerHandler(xr));
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endStorageClass(String content) {
            this.storageClass = content;
        }

        public void endUploadId(String content) {
            this.uploadId = content;
        }

        public void endPartNumberMarker(String content) {
            this.partNumberMarker = content;
        }

        public void endNextPartNumberMarker(String content) {
            this.nextPartNumberMarker = content;
        }

        public void endMaxParts(String content) {
            this.maxParts = Integer.parseInt(content);
        }

        public void endIsTruncated(String content) {
            this.isTruncated = Boolean.parseBoolean(content);
        }
    }

    public static class CompleteMultipartUploadHandler extends SimpleHandler {

        private String location;

        private String bucketName;

        private String objectKey;

        private String etag;

        public CompleteMultipartUploadHandler(XMLReader xr) {
            super(xr);
        }

        public void endLocation(String content) {
            this.location = content;
        }

        public void endBucket(String content) {
            this.bucketName = content;
        }

        public void endKey(String content) {
            this.objectKey = content;
        }

        public void endETag(String content) {
            this.etag = content;
        }

        public String getLocation() {
            return location;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getEtag() {
            return etag;
        }

    }

    public static class AudioAndVideoTaskCreateHandler extends DefaultXmlHandler  {
        private String operationType;

        private String persistentId;

        public String getPersistentId() {
            return persistentId;
        }

        public void setPersistentId(String persistentId) {
            this.persistentId = persistentId;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        @Override
        public void startElement(String name) {
            if (!StringUtils.isEmpty(name) && name.endsWith("Result")) {
                this.operationType = AvOperationTypeEnum.getTypeByValuePrefix(name);
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            if ("PersistentId".equals(name)) {
                this.persistentId = elementText;
            }
        }
    }

    public static class DeleteObjectsHandler extends DefaultXmlHandler {

        private DeleteObjectsResult result;

        private List<DeleteObjectsResult.DeleteObjectResult> deletedObjectResults = new ArrayList<DeleteObjectsResult.DeleteObjectResult>();

        private List<DeleteObjectsResult.ErrorResult> errorResults = new ArrayList<DeleteObjectsResult.ErrorResult>();

        private String key;

        private String errorCode;

        private String message;

        private boolean withDeleteMarker;

        public DeleteObjectsResult getMultipleDeleteResult() {
            return result;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("DeleteResult")) {
                result = new DeleteObjectsResult();
            }
        }

        @Override
        public void endElement(String name, String content) {
            if ("Key".equals(name)) {
                key = content;
            } else if ("DeleteMarker".equals(name)) {
                withDeleteMarker = Boolean.parseBoolean(content);
            } else if ("Code".equals(name)) {
                errorCode = content;
            } else if ("Message".equals(name)) {
                message = content;
            } else if ("Deleted".equals(name)) {
                DeleteObjectsResult.DeleteObjectResult r = new DeleteObjectsResult.DeleteObjectResult(key, withDeleteMarker);
                deletedObjectResults.add(r);
                key = null;
                withDeleteMarker = false;
            } else if ("Error".equals(name)) {
                errorResults.add(new DeleteObjectsResult.ErrorResult(key, errorCode, message));
                key = null;
            } else if (name.equals("DeleteResult")) {
                result.getDeletedObjectResults().addAll(deletedObjectResults);
                result.getErrorResults().addAll(errorResults);
            }
        }
    }

    public static class BucketLifecycleConfigurationHandler extends SimpleHandler {
        private LifecycleConfiguration config = new LifecycleConfiguration();

        private LifecycleConfiguration.Rule latestRule;

        private LifecycleConfiguration.TimeEvent latestTimeEvent;

        public BucketLifecycleConfigurationHandler(XMLReader xr) {
            super(xr);
        }

        public LifecycleConfiguration getLifecycleConfig() {
            return config;
        }

        public void startExpiration() {
            latestTimeEvent = config.new Expiration();
            latestRule.setExpiration(((LifecycleConfiguration.Expiration) latestTimeEvent));
        }

        public void startNoncurrentVersionExpiration() {
            latestTimeEvent = config.new NoncurrentVersionExpiration();
            latestRule.setNoncurrentVersionExpiration(
                    ((LifecycleConfiguration.NoncurrentVersionExpiration) latestTimeEvent));
        }

        public void startTransition() {
            latestTimeEvent = config.new Transition();
            latestRule.getTransitions().add(((LifecycleConfiguration.Transition) latestTimeEvent));
        }

        public void startNoncurrentVersionTransition() {
            latestTimeEvent = config.new NoncurrentVersionTransition();
            latestRule.getNoncurrentVersionTransitions()
                    .add(((LifecycleConfiguration.NoncurrentVersionTransition) latestTimeEvent));
        }

        public void endStorageClass(String content) {
            LifecycleConfiguration.setStorageClass(latestTimeEvent, StorageClassEnum.getValueFromCode(content));
        }

        public void endDate(String content) throws ParseException {
            LifecycleConfiguration.setDate(latestTimeEvent, ServiceUtils.parseIso8601Date(content));
        }

        public void endNoncurrentDays(String content) {
            LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(content));
        }

        public void endDays(String content) {
            LifecycleConfiguration.setDays(latestTimeEvent, Integer.parseInt(content));
        }

        public void startRule() {
            latestRule = config.new Rule();
        }

        public void endID(String content) {
            latestRule.setId(content);
        }

        public void endPrefix(String content) {
            latestRule.setPrefix(content);
        }

        public void endStatus(String content) {
            latestRule.setEnabled("Enabled".equals(content));
        }

        public void endRule(String content) {
            config.addRule(latestRule);
        }
    }

    public static class AccessControlListHandler extends DefaultXmlHandler {
        protected AccessControlList accessControlList;

        protected Owner owner;
        protected GranteeInterface currentGrantee;
        protected Permission currentPermission;
        protected boolean currentDelivered;

        protected boolean insideACL = false;

        public AccessControlList getAccessControlList() {
            return accessControlList;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("AccessControlPolicy")) {
                accessControlList = new AccessControlList();
            } else if (name.equals("Owner")) {
                owner = new Owner();
                accessControlList.setOwner(owner);
            } else if (name.equals("AccessControlList")) {
                insideACL = true;
            }
        }

        @Override
        public void endElement(String name, String content) {
            if (name.equals("ID") && !insideACL) {
                owner.setId(content);
            } else if (name.equals("DisplayName") && !insideACL) {
                owner.setDisplayName(content);
            } else if (name.equals("ID")) {
                currentGrantee = new CanonicalGrantee();
                currentGrantee.setIdentifier(content);
            } else if (name.equals("URI") || name.equals("Canned")) {
                currentGrantee = new GroupGrantee();
                currentGrantee.setIdentifier(content);
            } else if (name.equals("DisplayName")) {
                if (currentGrantee instanceof CanonicalGrantee) {
                    ((CanonicalGrantee) currentGrantee).setDisplayName(content);
                }
            } else if (name.equals("Permission")) {
                currentPermission = Permission.parsePermission(content);
            } else if (name.equals("Delivered")) {
                if (insideACL) {
                    currentDelivered = Boolean.parseBoolean(content);
                } else {
                    accessControlList.setDelivered(Boolean.parseBoolean(content));
                }
            } else if (name.equals("Grant")) {
                GrantAndPermission obj = accessControlList.grantPermission(currentGrantee, currentPermission);
                obj.setDelivered(currentDelivered);
            } else if (name.equals("AccessControlList")) {
                insideACL = false;
            }
        }

    }
}
