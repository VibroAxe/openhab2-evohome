/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.configuration.EvohomeTemperatureControlSystemConfiguration;
import org.openhab.binding.evohome.internal.api.models.v2.response.GatewayStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.TemperatureControlSystemStatus;
import org.openhab.binding.evohome.internal.models.EvohomeStatus;

/**
 * Handler for a temperature control system. Gets and sets global system mode.
 *
 * @author Jasper van Zuijlen
 *
 */
public class EvohomeTemperatureControlSystemHandler extends BaseEvohomeHandler {
    private EvohomeTemperatureControlSystemConfiguration configuration;
    private EvohomeStatus status;

    public EvohomeTemperatureControlSystemHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(EvohomeTemperatureControlSystemConfiguration.class);
        checkConfig();
    }

    @Override
    public void dispose() {
        configuration = null;
    }

    @Override
    public void update(EvohomeStatus status) {
        this.status = status;
        if (getThing().getStatus() == ThingStatus.ONLINE) {
            TemperatureControlSystemStatus tcsStatus = status.getTemperatureControlSystem(configuration.id);
            GatewayStatus gatewayStatus = status.getGateway(configuration.id);

            if (tcsStatus != null && gatewayStatus != null) {
                if (handleActiveFaults(gatewayStatus) == false) {
                    updateEvohomeThingStatus(ThingStatus.ONLINE);
                    updateState(EvohomeBindingConstants.SYSTEM_MODE_CHANNEL, new StringType(tcsStatus.mode.mode));
                }
            } else {
                updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Status not found, check the id");
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {
            update(status);
        } else if (channelUID.getId().equals(EvohomeBindingConstants.SYSTEM_MODE_CHANNEL)) {
            EvohomeAccountBridgeHandler bridge = getEvohomeBridge();
            if (bridge != null) {
                bridge.setTcsMode(configuration.id, command.toString());
            }
        }
    }

    private boolean handleActiveFaults(GatewayStatus gatewayStatus) {
        if (gatewayStatus.activeFaults.size() > 0) {
            updateEvohomeThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    gatewayStatus.activeFaults.get(0).faultType);
            return true;
        }
        return false;
    }

    private void checkConfig() {
        try {
            if (configuration == null) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Configuration is missing or corrupted");
            } else if (StringUtils.isEmpty(configuration.id)) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Id not configured");
            } else {
                updateStatus(ThingStatus.ONLINE);
            }
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, e.getMessage());
        }
    }
}
