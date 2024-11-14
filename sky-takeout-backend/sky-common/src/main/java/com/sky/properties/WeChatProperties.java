package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.wechat")
@Data
public class WeChatProperties {
    private String appid; // 小程序的appid
    private String secret; // 小程序的秘钥
    private String mchid; // 商户号
    private String mchSerialNo; // 商户 API 证书的证书序列号
    private String privateKeyFilePath; // 商户 API 证书私钥文件路径
    private String apiV3Key; // APIv3 证书解密的密钥
    private String weChatPayCertFilePath; // 平台证书路径
    private String notifyUrl; // 支付成功的回调地址
    private String refundNotifyUrl; // 退款成功的回调地址
}
