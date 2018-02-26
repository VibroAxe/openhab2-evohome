/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
  * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.handler.AccountStatusListener;
import org.openhab.binding.evohome.handler.EvohomeAccountBridgeHandler;
import org.openhab.binding.evohome.internal.api.models.v2.response.Gateway;
import org.openhab.binding.evohome.internal.api.models.v2.response.Location;
import org.openhab.binding.evohome.internal.api.models.v2.response.TemperatureControlSystem;
import org.openhab.binding.evohome.internal.api.models.v2.response.Zone;
import org.openhab.binding.evohome.internal.models.EvohomeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EvohomeDiscoveryService} class is capable of discovering the available data from Evohome
 *
 * @author Neil Renaud - Initial contribution
 * @author jasper van Zuijlen - Background discovery
 *
 */
public class EvohomeDiscoveryService extends AbstractDiscoveryService implements AccountStatusListener {
    private Logger logger = LoggerFactory.getLogger(EvohomeDiscoveryService.class);
    private static final int TIMEOUT = 5;

    private EvohomeAccountBridgeHandler bridge;
    private ThingUID bridgeUID;

    public EvohomeDiscoveryService(EvohomeAccountBridgeHandler bridge) {
        super(EvohomeBindingConstants.SUPPORTED_THING_TYPES_UIDS, TIMEOUT);
        this.bridge = bridge;

        this.bridgeUID = bridge.getThing().getUID();
        this.bridge.addAccountStatusListener(this);
    }

    @Override
    protected void startScan() {
        discoverDevices();
    }

    @Override
    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    @Override
    public void accountStatusChanged(ThingStatus status) {
        if (status.equals(ThingStatus.ONLINE)) {
            discoverDevices();
        }
    }

    @Override
    protected void deactivate() {
        super.deactivate();
        bridge.removeAccountStatusListener(this);
    }

    private void discoverDevices() {
        if (bridge.getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("Evohome Gateway not online, scanning postponed");
            return;
        }

        EvohomeConfiguration config = bridge.getEvohomeConfig();
        try {
            // Get all displays (TCSs) from all locations
            for (Location location : config.getLocations()) {
                for (Gateway gateway : location.gateways) {
                    for (TemperatureControlSystem tcs : gateway.temperatureControlSystems) {
                        addDisplayDiscoveryResult(location, tcs);
                        for (Zone zone : tcs.zones) {
                            // discoverHeatingZones(zone);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("{}", e.getMessage(), e);
        }

        stopScan();
    }

    private void addDisplayDiscoveryResult(Location location, TemperatureControlSystem tcs) {
        String id = tcs.systemId;
        String name = location.locationInfo.name;
        ThingUID thingUID = new ThingUID(EvohomeBindingConstants.THING_TYPE_EVOHOME_DISPLAY, bridgeUID, id);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put("id", id);
        properties.put("name", name);

        addDiscoveredThing(thingUID, properties, name);
    }

    private void addDiscoveredThing(ThingUID thingUID, Map<String, Object> properties, String displayLabel) {
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bridgeUID).withLabel(displayLabel).build();
        thingDiscovered(discoveryResult);
    };

    /*
     *
     *
     * @Override
     * public void startScan() {
     * if (evohomeGatewayHandler != null) {
     * try {
     * EvohomeApiClient client = evohomeGatewayHandler.getApiClient();
     * if (client != null) {
     * for (ControlSystem controlSystem : client.getControlSystems()) {
     * discoverDisplay(controlSystem);
     * discoverHeatingZones(controlSystem.getId(), controlSystem.getHeatingZones());
     * }
     * }
     * } catch (Exception e) {
     * logger.warn("{}", e.getMessage(), e);
     * }
     * }
     *
     * stopScan();
     * }
     *
     * private void discoverHeatingZones(String locationId, TemperatureControlSystem heatingZones) {
     * for (Zone zone : heatingZones.zones) {
     * String zoneName = zone.name;
     * String zoneId = zone.zoneId;
     * String modelType = zone.modelType;
     * String zoneType = zone.zoneType;
     *
     * ThingUID thingUID = findThingUID(EvohomeBindingConstants.THING_TYPE_EVOHOME_HEATING_ZONE.getId(), zoneName);
     *
     * Map<String, Object> properties = new HashMap<>();
     * properties.put(EvohomeBindingConstants.LOCATION_ID, locationId);
     * properties.put(EvohomeBindingConstants.ZONE_ID, zoneId);
     * properties.put(EvohomeBindingConstants.ZONE_NAME, zoneName);
     * properties.put(EvohomeBindingConstants.ZONE_TYPE, zoneType);
     * properties.put(EvohomeBindingConstants.ZONE_MODEL_TYPE, modelType);
     * addDiscoveredThing(thingUID, properties, zoneName);
     * }
     * }
     *
     *
     *
     * private ThingUID findThingUID(String thingType, String thingId) throws IllegalArgumentException {
     * for (ThingTypeUID supportedThingTypeUID : getSupportedThingTypes()) {
     * String uid = supportedThingTypeUID.getId();
     *
     * if (uid.equalsIgnoreCase(thingType)) {
     *
     * return new ThingUID(supportedThingTypeUID, evohomeGatewayHandler.getThing().getUID(),
     * thingId.replaceAll("[^a-zA-Z0-9_]", ""));
     * }
     * }
     *
     * throw new IllegalArgumentException("Unsupported device type discovered: " + thingType);
     * }
     */
}
