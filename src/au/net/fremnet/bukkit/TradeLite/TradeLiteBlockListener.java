package au.net.fremnet.bukkit.TradeLite;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

public class TradeLiteBlockListener extends BlockListener {
	static TradeLite plugin;
	
	// TODO add protection
	
	public TradeLiteBlockListener(TradeLite parent) {
		plugin = parent;
	}
	
	@Override
    public void onSignChange(SignChangeEvent event) {
		String line0 = event.getLine(0);
		if (line0.equalsIgnoreCase("[trade]")) {
			TradePost tradePost = TradePost.init(event.getBlock());
			if (tradePost != null) {
				tradePost.setup(event.getPlayer());
			}
		}
	}
}
