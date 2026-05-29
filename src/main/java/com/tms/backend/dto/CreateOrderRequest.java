package com.tms.backend.dto;

import java.math.BigDecimal;

public class CreateOrderRequest {
    private Long customerId;
    private Boolean isNewCustomer;
    private String newCustomerName;
    private String newCustomerPhone;
    private String newCustomerAddress;

    private String pickupAddress;
    private String deliveryAddress;
    private String goodsName;
    private BigDecimal weightKg;
    private BigDecimal price;
    private BigDecimal codAmount;
    private BigDecimal distanceKm;
    private BigDecimal surcharge;
    private String description;

    public CreateOrderRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Boolean getIsNewCustomer() { return isNewCustomer; }
    public void setIsNewCustomer(Boolean isNewCustomer) { this.isNewCustomer = isNewCustomer; }
    public String getNewCustomerName() { return newCustomerName; }
    public void setNewCustomerName(String newCustomerName) { this.newCustomerName = newCustomerName; }
    public String getNewCustomerPhone() { return newCustomerPhone; }
    public void setNewCustomerPhone(String newCustomerPhone) { this.newCustomerPhone = newCustomerPhone; }
    public String getNewCustomerAddress() { return newCustomerAddress; }
    public void setNewCustomerAddress(String newCustomerAddress) { this.newCustomerAddress = newCustomerAddress; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getCodAmount() { return codAmount; }
    public void setCodAmount(BigDecimal codAmount) { this.codAmount = codAmount; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
    public BigDecimal getSurcharge() { return surcharge; }
    public void setSurcharge(BigDecimal surcharge) { this.surcharge = surcharge; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
