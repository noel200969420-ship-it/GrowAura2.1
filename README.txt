GrowAura 1.0 - Fabric 1.21.9
Authors: Komi_san_100 & ChatGPT

Quick build & install instructions (IntelliJ):

1. Extract this ZIP anywhere, e.g. C:\Users\You\Projects\GrowAura or ~/GrowAura
2. Open IntelliJ IDEA -> Open -> select the extracted folder.
3. Wait for Gradle to load dependencies (automatic).
4. If prompted, set the Project SDK to Java 17 (or Java 21) - Java 21 is recommended for 1.21.9.
5. In the terminal (inside project) run: ./gradlew build  (on Windows use gradlew.bat build)
6. The built jar will be in build/libs/GrowAura-1.0.jar
7. Put the jar in your .minecraft/mods folder along with Fabric API and launch Minecraft (Fabric 1.21.9).

In-game:
- Toggle GrowAura: press G (default). You will see chat: "Instant Bonemeal: ON" or "OFF".
- Check radius: /bmr
- Set radius: /bmr <number>  (valid range: 1-20). Saves automatically to config/growaura.json
