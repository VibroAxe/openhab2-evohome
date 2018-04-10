# evohome Binding
This binding connects your to your Honeywell evohome system, similar to what the offical app does. This means that an internet connection is needed. Currently it is possible to set the global system mode and read the values of setpoints and temperatures of individual zones.

## Supported Things
The binding supports the following things:

* evohome Account
* evotouch control display
* Heating zones

### evohome Account
This thing functions as the bridge between all the other things. It contains your credentials and connects to the Honeywell web API. 

### evotouch
This thing represents the central display. It is used to display and changes the current system mode.

### Heating zone
The heating zone thing represents the evohome heating zone. It displays the current temperature, the temperature set point and the status of the set point. It also allows you to permanently override the current temperature set point as well as canceling any active overrides. 

## Discovery
After setting up the evohome account, the evotouch and heating zones available to your account will be discovered after a manual scan.


## Binding Configuration
The evohome account needs to be configured with your username and password. This can be done via PaperUI or things file.

## Thing Configuration
Thing configuration is optional, it is easier to use discovery which will automatically add all your zones and displays to the inbox, once the account Thing is online.

To manually configure the account Thing in a things file you can use:

    Bridge evohome:account:your_account_alias [ username="your_user_name", password="your_password" ]

Adding displays or zones can be done using this:

    Bridge evohome:account:your_account_alias [ username="your_user_name", password="your_password" ]
    {
	    display your_display_alias  [ id="your_display_id_here" ]
	    heatingzone your_zone_alias [ id="your_zone_id_here" ]" ]
    }

You can define multiple displays and zones this way

## Channels
### account
None

### Display

| Channel | Type ID | Item Type | Description|
|---------|---------|-----------|------------|

### Zone


## Full Example
TBD
