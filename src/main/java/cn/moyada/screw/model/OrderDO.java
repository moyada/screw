package cn.moyada.screw.model;

import java.sql.Timestamp;

/**
 * Created by xueyikang on 2017/12/11.
 */
public class OrderDO extends BaseDO {

    private Long orderCode;

    private Timestamp createTime;

    private Timestamp paymentTime;

    private String userName;

    private String phone;

    private Long price;

    private String userId;

    private OrderDetailDO orderDetailDO;

    public Long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderDetailDO getOrderDetailDO() {
        return orderDetailDO;
    }

    public void setOrderDetailDO(OrderDetailDO orderDetailDO) {
        this.orderDetailDO = orderDetailDO;
    }
}
