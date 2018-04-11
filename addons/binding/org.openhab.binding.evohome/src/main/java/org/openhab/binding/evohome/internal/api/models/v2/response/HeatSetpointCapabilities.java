/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.api.models.v2.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the heat set point capabilities
 * 
 * @author Jasper van Zuijlen
 *
 */
public class HeatSetpointCapabilities {

    @SerializedName("maxHeatSetpoint")
    public double maxHeatSetpoint;

    @SerializedName("minHeatSetpoint")
    public double minHeatSetpoint;

    @SerializedName("valueResolution")
    public double valueResolution;

    @SerializedName("allowedSetpointModes")
    public List<String> allowedSetpointModes;

    @SerializedName("maxDuration")
    public String maxDuration;

    @SerializedName("timingResolution")
    public String timingResolution;

}
