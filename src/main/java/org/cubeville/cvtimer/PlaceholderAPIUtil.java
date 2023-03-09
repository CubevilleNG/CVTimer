package org.cubeville.cvtimer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIUtil extends PlaceholderExpansion {

    private CVTimer cvTimer;

    public PlaceholderAPIUtil(CVTimer cvTimer) {
        this.cvTimer = cvTimer;
    }

    @Override
    public String getAuthor() {
        return "ToeMan_";
    }

    @Override
    public String getIdentifier() {
        return "cvtimer";
    }

    @Override
    public String getVersion() {
        return "1.19";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String pName = params.substring(params.indexOf(".") + 1);
        if(params.startsWith("finaltimemillis")) {
            return cvTimer.getFinalTime(pName, false);
        } else if(params.startsWith("currenttimemillis")) {
            return cvTimer.getCurrentTime(pName, false);
        } else if(params.startsWith("finaltime")){
            return cvTimer.getFinalTime(pName, true);
        } else if(params.startsWith("currenttime")) {
            return cvTimer.getCurrentTime(pName, true);
        }
        return null;
    }
}
