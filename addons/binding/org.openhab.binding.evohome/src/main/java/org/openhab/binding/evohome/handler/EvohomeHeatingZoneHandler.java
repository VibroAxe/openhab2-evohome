/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.internal.api.models.v2.response.ZoneStatus;

/**
 * The {@link EvohomeHeatingZoneHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jasper van Zuijlen - Initial contribution
 * @author Neil Renaud - Working implementation
 * @author Jasper van Zuijlen - Refactor + Permanent Zone temperature setting
 */
public class EvohomeHeatingZoneHandler extends BaseEvohomeHandler {
    private ThingStatus tcsStatus;
    private ZoneStatus zoneStatus;

    public EvohomeHeatingZoneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(ThingStatus tcsStatus, ZoneStatus zoneStatus) {
        this.tcsStatus = tcsStatus;
        this.zoneStatus = zoneStatus;

        // Make the zone offline when the related display is offline
        // If the related display is not a thing, ignore this
        if (tcsStatus != null && tcsStatus.equals(ThingStatus.OFFLINE)) {
            updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Display Controller offline");
        } else if (zoneStatus == null) {
            updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Status not found, check the zone id");
        } else if (handleActiveFaults(zoneStatus) == false) {
            updateEvohomeThingStatus(ThingStatus.ONLINE);

            updateState(EvohomeBindingConstants.ZONE_TEMPERATURE_CHANNEL,
                    new DecimalType(zoneStatus.temperature.temperature));
            updateState(EvohomeBindingConstants.ZONE_CURRENT_SET_POINT_CHANNEL,
                    new DecimalType(zoneStatus.heatSetpoint.targetTemperature));
            updateState(EvohomeBindingConstants.ZONE_PERMANENT_SET_POINT_CHANNEL,
                    new DecimalType(zoneStatus.heatSetpoint.targetTemperature));
            updateState(EvohomeBindingConstants.ZONE_SET_POINT_STATUS_CHANNEL,
                    new StringType(zoneStatus.heatSetpoint.setpointMode));
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (command == RefreshType.REFRESH) {
            update(tcsStatus, zoneStatus);
        } else {
            EvohomeAccountBridgeHandler bridge = getEvohomeBridge();
            if (bridge != null) {
                String channelId = channelUID.getId();
                if (channelId.equals(EvohomeBindingConstants.ZONE_PERMANENT_SET_POINT_CHANNEL)
                        && command instanceof DecimalType) {
                    bridge.setPermanentSetPoint(getEvohomeThingConfig().id, ((DecimalType) command).doubleValue());
                } else if (channelId.equals(EvohomeBindingConstants.ZONE_CANCEL_SET_POINT_CHANNEL)
                        && command instanceof OnOffType) {
                    OnOffType onOff = (OnOffType) command;
                    if (onOff == OnOffType.ON) {
                        bridge.cancelSetPointOverride(getEvohomeThingConfig().id);
                        postCommand(channelUID, OnOffType.OFF);
                    }
                }
            }

        }
    }

    private boolean handleActiveFaults(ZoneStatus zoneStatus) {
        if (zoneStatus.activeFaults.size() > 0) {
            updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    zoneStatus.activeFaults.get(0).faultType);
            return true;
        }
        return false;
    }

}
