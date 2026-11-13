This is Alpha 1 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## Changes
- **Removed `THRUST LOCKED`**
  - When automation stops controlling thrust, the thrust will now return to the last set manual thrust. 
- **Reworked the Flight Plan screen**
  - This changes the flight plan format. The mod can load flight plans from versions 3.0.x, but this may be removed in a future release. Save the flight plan after loading it to convert it to the new format 
- **Reworked the configuration screen**
  - This changes the configuration format. Some settings may be lost.
- **Pitch is no longer restricted when flying above the void at high altitudes**
- **Opening FlightAssistant screens will now pause the game in singleplayer**
- Added back keybinds from v2: `Toggle flight directors`, `Toggle auto thrust`, `Toggle autopilot`
- Added alert dismiss/recall buttons on the Setup Screen
- Distance and time now appear on the Status Display when using the `COORDS` lateral mode 
- Added a config option `Autopilot resistance threshold`
  - Adjust it if it is too easy or too difficult to disconnect the autopilot by moving the camera
- Added a button to open the Wiki in the Setup Screen
- Changed how Ground Speed and Vertical Spped are displayed on the HUD
- Text now refers to 100% thrust as `MAX` instead of `TOGA`
- Disabling the Ground Proximity computer will now trigger `F/CTL PROTECT LOST`
- Reduced the threshold required to block firework activation for obstacles

## Fixed issues
- **Fixed severe lag when the player was around Sable sublevels (Create: Aeronautics contraptions)**
- **Fixed flight directors lagging behind the camera**
- Fixed dashed lines having inconsistent width
- Fixed incorrect rounding in Coordinates Display
- Fixed vanilla master volume not being respected when playing alert sounds in 1.21.9+
- Fixed `F/PLAN DESCENT TOO STEEP` alert not appearing when it should
- Fixed `R/ALT` flashing when the player was moving onto higher surfaces while on the ground 
- Fixed an issue that caused FlightAssistant to not appear in the Profiler (pie chart)
