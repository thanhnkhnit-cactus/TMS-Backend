package com.tms.backend.dto;

import java.util.List;

public class RejectOrdersRequest {
    private List<Long> orderIds;
    private String reason;

    public RejectOrdersRequest() {}

    public List<Long> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
