package com.dotpot.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {


    @SerializedName("payeeId")
    @Expose
    private String payeeId;
    @SerializedName("txnType")
    @Expose
    private String txnType;
    @SerializedName("amount")
    @Expose
    private Float amount;
    @SerializedName("refTxnId")
    @Expose
    private String refTxnId;
    @SerializedName("userseq")
    @Expose
    private Long userseq;
    @SerializedName("timeStamp")
    @Expose
    private Long timeStamp;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("statusWallet")
    @Expose
    private String statusWallet;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("id")
    @Expose
    private String id;

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getRefTxnId() {
        return refTxnId;
    }

    public void setRefTxnId(String refTxnId) {
        this.refTxnId = refTxnId;
    }

    public Long getUserseq() {
        return userseq;
    }

    public void setUserseq(Long userseq) {
        this.userseq = userseq;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatusWallet() {
        return statusWallet;
    }

    public void setStatusWallet(String statusWallet) {
        this.statusWallet = statusWallet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
