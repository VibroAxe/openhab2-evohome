/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.models;

import org.openhab.binding.evohome.internal.api.models.v2.response.Locations;

/**
 * Facade to the evohome installation information
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class EvohomeConfiguration {
    private final Locations installationInfo;

    public EvohomeConfiguration(Locations installationInfo) {
        this.installationInfo = installationInfo;
    }

    public Locations getLocations() {
        return installationInfo;
    }

}
