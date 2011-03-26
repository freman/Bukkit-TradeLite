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
