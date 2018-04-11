/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.evohome.internal.api.models.v2.request.HeatSetPoint;
import org.openhab.binding.evohome.internal.api.models.v2.request.Mode;
import org.openhab.binding.evohome.internal.api.models.v2.response.Authentication;
import org.openhab.binding.evohome.internal.api.models.v2.response.Location;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.Locations;
import org.openhab.binding.evohome.internal.api.models.v2.response.LocationsStatus;
import org.openhab.binding.evohome.internal.api.models.v2.response.UserAccount;
import org.openhab.binding.evohome.internal.configuration.EvohomeAccountConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the evohome client V2 api
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class EvohomeApiClientV2 implements EvohomeApiClient {

    private static final String APPLICATION_ID = "b013aa26-9724-4dbd-8897-048b9aada249";

    private final Logger logger = LoggerFactory.getLogger(EvohomeApiClientV2.class);

    private final SslContextFactory sslContextFactory = new SslContextFactory();
    private final HttpClient httpClient = new HttpClient(sslContextFactory);
    private final EvohomeAccountConfiguration configuration;
    private final ApiAccess apiAccess;

    private UserAccount useraccount = null;
    private Locations locations = null;
    private LocationsStatus locationsStatus = null;

    /**
     * Creates a new API client based on the V2 API interface
     *
     * @param configuration The configuration of the account to use
     * @throws Exception
     */
    public EvohomeApiClientV2(EvohomeAccountConfiguration configuration) throws Exception {
        this.configuration = configuration;

        try {
            httpClient.start();
        } catch (Exception e) {
            logger.error("Could not start http client", e);
            throw new EvohomeApiClientException("Could not start http client", e);
        }

        apiAccess = new ApiAccess(httpClient);
        apiAccess.setApplicationId(APPLICATION_ID);
    }

    /**
     * Closes the current connection to the API
     */
    public void close() {
        apiAccess.setAuthentication(null);
        useraccount = null;
        locations = null;
        locationsStatus = null;

        if (httpClient.isStarted()) {
            try {
                httpClient.stop();
            } catch (Exception e) {
                logger.debug("Could not stop http client.", e);
            }
        }
    }

    @Override
    public boolean login() {
        boolean success = authenticateWithUsername();

        // If the authentication succeeded, gather the basic intel as well
        if (success) {
            useraccount = requestUserAccount();
            locations = requestLocations();
            update();
        } else {
            apiAccess.setAuthentication(null);
            logger.error("Authorization failed");
        }

        return success;
    }

    @Override
    public void logout() {
        close();
    }

    @Override
    public void update() {
        updateAuthentication();
        locationsStatus = requestLocationsStatus();
    }

    @Override
    public Locations getInstallationInfo() {
        return locations;
    }

    @Override
    public LocationsStatus getInstallationStatus() {
        return locationsStatus;
    }

    @Override
    public void setTcsMode(String tcsId, String mode) {
        String url = String.format(EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_MODE, tcsId);
        apiAccess.doAuthenticatedRequest(HttpMethod.PUT, url, null, new Mode(mode), null, null);
    }

    @Override
    public void setHeatingZoneOverride(String zoneId, double setPoint) {
        setHeatingZoneOverride(zoneId, new HeatSetPoint(setPoint));
    }

    @Override
    public void cancelHeatingZoneOverride(String zoneId) {
        setHeatingZoneOverride(zoneId, new HeatSetPoint());
    }

    private void setHeatingZoneOverride(String zoneId, HeatSetPoint heatSetPoint) {
        String url = EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_HEAT_SETPOINT;
        url = String.format(url, zoneId);
        apiAccess.doAuthenticatedRequest(HttpMethod.PUT, url, null, heatSetPoint, null, null);
    }

    private UserAccount requestUserAccount() {
        String url = EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_ACCOUNT;

        UserAccount userAccount = new UserAccount();
        return apiAccess.doAuthenticatedRequest(HttpMethod.GET, url, null, null, UserAccount.class, userAccount);
    }

    private Locations requestLocations() {
        Locations locations = null;
        if (useraccount != null) {
            String url = EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_INSTALLATION_INFO;
            url = String.format(url, useraccount.userId);

            locations = new Locations();
            locations = apiAccess.doAuthenticatedRequest(HttpMethod.GET, url, null, null, Locations.class, locations);
        }

        return locations;
    }

    private LocationsStatus requestLocationsStatus() {
        LocationsStatus locationsStatus = null;

        if (locations != null) {
            locationsStatus = new LocationsStatus();

            for (Location location : locations) {
                String url = EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_LOCATION_STATUS;
                url = String.format(url, location.locationInfo.locationId);
                LocationStatus status = new LocationStatus();
                status = apiAccess.doAuthenticatedRequest(HttpMethod.GET, url, null, null, LocationStatus.class,
                        status);
                locationsStatus.add(status);
            }
        }

        return locationsStatus;
    }

    private boolean authenticate(String credentials, String grantType) {
        Authentication authentication = new Authentication();

        String data = credentials + "&" + "Host=rs.alarmnet.com%2F&" + "Pragma=no-cache&"
                + "Cache-Control=no-store+no-cache&"
                + "scope=EMEA-V1-Basic+EMEA-V1-Anonymous+EMEA-V1-Get-Current-User-Account&" + "grant_type=" + grantType
                + "&" + "Content-Type=application%2Fx-www-form-urlencoded%3B+charset%3Dutf-8&"
                + "Connection=Keep-Alive";

        Map<String, String> headers = new HashMap<>();
        String basicAuth = Base64.getEncoder().encodeToString((APPLICATION_ID + ":test").getBytes());
        headers.put("Authorization", "Basic " + basicAuth);
        headers.put("Accept", "application/json, application/xml, text/json, text/x-json, text/javascript, text/xml");

        authentication = apiAccess.doRequest(HttpMethod.POST, EvohomeApiConstants.URL_V2_AUTH, headers, data,
                "application/x-www-form-urlencoded", Authentication.class, authentication);

        apiAccess.setAuthentication(authentication);

        if (authentication != null) {
            authentication.systemTime = System.currentTimeMillis() / 1000;
        }

        return (authentication != null);
    }

    private boolean authenticateWithUsername() {
        boolean result = false;

        try {
            String credentials = "Username=" + URLEncoder.encode(configuration.username, "UTF-8") + "&" + "Password="
                    + URLEncoder.encode(configuration.password, "UTF-8");
            result = authenticate(credentials, "password");
        } catch (UnsupportedEncodingException e) {
            logger.error("Credential conversion failed", e);
        }

        return result;
    }

    private boolean authenticateWithToken(String accessToken) {
        String credentials = "refresh_token=" + accessToken;
        return authenticate(credentials, "refresh_token");
    }

    private void updateAuthentication() {
        Authentication authentication = apiAccess.getAuthentication();
        if (authentication == null) {
            authenticateWithUsername();
        } else {
            // Compare current time to the expiration time minus four intervals for slack
            long currentTime = System.currentTimeMillis() / 1000;
            long expiration = authentication.systemTime + authentication.expiresIn;
            expiration -= 4 * configuration.refreshInterval;

            // Update the access token just before it expires, but fall back to username and password
            // when it fails (i.e. refresh token had been invalidated)
            if (currentTime > expiration) {
                authenticateWithToken(authentication.refreshToken);
                if (apiAccess.getAuthentication() == null) {
                    authenticateWithUsername();
                }
            }
        }
    }

}
