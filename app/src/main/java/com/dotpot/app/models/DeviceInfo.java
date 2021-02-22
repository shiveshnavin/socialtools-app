package com.dotpot.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    public String deviceId;
    public String country;
    public String countryCode;
    public String region;
    public String latLng;
    public String lang;

}
