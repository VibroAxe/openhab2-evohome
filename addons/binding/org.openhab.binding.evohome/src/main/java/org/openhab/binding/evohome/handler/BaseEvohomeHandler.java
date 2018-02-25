/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.evohome.internal.models.EvohomeConfiguration;
import org.openhab.binding.evohome.internal.models.EvohomeStatus;

/**
 * Base class for an evohome handler
 *
 * @author Jasper van Zuijlen - Initial contribution
 */
public abstract class BaseEvohomeHandler extends BaseThingHandler {

    public BaseEvohomeHandler(Thing thing) {
        super(thing);
    }

    /**
     * Updates the Thing with the current status
     *
     * @param status The status all locations
     */
    public abstract void update(EvohomeStatus status);

    /**
     * Retrieves the bridge
     *
     * @return The evohome brdige
     */
    protected EvohomeAccountBridgeHandler getEvohomeBridge() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            return (EvohomeAccountBridgeHandler) bridge.getHandler();
        }

        return null;
    }

    /**
     * Retrieves the evohome configuration from the bridge
     *
     * @return The current evohome configuration
     */
    protected EvohomeConfiguration getEvohomeConfig() {
        EvohomeAccountBridgeHandler bridge = getEvohomeBridge();
        if (bridge != null) {
            return bridge.getEvohomeConfig();
        }

        return null;
    }

    /**
     * Retrieves the evohome configuration from the bridge
     *
     * @return The current evohome configuration
     */
    protected void requestUpdate() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            ((EvohomeAccountBridgeHandler) bridge).getEvohomeConfig();
        }
    }

    /**
     * Updates the status of the evohome thing when it changes
     *
     * @param newStatus The new status to update to
     */
    protected void updateEvohomeThingStatus(ThingStatus newStatus) {
        updateEvohomeThingStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    /**
     * Updates the status of the evohome thing when it changes
     *
     * @param newStatus The new status to update to
     * @param detail The status detail value
     * @param message The message to show with the status
     */
    protected void updateEvohomeThingStatus(ThingStatus newStatus, ThingStatusDetail detail, String message) {
        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
        }
    }

}
