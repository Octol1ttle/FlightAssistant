This is a major update to FlightAssistant.
Please use [Discord](https://discord.gg/5kcBCvnbTp) or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this release or report any bugs.

**A wiki is now available: https://github.com/Octol1ttle/FlightAssistant/wiki**

## 3.1.0

- Added support for Minecraft 26.1
- Added a keybind to toggle Auto Thrust on its own, independent of the rest of the autopilot
- Added a keybind to toggle Flight Directors, Auto Thrust, and Autopilot together with one press
- Added a holding pattern autopilot mode: enter it with a keybind to hold at the waypoint you're currently flying to (or at your present position if there isn't one), or configure a fix and inbound course manually in the Autoflight setup screen
- Fixed the attitude display's pitch-limit indicators sometimes appearing far more than intended, especially at low speed

## Changes from Beta 1

- Added Chinese localization
- Changed the default durability display style to Time
- Made the `BELOW G/S` warning alert show when deviation is more than 2.5 blocks and the player is below minimums
- Reduced sensitivity of the `DON'T SINK` alert
- Adjusted the attitude display to account for crosshair offset
- Adjusted the way vertical deviation is displayed
- Reduced the maximum recommended pitch to 45 degrees in the `F/CTL PROTECT LOST` alert text
- Fixed an issue that caused `TERRAIN AHEAD` and `PULL UP` alerts to appear only right before impact
- Fixed an issue that caused `AUTO FLT AP OFF` and `AUTO FLT A/THR OFF` alerts to not be displayed when they occur due to losing protections and chunk unloading
- Fixed an issue where DaBR thrust would be overriden even when Global Automation Override is active

If you weren't keeping up with changes in v3, here's a quick overview:

- HUD overhaul
    - New layout: <img src="https://github.com/Octol1ttle/FlightAssistant/wiki/img/hud/full_screenshot_alt.png">
    - The HUD data now updates at the render frame rate instead of game tick rate. Numbers and scales will move smoother
- Commands are gone and replaced with the Setup Screen (see below)
- Introduced the FlightAssistant Setup Screen
  - Opened with `Numpad Enter` by default
  - Used to reset and temporarily disable computers & displays
  - Used to configure the autopilot
  - Used to fill out the flight plan
- Various systems have been reworked and features have been added. For example, the GPWS now dynamically adjusts sensitivity based on surroundings
- Added Chinese localization