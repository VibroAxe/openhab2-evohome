/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.api;

import org.openhab.binding.evohome.internal.api.models.v2.response.Locations;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationsStatus;

/**
 * Interface for interacting with a specific version of an evohome client. This interface currently supports one
 * account.
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public interface EvohomeApiClient {

    /**
     * Logs in the client
     *
     * @return True when successful, false otherwise
     */
    boolean login();

    /**
     * Logs out the client
     */
    void logout();

    /**
     * Updates the actual values of the current installation
     */
    void update();

    /**
     * Gets the information on the current installation of the authenticated user
     *
     * @return A list of locations with all the information on the installation
     */
    Locations getInstallationInfo();

    /**
     * Gets the current status of the installation
     *
     * @return A list of locations with the status of the installation
     */
    LocationsStatus getInstallationStatus();

    /**
     * Sets the mode of a particular Temperature Control System (TCS)
     *
     * @param tcsId The id of the TCS to use
     * @param mode The mode to set
     */
    void setTcsMode(String tcsId, String mode);

}