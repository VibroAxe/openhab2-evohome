package org.openhab.binding.evohome.internal.api.models.v2.response;

import com.google.gson.annotations.SerializedName;

public class HeatSetpointStatus {

    @SerializedName("targetTemperature")
    public double targetTemperature;

    @SerializedName("setpointMode")
    public String setpointMode;

}
