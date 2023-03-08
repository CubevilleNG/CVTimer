package org.cubeville.cvtimer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

public class CVTimer extends JavaPlugin implements CommandExecutor {

    private Logger logger;
    private ProtocolManager protocolManager;

    public void onEnable() {
        this.logger = getLogger();

        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer p = event.getPacket();
                String out = "";
                out = out + "Player:" + event.getPlayer().getName();
                out = out + " Name:" + p.getStrings().read(0);
                out = out + " Mode:" + p.getIntegers().read(0);
                //if(p.getIntegers().read(0) == 0) {
                    out = out + " Entities:" + p.getSpecificModifier(Collection.class).read(0);
                //}
                //out = out + " IDK:" + p.getStrings().read(2);
                System.out.println(out);
            }
        });

        logger.info(ChatColor.LIGHT_PURPLE + "Plugin Enabled Successfully");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
        return false;
    }
}
