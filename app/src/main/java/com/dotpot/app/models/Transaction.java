package com.dotpot.app.models;

import androidx.annotation.DrawableRes;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
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
    @SerializedName("debitOrCredit")
    @Expose
    private String debitOrCredit;


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

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

    public void setDebitOrCredit(String debitOrCredit) {
        this.debitOrCredit = debitOrCredit;
    }

    public int getStatusColor(){
        switch (getStatus()){
            case Constants
                    .TXN_SUCCESS:
                return R.color.colorTextSuccess;
            case Constants
                    .TXN_FAILURE:
                return R.color.colorTextWarning;
            default:
                return R.color.colortext;
        }
    }

    public @DrawableRes
    int getTxtTypeIcon() {

        if(getTxnType().contains("topup")){
            return R.drawable.cash;
        }
        else if(getTxnType().contains("shop")){
            return R.drawable.ic_logo;
        }
        else if(getTxnType().contains("withdraw")){
            return R.drawable.withdraw;
        }
        else {
            return R.drawable.gamepad_variant_outline;
        }
    }

    public String getDescription(){
        return utl.toTitleCase(getTxnType().replaceAll("_"," "));
    }



    public String getDisplayStatus() {

        if(getStatus().contains(Constants.TXN_SUCCESS)){
            return ResourceUtils.getString(R.string.txn_success);
        }
        else if(getStatus().contains(Constants.TXN_FAILURE)){
            return ResourceUtils.getString(R.string.txn_failed);
        }
        else {
            return ResourceUtils.getString(R.string.txn_initated);
        }
    }
}
