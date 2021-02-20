package com.dotpot.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet {


    @SerializedName("aggCreditLoaded")
    @Expose
    private Float aggCreditLoaded;
    @SerializedName("userseq")
    @Expose
    private Float userseq;
    @SerializedName("referrals")
    @Expose
    private String referrals;
    @SerializedName("creditBalance")
    @Expose
    private Float creditBalance;
    @SerializedName("aggReferralBalance")
    @Expose
    private Float aggReferralBalance;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("winningBalance")
    @Expose
    private Float winningBalance;
    @SerializedName("aggWinningBalance")
    @Expose
    private Float aggWinningBalance;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;
    @SerializedName("referredBy")
    @Expose
    private String referredBy;
    @SerializedName("currency")
    @Expose
    private String currency;

    public Float getAggCreditLoaded() {
        return aggCreditLoaded;
    }

    public void setAggCreditLoaded(Float aggCreditLoaded) {
        this.aggCreditLoaded = aggCreditLoaded;
    }

    public Float getUserseq() {
        return userseq;
    }

    public void setUserseq(Float userseq) {
        this.userseq = userseq;
    }

    public String getReferrals() {
        return referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public Float getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Float creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Float getAggReferralBalance() {
        return aggReferralBalance;
    }

    public void setAggReferralBalance(Float aggReferralBalance) {
        this.aggReferralBalance = aggReferralBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getWinningBalance() {
        return winningBalance;
    }

    public void setWinningBalance(Float winningBalance) {
        this.winningBalance = winningBalance;
    }

    public Float getAggWinningBalance() {
        return aggWinningBalance;
    }

    public void setAggWinningBalance(Float aggWinningBalance) {
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

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }


}
