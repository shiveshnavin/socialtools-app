package com.dotpot.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet {


    @SerializedName("aggCreditLoaded")
    @Expose
    private Long aggCreditLoaded;
    @SerializedName("userseq")
    @Expose
    private Long userseq;
    @SerializedName("referrals")
    @Expose
    private String referrals;
    @SerializedName("creditBalance")
    @Expose
    private Long creditBalance;
    @SerializedName("aggReferralBalance")
    @Expose
    private Long aggReferralBalance;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("winningBalance")
    @Expose
    private Long winningBalance;
    @SerializedName("aggWinningBalance")
    @Expose
    private Long aggWinningBalance;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;
    @SerializedName("currency")
    @Expose
    private String currency;

    public Long getAggCreditLoaded() {
        return aggCreditLoaded;
    }

    public void setAggCreditLoaded(Long aggCreditLoaded) {
        this.aggCreditLoaded = aggCreditLoaded;
    }

    public Long getUserseq() {
        return userseq;
    }

    public void setUserseq(Long userseq) {
        this.userseq = userseq;
    }

    public String getReferrals() {
        return referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public Long getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Long creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Long getAggReferralBalance() {
        return aggReferralBalance;
    }

    public void setAggReferralBalance(Long aggReferralBalance) {
        this.aggReferralBalance = aggReferralBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getWinningBalance() {
        return winningBalance;
    }

    public void setWinningBalance(Long winningBalance) {
        this.winningBalance = winningBalance;
    }

    public Long getAggWinningBalance() {
        return aggWinningBalance;
    }

    public void setAggWinningBalance(Long aggWinningBalance) {
        this.aggWinningBalance = aggWinningBalance;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
