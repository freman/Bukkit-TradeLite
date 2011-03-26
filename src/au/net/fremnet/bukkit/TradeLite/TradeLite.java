/*
    TradeLite Bukkit plugin for Minecraft
    Copyright (C) 2011 Shannon Wynter (http://fremnet.net/)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package au.net.fremnet.bukkit.TradeLite;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TradeLite for Bukkit
 *
 * @author freman
 */
public class TradeLite extends JavaPlugin {
	public TradeLiteBlockListener blockListener = new TradeLiteBlockListener(this);
	public TradeLitePlayerListener playerListener = new TradeLitePlayerListener(this);

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled :)" );
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled :(" );
    }
}
