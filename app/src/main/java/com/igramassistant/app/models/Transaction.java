package com.igramassistant.app.models;

import androidx.annotation.DrawableRes;

import com.igramassistant.app.Constants;
import com.igramassistant.app.R;
import com.igramassistant.app.utils.ResourceUtils;
import com.igramassistant.app.utl;
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


    public int getStatusColor(){
        switch (getStatus()){
            case Constants
                    .TXN_SUCCESS:
                if(getDebitOrCredit().equals(Constants.TXN_TYPE_DEBIT))
                    return R.color.colorAccent;
                else
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
