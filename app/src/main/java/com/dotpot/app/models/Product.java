package com.dotpot.app.models;

import com.dotpot.app.R;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("amount")
    @Expose
    private Long amount;
    @SerializedName("timeStamp")
    @Expose
    private Long timeStamp;
    @SerializedName("expires")
    @Expose
    private Long expires;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("redeemsLeft")
    @Expose
    private Long redeemsLeft;
    @SerializedName("redeemers")
    @Expose
    private String redeemers;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("terms")
    @Expose
    private String terms;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("productid")
    @Expose
    private String productid;

    public transient static final String TYPE_EARN = "earn";

    public ActionItem actionItem(){
        try{
            ActionItem item = utl.js.fromJson(terms,ActionItem.class);
            return item;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String expiry(){
        return String.format(ResourceUtils.getString(R.string.expires), utl.getDateFormatted(new Date(expires)));
    }
}
