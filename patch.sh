#!/bin/bash

echo Patching Lavasurvival...
cp /home/minecraft/ubot/Lavasurvival/lavasurvival/target/lavasurvival-0.3.jar /home/minecraft/lavasurvival/plugins/UBotPatcher/Lavasurvival.jar

echo Patching ClassicPhysics...
cp /home/minecraft/ubot/Lavasurvival/classicphysics/target/classicphysics-1.0.jar /home/minecraft/lavasurvival/plugins/UBotPatcher/ClassicPhysics.jar

echo Patching Necessities...
cp /home/minecraft/ubot/Lavasurvival/necessities/target/necessities-1.0.jar /home/minecraft/lavasurvival/plugins/UBotPatcher/Necessities.jar

echo Done!
