package com.dotpot.app.models;

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
}
