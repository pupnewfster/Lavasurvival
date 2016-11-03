package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Commands.Cmd;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.WorldManager.PortalManager;
import com.crossge.necessities.WorldManager.WarpManager;
import com.crossge.necessities.WorldManager.WorldManager;

public interface WorldCmd extends Cmd {
    WorldManager wm = Necessities.getInstance().getWM();
    PortalManager pm = Necessities.getInstance().getPM();
    WarpManager warps = Necessities.getInstance().getWarps();
}