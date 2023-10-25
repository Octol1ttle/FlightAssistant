<p align=center>
    <img src="images/logo.png">
</p>

<p align=center>
    <a href="https://modrinth.com/mod/flightassistant">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.1.2/assets/cozy/available/modrinth_vector.svg"
            alt="Available on Modrinth"></img></a>
    <a href="https://modrinth.com/mod/fabric-api/">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg"
            alt="Requires Fabric API"></img></a>
    <!-- uncomment if Forge version is released (https://github.com/Octol1ttle/FlightAssistant/pull/2#discussion_r1370700775)
    <a href="https://modrinth.com/mod/flightassistant/versions?l=forge">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.1.2/assets/cozy/supported/forge_vector.svg"
            alt="Available for Forge"></img></a>
    -->
</p>

---

**FlightAssistant** is a client side Minecraft mod that adds a flight style HUD
(like one you would see in a flight simulator), autopilot systems, flight protections, and some more features.

## Main Features

- Flight HUD
- Stall protection<sup>1</sup>, warning and automatic recovery<sup>2</sup>
- Ground Proximity Warning System (GPWS):
  - Sinkrate warning, protection<sup>1</sup> and automatic recovery<sup>2</sup>
  - Unsafe terrain clearance warning, protection<sup>1</sup> and automatic recovery<sup>2</sup>
- Autopilot systems:
  - Automatic path calculation for given coordinates (1.0 only)
  - Flight planner (2.0 only)
  - Flight Directors
  - Auto thrust (fireworks)
  - Auto pilot
- Alerts for unsafe flight conditions:
  - Approaching void damage altitude (2.0 only)
  - Firework delayed response/no response
  - Low elytra durability
  - Unsafe (explosive) fireworks
  - Low firework count/no fireworks in inventory
  - Passengers dismounted mid-flight

<sup>1</sup> Protection via blocked pitch changes<br>
<sup>2</sup> Automatic recovery via automatic pitch changes and firework usage (2.0 only, when applicable)

## HUD Features

<details>
    <summary>Diagram</summary>
    <img src="images/diagram.png"></img>
</details>

1. Heading
2. Positive pitch
3. Pitch ladder
4. Optimum glide pitch
5. Speed
6. Altitude
7. Negative pitch
8. Coordinates (X / Z)
9. Ground Speed
10. Elytra Health
11. Vertical Speed
12. Height above ground/void

#
<sup><b>FlightAssistant</b> is an unofficial continuation of <a href="https://github.com/frodare/FlightHud">FlightHUD</a> mod by <a href="https://github.com/frodare">frodare</a>.</sup>
