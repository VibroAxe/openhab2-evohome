/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
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
package org.openhab.binding.evohome.internal;

import java.util.Hashtable;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.discovery.EvohomeDiscoveryService;
import org.openhab.binding.evohome.handler.EvohomeAccountBridgeHandler;
import org.openhab.binding.evohome.handler.EvohomeHeatingZoneHandler;
import org.openhab.binding.evohome.handler.EvohomeTemperatureControlSystemHandler;
import org.osgi.framework.ServiceRegistration;

/**
 * Provides the thing factory for this binding
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class EvohomeHandlerFactory extends BaseThingHandlerFactory {

    private ServiceRegistration<?> discoveryServiceReg;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return EvohomeBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(EvohomeBindingConstants.THING_TYPE_EVOHOME_ACCOUNT)) {
            EvohomeAccountBridgeHandler bridge = new EvohomeAccountBridgeHandler((Bridge) thing);
            registerEvohomeDiscoveryService(bridge);
            return bridge;
        } else if (thingTypeUID.equals(EvohomeBindingConstants.THING_TYPE_EVOHOME_DISPLAY)) {
            return new EvohomeTemperatureControlSystemHandler(thing);
        } else if (thingTypeUID.equals(EvohomeBindingConstants.THING_TYPE_EVOHOME_HEATING_ZONE)) {
            return new EvohomeHeatingZoneHandler(thing);
        }

        return null;
    }

    private void registerEvohomeDiscoveryService(EvohomeAccountBridgeHandler evohomeBridgeHandler) {
        EvohomeDiscoveryService discoveryService = new EvohomeDiscoveryService(evohomeBridgeHandler);
        discoveryServiceReg = bundleContext.registerService(DiscoveryService.class.getName(), discoveryService,
                new Hashtable<String, Object>());
    }

    @Override
    public ThingHandler registerHandler(Thing thing) {
        return super.registerHandler(thing);
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (discoveryServiceReg != null && thingHandler.getThing().getThingTypeUID()
                .equals(EvohomeBindingConstants.THING_TYPE_EVOHOME_ACCOUNT)) {
            discoveryServiceReg.unregister();
            discoveryServiceReg = null;
        }
        super.removeHandler(thingHandler);
    }

}
