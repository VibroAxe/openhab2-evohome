/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationsStatus;

/**
 * Base class for an evohome handler
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
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
    public abstract void update(LocationsStatus status);

    /**
     * Gets the current system mode based on the Temperature Control System (TCS) id
     *
     * @param status The current status of all locations
     * @param id The id of the TCS
     */
    protected void getSystemModeStatus(LocationsStatus status, String tcsId) {

    }

}
