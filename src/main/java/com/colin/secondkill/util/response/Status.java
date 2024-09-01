package com.colin.secondkill.util.response;

/**
 * 2024年05月17日18:00
 */
public enum Status {
    /**
     * 最基本的成功响应
     */
    SUCCESS,
    /**
     * 最基本的服务端数据错误响应
     */
    ERROR,
    /**
     * 格式校验不通过
     */
    PATTERN_ERROR,
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS,
    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS,
    /**
     * 文件已存在
     */
    FILE_EXISTS,
    /**
     * 文件不存在
     */
    FILE_NOT_EXISTS,
    /**
     * 库存不足
     */
    INSUFFICIENT_INVENTORY,
    /**
     * 下单中
     */
    ORDERING_IN_PROGRESS,
    /**
     * 限购
     */
    PURCHASE_LIMIT,
    /**
     * 下单失败
     */
    ORDER_FAILED,
    /**
     * 支付失败
     */
    PAYMENT_FAILED

}
