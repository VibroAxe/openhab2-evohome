package org.openhab.binding.evohome.internal.api;

import org.openhab.binding.evohome.internal.api.models.ControlSystem;
import org.openhab.binding.evohome.internal.api.models.v1.DataModelResponse;
import org.openhab.binding.evohome.internal.api.models.v2.response.ZoneStatus;

public interface EvohomeApiClient {

    boolean login();

    void logout();

    void update();

    DataModelResponse[] getData();

    ControlSystem[] getControlSystems();

    ControlSystem getControlSystem(String id);

    ZoneStatus getHeatingZone(String locationId, String zoneId);

}