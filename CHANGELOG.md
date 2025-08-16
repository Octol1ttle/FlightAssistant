This is Alpha 12 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## New features

- **Added missing ability to turn off status message display from the config screen**

## Changes

- The attitude display will no longer be shown when the camera is in mirrored third-person mode
- The `THR` status message will no longer appear if thrust is set to 0% and there's no thrust source available
- A separate Flight Mode message will be shown when thrust is in reverse
- Improved detection of modded elytras
- Improved ECAM Actions text for the `F/CTL PROT LOST` alert

## Fixed issues

- **Fixed crashing on Forge**
- **Fixed mod not being present on NeoForge**
- Fixed elytra automatically opening/closing even when `Enable safety features` is set to OFF
- Fixed automatic thrust reduction occurring even when `Enable safety features` is set to OFF
- Fixed `FRWK EXPLOSIVE` alert appearing even when `Enable safety features` is set to OFF
- Fixed missing fault alert for the HUD Display Data Computer
- Fixed missing translations for Status Display in the Display Management Screen