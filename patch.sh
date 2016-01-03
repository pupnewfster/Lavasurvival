#!/bin/bash

echo Patching Lavasurvival...
cp /root/ubot/ls1/Lavasurvival/lavasurvival/target/lavasurvival-0.3.jar /root/lavasurvival/plugins/Lavasurvival.jar

echo Patching ClassicPhysics...
cp /root/ubot/ls1/Lavasurvival/classicphysics/target/classicphysics-1.0.jar /root/lavasurvival/plugins/ClassicPhysics.jar

echo Patching Necessities...
cp /root/ubot/ls1/Lavasurvival/necessities/target/necessities-1.0.jar /root/lavasurvival/plugins/Necessities.jar

echo Done!
