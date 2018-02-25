package org.openhab.binding.evohome.internal.models;

import org.openhab.binding.evohome.internal.api.models.v2.response.GatewayStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationsStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.TemperatureControlSystemStatus;

public class EvohomeStatus {

    private final LocationsStatus installationStatus;

    public EvohomeStatus(LocationsStatus installationStatus) {
        this.installationStatus = installationStatus;
    }

    /**
     * Lookup the status of the Temperature Control System (TCS)
     *
     * @param tcsId the Id of the TCS
     * @return The status of the TCS or null when not found
     */
    public TemperatureControlSystemStatus getTemperatureControlSystem(String tcsId) {
        for (LocationStatus location : installationStatus) {
            for (GatewayStatus gateway : location.gateways) {
                for (TemperatureControlSystemStatus tcs : gateway.temperatureControlSystems) {
                    if (tcsId.equals(tcs.systemId)) {
                        return tcs;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Lookup the status of the Gateway linked to the TCS
     *
     * @param tcsId the Id of the TCS
     * @return The status of the gateway linked to the TCS or null when not found
     */

    public GatewayStatus getGateway(String tcsId) {
        for (LocationStatus location : installationStatus) {
            for (GatewayStatus gateway : location.gateways) {
                for (TemperatureControlSystemStatus tcs : gateway.temperatureControlSystems) {
                    if (tcsId.equals(tcs.systemId)) {
                        return gateway;
                    }
                }
            }
        }
        return null;
    }

}
