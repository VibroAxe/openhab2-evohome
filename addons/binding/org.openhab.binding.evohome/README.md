# evohome Binding

This binding integrates the Honeywell evohome system.
It uses your Honeywell Total Connect Comfort account to access your locations and heating zones.

## Supported Things

The binding supports the following things:

* evohome Account
* evotouch control display
* Heating zones

### evohome Account

This thing functions as the bridge between all the other things.
It contains your credentials and connects to the Honeywell web API.


### evotouch

This thing represents the central display controller.
It is used to view and change the current system mode.

### Heating zone

The heating zone thing represents the evohome heating zone.
It displays the current temperature, the temperature set point and the status of the set point.
It also allows you to permanently override the current temperature set point as well as canceling any active overrides. 

## Discovery

After setting up the evohome account, the evotouch and heating zones available to your account will be discovered after a manual scan.

## Thing Configuration

Thing configuration is optional, it is easier to use discovery which will automatically add all your zones and displays to the inbox, once the account Thing is online.

To manually configure the account Thing in a things file you can use:

    Bridge evohome:account:your_account_alias [ username="your_user_name", password="your_password" ]

Adding displays or zones can be done using this:

    Bridge evohome:account:your_account_alias [ username="your_user_name", password="your_password" ]
    {
	    display your_display_alias  [ id="your_display_id_here" ]
	    heatingzone your_zone_alias [ id="your_zone_id_here" ]
    }

You can define multiple displays and zones this way

## Channels

### account

None

### Display

| Channel Type ID | Item Type | Description                                                                                                        |
|-----------------|-----------|--------------------------------------------------------------------------------------------------------------------|
| Mode            | String    | Allows to view or set the system mode. Supported values are: Auto, Auto, WithEco, Away, DayOff, HeatingOff, Custom |

### Zone

| Channel Type ID   | Item Type | Description                                                                                                                                                                                                            |
|-------------------|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Temperature       | Number    | Allows for viewing the current actual temperature of the zone.                                                                                                                                                         |
| CurrentSetPoint   | Number    | Allows for viewing the current temperature set point of the zone.                                                                                                                                                      |
| SetPointStatus    | String    | Allows for viewing the current set point mode of the zone.                                                                                                                                                             |
| PermanentSetPoint | Number    | Allows for permanently overriding the temperature set point of the zone.                                                                                                                                               |
| CancelSetPoint    | Switch    | Allowing for cancelling any active temperature overrides on the zone and allowing it to follow the active schedule. Activate by sending `ON` to the channel; it will auto-reset to `OFF`. Sending `OFF` has no effect. |

## Full Example

### demo.things

    Bridge evohome:account:your_account_alias [ username="your_user_name", password="your_password" ]
    {
	    display your_display_alias  [ id="1234" ]
	    heatingzone your_zone_alias [ id="5678" ]
    }
	
	
### demo.items	

	// evohome Display
	String DemoMode                 { channel="evohome:display:your_account_alias:your_display_alias:SystemMode" }

	// evohome Heatingzone
	Number DemoZoneTemperature      { channel="evohome:heatingzone:your_account_alias:your_zone_alias:Temperature" }
	Number DemoZoneSetPointValue    { channel="evohome:heatingzone:your_account_alias:your_zone_alias:CurrentSetPoint" }
	String DemoZoneSetPointStatus   { channel="evohome:heatingzone:your_account_alias:your_zone_alias:SetPointStatus" }
	Number DemoZoneSetPointOverride { channel="evohome:heatingzone:your_account_alias:your_zone_alias:PermanentSetPoint" }
	Switch DemoZoneSetPointCancel   { channel="evohome:heatingzone:your_account_alias:your_zone_alias:CancelSetPoint" }
	
### demo.sitemap
	
	sitemap evohome label="evohome Menu"
	{
		Frame label="evohome display" {
			Selection label="[%s]" item=DemoMode mappings=[
			  "Auto"="Normal",
			  "AutoWithEco"="Eco",
			  "Away"="Away",
			  "DayOff"="Day Off",
			  "HeatingOff"="Off",
			  "Custom"="Custom"
			]
		}
		
		Frame label="evohome heating zone" {
			Text     label="Temperature"          item=DemoZoneTemperature      
			Text     label="Set point"            item=DemoZoneSetPointValue    
			Text     label="Status"               item=DemoZoneSetPointStatus   
			Setpoint label="Permanent override"   item=DemoZoneSetPointOverride minValue=5 maxValue=35 step=0.5
			Switch   label="Cancel override"      item=DemoZoneSetPointCancel   
		}
	}

