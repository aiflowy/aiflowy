package tech.aiflowy.common.filestorage.s3;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.common.filestorage.StorageConfig;
import tech.aiflowy.common.filestorage.utils.PathGeneratorUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Date;

/**
 * S3 存储协议 所有兼容S3协议的云厂商均支持 阿里云 腾讯云 七牛云 minio
 */
public class S3Client {
    private static final Logger log = LoggerFactory.getLogger(S3Client.class);
    /**
     * https 状态
     */
    private final String[] CLOUD_SERVICE = new String[]{"aliyun", "qcloud", "qiniu", "obs"};
    private static final int IS_HTTPS = 1;
    private final S3StorageConfig properties;

    private final AmazonS3 client;

    public S3Client() {
        this.properties = S3StorageConfig.getInstance();
        try {
            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion());

            AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
            AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            ClientConfiguration clientConfig = new ClientConfiguration();
            if (IS_HTTPS == properties.getIsHttps()) {
                clientConfig.setProtocol(Protocol.HTTPS);
            } else {
                clientConfig.setProtocol(Protocol.HTTP);
            }
            AmazonS3ClientBuilder build = AmazonS3Client.builder().withEndpointConfiguration(endpointConfig)
                    .withClientConfiguration(clientConfig).withCredentials(credentialsProvider).disableChunkedEncoding();
            if (isPathStyleAccessRequired(properties.getEndpoint())) {
                build.enablePathStyleAccess();
            }

            this.client = build.build();

            createBucket();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }

    private boolean isPathStyleAccessRequired(String endpoint) {
        return !StrUtil.containsAny(endpoint,
                CLOUD_SERVICE);
    }

    public void createBucket() {
        try {
            String bucketName = properties.getBucketName();
            if (client.doesBucketExistV2(bucketName)) {
                return;
            }
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            AccessPolicyType accessPolicy = getAccessPolicy();
            createBucketRequest.setCannedAcl(accessPolicy.getAcl());
            client.createBucket(createBucketRequest);
            client.setBucketPolicy(bucketName, getPolicy(bucketName, accessPolicy.getPolicyType()));
        } catch (Exception e) {
            throw new RuntimeException("创建Bucket失败, 请核对配置信息:[" + e.getMessage() + "]");
        }
    }

    public void upload(byte[] data, String objectPath, String contentType) {
        upload(new ByteArrayInputStream(data), objectPath, contentType);
    }

    public void upload(InputStream inputStream, String objectPath, String contentType) {
        if (!(inputStream instanceof ByteArrayInputStream)) {
            inputStream = new ByteArrayInputStream(IoUtil.readBytes(inputStream));
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(inputStream.available());
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(properties.getBucketName(), objectPath, inputStream, metadata);
            // 设置上传对象的 Acl 为公共读
            putObjectRequest.setCannedAcl(getAccessPolicy().getAcl());
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("上传文件失败，请检查配置信息:{}",e.getMessage());
            throw new RuntimeException("上传文件失败，请稍后重试！");
        }
    }

    public String upload(MultipartFile file) throws Exception {

        String path = "";
        String loginIdAsString = "";

        try {
            loginIdAsString = StpUtil.getLoginIdAsString();
            if (StringUtils.hasValue(loginIdAsString)){
                path = StpUtil.getLoginIdAsString() + PathGeneratorUtil.generatePath(file.getOriginalFilename());
            }
        } catch (Exception e) {
            if (e instanceof NotLoginException) {
                path = "/" + "commons" + PathGeneratorUtil.generatePath(file.getOriginalFilename());
            } else {
                e.printStackTrace();
            }
        }
        byte[] content = file.getBytes();
        String baseUrl = properties.getEndpoint() + "/" + properties.getBucketName() + "/";
        if ("aliyun".equals(properties.getManufacturer())){
            baseUrl = "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/";
        }
        if (StringUtils.hasValue(properties.getPrefix())) {
            path = properties.getPrefix() + "/" + path;
        }

        if (StrUtil.isNotEmpty(properties.getDomain())) {
            baseUrl = properties.getDomain() + "/";
        }
        String completeUrl = baseUrl + path;
        upload(content, path, file.getContentType());
        return completeUrl;
    }

