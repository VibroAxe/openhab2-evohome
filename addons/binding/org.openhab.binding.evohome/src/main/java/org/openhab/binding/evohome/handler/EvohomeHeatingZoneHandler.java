/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
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
        }

        /*
         * if (tcsStatus == null || zoneStatus == null) {
         * updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
         * "Status not found, check the zone id");
         * }
         * else if (handleActiveFaults(zoneStatus) == false) {
         * updateState(EvohomeBindingConstants.SYSTEM_MODE_CHANNEL, new StringType(tcsStatus.mode.mode));
         * updateEvohomeThingStatus(ThingStatus.ONLINE);
         * }
         */

    }

    /*
     * @Override
     * public void update() {
     *
     * // getTcsStatus(getEvohomeThingConfig().id);
     *
     * if (getThing().getStatus() == ThingStatus.ONLINE) {
     * ZoneStatus tcsStatus = getStatus().getZone(getEvohomeThingConfig().id);
     * GatewayStatus gatewayStatus = getStatus().getGateway(getEvohomeThingConfig().id);
     *
     * if (tcsStatus != null && gatewayStatus != null) {
     * /*
     * if (handleActiveFaults(gatewayStatus) == false) {
     * updateState(EvohomeBindingConstants.SYSTEM_MODE_CHANNEL, new StringType(tcsStatus.mode.mode));
     * }
     *
     * }else{updateEvohomeThingStatus(ThingStatus.OFFLINE,ThingStatusDetail.
     * COMMUNICATION_ERROR,"Status not found, check the display id");}}
     * // TODO Auto-generated method stub
     * // ; //TODO When TCS offline; zone offline
     * }
     */

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        /*
         * if (getBridge().getStatus() != ThingStatus.OFFLINE) {
         * if (command instanceof RefreshType) {
         * updateZoneStatus();
         * }
         * }
         */
    }

    /*
     * @Override
     * public void update(EvohomeApiClient client) {
     * updateZoneStatus();
     * }
     *
     * private void updateZoneStatus() {
     * ZoneStatus zoneStatus = getZoneStatus();
     *
     * if (zoneStatus == null) {
     * updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
     * return;
     * }
     * if (!zoneStatus.temperature.isAvailable) {
     * updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Zone is offline");
     * return;
     * }
     *
     * double temperature = zoneStatus.temperature.temperature;
     * double targetTemperature = zoneStatus.heatSetpoint.targetTemperature;
     * String mode = zoneStatus.heatSetpoint.setpointMode;
     *
     * updateState(EvohomeBindingConstants.TEMPERATURE_CHANNEL, new DecimalType(temperature));
     * updateState(EvohomeBindingConstants.CURRENT_SET_POINT_CHANNEL, new DecimalType(targetTemperature));
     * updateState(EvohomeBindingConstants.SET_POINT_STATUS_CHANNEL, new StringType(mode));
     *
     * updateStatus(ThingStatus.ONLINE);
     * }
     *
     *
     * private ZoneStatus getZoneStatus() {
     *
     * ZoneStatus zoneStatus = null;
     * if(getBridge() != null && getBridge().getHandler() != null){
     * EvohomeApiClient apiClient = ((EvohomeGatewayHandler) getBridge().getHandler()).getApiClient();
     * EvohomeHeatingZoneConfiguration configuration = getConfigAs(EvohomeHeatingZoneConfiguration.class);
     * zoneStatus = apiClient.getHeatingZone(configuration.locationId, configuration.zoneId);
     * }
     *
     * return zoneStatus;
     *
     * return null;
     * }
     *
     */
}
