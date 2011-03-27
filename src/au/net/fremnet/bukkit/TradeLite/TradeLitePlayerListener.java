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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class TradeLitePlayerListener extends PlayerListener {
	static TradeLite plugin;
	
	public TradeLitePlayerListener(TradeLite parent) {
		plugin = parent;
	}
	
	@Override
    public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock()) return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			if (block.getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign)block.getState();
				if (sign.getLine(0).equalsIgnoreCase("[trade]")) {
					TradePost tradePost = TradePost.init(block);
					if (tradePost != null) {
						try {
							tradePost.readSigns();
							trade(event.getPlayer(), tradePost);
						}
						catch (InvalidTradeSignException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
    }
	
	private void trade(Player player, TradePost tradePost) {
		if (tradePost.isChestEmpty()) {
			tradeEmpty(player, tradePost);
		}
		else if (tradePost.isTradingFrom()) {
			tradeFrom(player, tradePost);
		}
		else if (tradePost.isTradingTo()) {
			tradeTo(player, tradePost);
		}
		else {
			player.sendMessage("Please empty the chest and start again");
		}
	}
	
	private void tradeEmpty(Player player, TradePost tradePost) {
		if (player.getName().startsWith(tradePost.getOwner())) {
			Material transfer;
			Integer qty = tradePost.getTradingFromStock();
			Integer remaining = 0;
			Boolean tradingTo = qty == 0;
			if (tradingTo) {
				// Give the player back the left over items
				transfer = tradePost.getTradingTo();
				qty = tradePost.getTradingToStock();
			}
			else {
				// Give the player the goodies
				transfer = tradePost.getTradingFrom();
			}
			
			if (qty == 0) {
				player.sendMessage("There is no more stock to remove");
			}
			else {
				Integer maxQty = transfer.getMaxStackSize() * 27;
				if (qty > maxQty) {
					remaining = maxQty - qty;
					qty = maxQty;
				}
	
				tradePost.getInventory().addItem(new ItemStack(transfer, qty));
				if (tradingTo) {
					tradePost.updateStock(0, -qty);
				}
				else {
					tradePost.updateStock(-qty, 0);
				}
				
				if (remaining > 0) {
					player.sendMessage(String.format("Removed %d x %s from stock, %d remains in stock", qty, transfer.name(), remaining));
				}
				else {
					player.sendMessage(String.format("Removed %d x %s from stock", qty, transfer.name()));
				}
			}
		}
		else {
			player.sendMessage(String.format("You will recieve %d x %s", tradePost.getTradingToQty(), tradePost.getTradingTo().name()));
			player.sendMessage(String.format("in exchange for %d x %s", tradePost.getTradingFromQty(), tradePost.getTradingFrom().name()));
			player.sendMessage(String.format("There is currently %d x %s in stock", tradePost.getTradingToStock(), tradePost.getTradingTo().name()));
		}
	}
	
	private void tradeFrom(Player player, TradePost tradePost) {
		if (player.getName().startsWith(tradePost.getOwner())) {
			player.sendMessage("Trading with yourself is not possible");
		}
		else {
			// TODO check that the chest can hold the trade
			Integer fromOffer = tradePost.chestStockCount(tradePost.getTradingFrom());
			Integer fromRemainder = fromOffer %	tradePost.getTradingFromQty();
			Integer toOffer = (fromOffer - fromRemainder) / tradePost.getTradingFromQty();
			if (toOffer > tradePost.getTradingToStock()) {
				player.sendMessage(String.format("There is insufficient stock of %s", tradePost.getTradingTo().name()));
			}
			else {
				Inventory inv = tradePost.getInventory();
				inv.clear();
				inv.addItem(new ItemStack(tradePost.getTradingTo(), toOffer));
				if (fromRemainder > 0) {
					inv.addItem(new ItemStack(tradePost.getTradingFrom(), fromRemainder));
					player.sendMessage(String.format("Trade successful, you have %d change", fromRemainder));
				}
				else {
					player.sendMessage("Trade successful");
				}
				tradePost.updateStock(fromOffer - fromRemainder, -toOffer);
				
				// TODO tell owner that a trade has been completed
			}
		}
	}
	
	private void tradeTo(Player player, TradePost tradePost) {
		if (player.getName().startsWith(tradePost.getOwner())) {
			Integer add = tradePost.chestStockCount(tradePost.getTradingTo());
			tradePost.getInventory().clear();
			tradePost.updateStock(0, add);
			player.sendMessage(String.format("Added %d x %s to stock, total %d", add, tradePost.getTradingTo().name(), tradePost.getTradingToStock()));
		}
		else {
			player.sendMessage(String.format("Please remove %s and add %s", tradePost.getTradingTo().name(), tradePost.getTradingFrom().name()));
		}
	}
	
	
}