package com.dotpot.app.models;

import com.dotpot.app.R;
import com.dotpot.app.utils.ResourceUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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


    public static String wrap(float amount) {
        return ResourceUtils.getString(R.string.currency)+" "+amount;
    }
}
