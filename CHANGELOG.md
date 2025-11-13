This is Beta 1 of a major update to FlightAssistant. All features planned for this update have been implemented, but there may be major issues.
Please use [Discord](https://discord.gg/5kcBCvnbTp) or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this beta or report any bugs.

## New features
- **Added flight plans**
  - Flight plans consist of departure information, enroute information, and arrival information
  - Use the FlightAssistant Setup screen to enter data
  - Flight plans can be saved to disk and loaded from disk
- **Added automatic landings using flight plans**
  - For automatic landings to work, there must be a "cruise" enroute waypoint (one with highest altitude among all waypoints), and an "approach" waypoint (the last waypoint in the list)
  - Entering correct data is necessary for automatic landings to function correctly
  - Pressing the "Set thrust to TOGA" key during landing will perform an automatic go around (if autopilot is engaged)
- **Added new alerts**
  - `GPWS BELOW G/S` ("below glide slope") - triggered when the player descends too low during a landing
  - `GPWS DON'T SINK` - triggered when the player starts descending during a takeoff or go around
  - `GPWS REACHED MINIMUMS` - triggered when the player descends below minimums specified in the arrival information page
  - `F/PLAN DEPART ELEV DISAGREE` - triggered when the entered departure elevation is incorrect
  - `F/PLAN ARRIVAL ELEV DISAGREE` - triggered when the entered arrival elevation is incorrect. Glide slope will be inaccurate
  - `F/PLAN OBSTACLES ON PATH` (aural alert: "CAUTION: TERRAIN") - triggered when there are obstacles on takeoff, enroute or arrival paths
  - `F/PLAN DESCENT TOO STEEP` - triggered when the flight plan contains a descent that requires an unsafe sink rate
- **Added new display: Course Deviation Display**
  - This display shows deviation when on a flight plan
  - The purple rectangle represents the target and is a command indicator - the camera must be moved in whichever direction the rectangle is deviating from the middle
- **Added safety feature from v2: "Lock fireworks near obstacles"**
  - Enabled by default
  - Prevents firework usage when the camera is pointed at an obstacle
  - Can be overriden with the Global Automation Override
- The current flight phase, distance from waypoint, and time from waypoint are now shown on the HUD
- Maximum pitch with no thrust, optimum glide pitch and max safe descent pitch are now shown on the HUD
- When closing a screen, its parent screen will now be opened
- Pressing the "Open FlightAssistant Setup screen" keybind while the setup screen is already open will now close it

## Changes

- **The sensitivity of the Ground Proximity Warning System now reduces dynamically based on the player's surroundings**
- **The Ground Proximity Warning System will no longer issue a `PULL UP` command if doing so would cause a collision anyway**
- **Replaced "Manual pitch override" keybind with "Global automation override"**
  - When held, auto thrust and autopilot disconnect automatically
  - When held, all flight protections are disabled
- Adjusted the autopilot control code to increase vertical navigation performance
- Adjusted the position of elements in the Auto Flight Screen based on suggestions from mctaylors
- Flight directors now rotate with roll
- Flight directors are no longer displayed when in mirrored third-person
- Flight directors are no longer disabled when entering unloaded chunks or losing flight protections
- Flight directors movements are now smoother
- Maximum and minimum pitch indicator movements are now smoother
- The decimal separator is now the same across all languages

## Fixed issues

- **Fixed an issue that caused the mod to unintentionally deploy the Elytra when climbing ladders**
- **Fixed an issue that caused ground proximity alerts to still appear even when disabled**
- Fixed some issues that caused the System Management Screen to behave incorrectly on different screen resolutions and languages
- Fixed an issue where the target Z coordinate on the HUD was incorrect
- Fixed an issue where the horizon would be drawn with the wrong color
- Fixed an issue where the target reading would be shown even if `Show Automation Modes` is disabled
- Fixed an issue that caused Do a Barrel Roll thrust to still be active even when the Thrust Computer has been disabled or faulted
- Fixed an issue that caused `FRWK SLOW RESPONSE` alert to trigger when it shouldn't, and not trigger when it should
- Fixed an issue that caused `ALERT SYS FAULT` alert to not disappear when the Alert Computer is reset