    public String upload(MultipartFile file, String prePath) throws Exception {
        byte[] content = file.getBytes();
        String name = file.getOriginalFilename();
        String path = generatePath(content, name);
        String baseUrl = properties.getEndpoint() + "/" + properties.getBucketName() + "/";



        if (StringUtils.hasValue(prePath)) {

            if (prePath.startsWith("/")){
                prePath = prePath.substring(1);
            }

            if (prePath.endsWith("/")){
                prePath = prePath.substring(0, prePath.length() - 1);
            }


            path = prePath + "/" + path;
        }

        if (StringUtils.hasValue(properties.getPrefix())) {
            path =  (properties.getPrefix().endsWith("/") ? properties.getPrefix() : properties.getPrefix() + "/") + path ;
        }

        String completeUrl = baseUrl + path;
        upload(content, path, file.getContentType());
        return completeUrl;
    }

    public String upload(File file,String prePath) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String name = file.getName();
        String hashName = generatePath(bytes,name);
        String completeUrl = (StringUtils.hasValue(prePath) ? prePath + "/" : "/") + hashName;
        upload(bytes,completeUrl,Files.probeContentType(file.toPath()));
        return generateAccessUrl(completeUrl);
    }

    /**
     * 生成完整的访问路径（需要访问路径的策略为可读）
     */
    private String generateAccessUrl(String path){
        String domain = properties.getDomain();

        String accessUrl = "";

        if (StringUtils.hasValue(domain)) {
            accessUrl += domain;
        }else{
            accessUrl += properties.getEndpoint();
        }

        accessUrl += "/" + properties.getBucketName();

        if (path.startsWith("/")){
            path = path.substring(1);
        }

        if (path.endsWith("/")){
            path = path.substring(0, path.length()-1);
        }

        accessUrl += "/" + path;

        return accessUrl;


    }

    public static String generatePath(byte[] content, String originalName) throws Exception {
        // 计算文件内容的 SHA256 哈希值
        String sha256Hex = sha256Hex(content);

        // 情况一：如果存在原始文件名，优先使用其后缀
        if (StrUtil.isNotBlank(originalName)) {
            // 提取文件后缀
            String extName = FileNameUtil.extName(originalName);
            // 如果后缀存在，返回 "哈希值.后缀"，否则返回 "哈希值"
            return (StrUtil.isBlank(extName) ? sha256Hex : sha256Hex + "." + extName);
        }

        // 情况二：如果原始文件名为空，基于文件内容推断文件类型
        return sha256Hex + '.' + FileTypeUtil.getType(new ByteArrayInputStream(content));
    }

    public static String sha256Hex(byte[] input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input);

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString(); // 返回字符串类型的哈希值
    }

    /**
     * 根据OSS对象存储路径, 从OSS存储删除文件
     *
     * @param objectPath 文件访问路径
     */
    public void delete(String objectPath) {
        try {
            client.deleteObject(properties.getBucketName(), objectPath);
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }


    /**
     * 根据OSS对象存储路径, 获取文件元数据
     *
     * @param objectPath 文件访问路径(对应OSS存储数据的key)
     */
    public ObjectMetadata getObjectMetadata(String objectPath) {
        return client.getObjectMetadata(properties.getBucketName(), objectPath);
    }

    /**
     * 根据OSS对象存储路径, 获取文件内容
     *
     * @param objectPath 文件访问路径
     * @return 文件内容
     */
    public InputStream getObjectContent(String objectPath) {
        return getObjectContent(objectPath, null, null);
    }

    /**
     * 根据OSS对象存储路径, 获取文件内容(指定文件字节范围)
     *
     * @param objectPath 文件访问路径
     * @param start      起始字节
     * @param end        结束字节
     * @return 文件内容(指定文件字节范围)
     */
    public InputStream getObjectContent(String objectPath, Long start, Long end) {

//        if (objectPath.startsWith("http") || objectPath.startsWith("https")) {
//            objectPath = objectPath.substring(properties.getEndpoint().length() + properties.getBucketName().length() + 1);
//        }
        objectPath = objectPath.replace(properties.getDomain() + "/", "");
        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), objectPath);
        if (start != null) {
            if (end != null) {
                request.setRange(start, end);
            } else {
                request.setRange(start);
            }
        }

        S3Object object = client.getObject(request);
        return object.getObjectContent();
    }

    private String getSchema() {
        String schema = (IS_HTTPS == properties.getIsHttps() ? "https://" : "http://");
        return schema;
    }

    /**
     * 获取OSS基础路径
     *
     * @return 基础路径
     */
    public String getBasePath() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String schema = this.getSchema();
        // 云服务厂商object url格式: <schema>://<bucket>.<endpoint>/<objectPath>
        if (StrUtil.isNotBlank(domain)) {
            return schema + domain;
        }
        return schema + properties.getBucketName() + "." + endpoint;
    }

    /**
     * 根据访问url, 获取OSS对象存储路径
     *
     * @return OSS对象存储路径
     */
    public String getObjectPath(String url) {
        if (StrUtil.isBlank(url)) {
            return url;
        }
        return url.replace(getBasePath() + "/", "");
    }

    /**
     * 根据OSS对象存储路径, 获取访问url
     *
     * @param objectPath 文件访问路径
     * @return 访问url
     */
    public String getUrl(String objectPath) {
        return getBasePath() + "/" + objectPath;
    }

    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getSchema();
        // 云服务商直接返回
        if (StrUtil.isNotBlank(domain)) {
            return header + domain;
        }
        return header + properties.getBucketName() + "." + endpoint;
    }

    /**
     * 根据前缀和后缀, 生成OSS对象存储路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return OSS对象存储路径
     */
    public String getObjectPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 文件路径
        String path = DateUtil.format(new Date(), "yyyy/MM/dd") + "/" + uuid;
        if (StrUtil.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }

    /**
     * 获取私有URL链接
     *
     * @param objectKey 对象KEY
     * @param second    授权时间
     */
    public String getPrivateUrl(String objectKey, Integer second) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(properties.getBucketName(), objectKey).withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + 1000L * second));
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    /**
     * 检查配置是否相同
     */
    public boolean checkPropertiesSame(StorageConfig properties) {
        return this.properties.equals(properties);
    }

    /**
     * 获取当前桶权限类型
     *
     * @return 当前桶权限类型code
     */
    public AccessPolicyType getAccessPolicy() {
        return AccessPolicyType.getByType(properties.getAccessPolicy());
    }

    private static String getPolicy(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n\"Statement\": [\n{\n\"Action\": [\n");
        if (policyType == PolicyType.WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else if (policyType == PolicyType.READ_WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucket\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else {
            builder.append("\"s3:GetBucketLocation\"\n");
        }
        builder.append("],\n\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n},\n");
        if (policyType == PolicyType.READ) {
            builder.append(
                    "{\n\"Action\": [\n\"s3:ListBucket\"\n],\n\"Effect\": \"Deny\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n},\n");
        }
        builder.append("{\n\"Action\": ");
        switch (policyType) {
            case WRITE:
                builder.append(
                        "[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
                break;
            case READ_WRITE:
                builder.append(
                        "[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:GetObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
                break;
        }
        builder.append("\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n}\n],\n\"Version\": \"2012-10-17\"\n}\n");
        return builder.toString();
    }


    /**
     * @param path 相对路径
     * @return 文件内容
     * @throws Exception
     * @description 获取文件内容
     */
    public InputStream getContent(String path) throws Exception {
        return getObjectContent(path);
    }


    public String getPreSignedAccessUrl(String path) {
        try {
            return getPreSignedAccessUrl(path, DateField.MINUTE, 30);
        } catch (Exception e) {
            log.error("获取文件访问地址异常,path={}", path, e);
            throw new RuntimeException(String.format("获取文件访问地址异常,path=[%s]:[%s]", path, e.getMessage()));
        }
    }

    /**
     * 获取私有URL链接
     *
     * @param objectKey 对象KEY
     */
    public String getPreSignedAccessUrl(String objectKey, DateField dateField, Integer time) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(properties.getBucketName(), objectKey).withMethod(HttpMethod.GET)
                        .withExpiration(DateUtil.offset(DateUtil.date(), dateField, time));
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

}
