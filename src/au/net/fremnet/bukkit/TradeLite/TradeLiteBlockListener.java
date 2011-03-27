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
				if (!tradePost.setup(event)) {
					event.setCancelled(true);
				}
				else {
					event.setLine(0, "[TRADE]");
					event.setLine(2, "for");
				}
			}
		}
	}
}
