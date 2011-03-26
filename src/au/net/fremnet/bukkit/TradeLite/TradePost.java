package au.net.fremnet.bukkit.TradeLite;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradePost {

	private Block tradeSign;
	private Block chest;
	private Block inventorySign;
	
	private Material tradingFrom;
	private Integer tradingFromQty;
	private Integer tradingFromStock;

	private Material tradingTo;
	private Integer tradingToQty;
	private Integer tradingToStock;

	// TODO come back and make the material:x work
	// static private Pattern tradeExpression = Pattern.compile("^(\\d+) ?x ?(\\d+(?::\\d*)?)\t(\\d+) ?x ?(\\d+(?::\\d*)?)$");
	static private Pattern tradeExpression = Pattern.compile("^(\\d+) ?x ?(\\d+)\t(\\d+) ?x ?(\\d+)$");
	
	private String owner;
	
	private TradePost(Block tradeSign, Block chest, Block inventorySign) {
		this.setTradeSign(tradeSign);
		this.setChest(chest);
		this.setInventorySign(inventorySign);
	}
	
	static public TradePost init(Block tradeSign) {
		org.bukkit.material.Sign signData = new org.bukkit.material.Sign(tradeSign.getType(), tradeSign.getData());
		Block chest = tradeSign.getFace(BlockFace.DOWN);
		Block inventorySign = chest.getFace(signData.getAttachedFace());
		
		if (chest.getType().equals(Material.CHEST) && inventorySign.getType().equals(Material.WALL_SIGN)) {
			return new TradePost(tradeSign, chest, inventorySign);
		}

		return null;
	}

	public void readSigns() throws InvalidTradeSignException {
		this.readTradeSign();
		this.readInventorySign();
	}
	
	public void readTradeSign() throws InvalidTradeSignException {
		Sign signState = (Sign) this.getTradeSign().getState();
		String tradingLines = signState.getLine(1) + "\t" + signState.getLine(3);
		Matcher m = tradeExpression.matcher(tradingLines);
		if (!m.matches())
			throw new InvalidTradeSignException();

		this.setTradingFrom(Material.getMaterial(Integer.parseInt(m.group(4))));
		this.setTradingFromQty(Integer.parseInt(m.group(3)));
		
		this.setTradingTo(Material.getMaterial(Integer.parseInt(m.group(2))));
		this.setTradingToQty(Integer.parseInt(m.group(1)));
	}

	public void readInventorySign() {
		Sign signState = (Sign) this.getTradeSign().getState();
		this.setTradingFromStock(Integer.parseInt(signState.getLine(0)));
		this.setTradingToStock(Integer.parseInt(signState.getLine(1)));
		this.setOwner(signState.getLine(3));
	}

	public void updateStock(Integer fromDelta, Integer toDelta) {
		Sign signState = (Sign) this.getTradeSign().getState();

		this.tradingFromStock += fromDelta;
		this.tradingToStock += toDelta;
		
		signState.setLine(0, String.format("%d", this.tradingFromStock));
		signState.setLine(1, String.format("%d", this.tradingToStock));
		
		signState.update();
	}
	
	public Boolean setup(Player player) {
		try {
			this.readTradeSign();
		}
		catch (InvalidTradeSignException e) {
			player.sendMessage("Unable to parse trade sign");
			return false;
		}
		
		String playerName = player.getName();
		if (playerName.length() > 15) {
			playerName = playerName.substring(0, 15);
		}
		
		Sign signState = (Sign) this.getInventorySign().getState();
		signState.setLine(0, "0");
		signState.setLine(1, "0");
		signState.setLine(3, playerName);
		signState.update();
		return true;
	}
	
	public Inventory getInventory() {
		Chest chest = (Chest)this.getChest().getState();
		return chest.getInventory();
	}
	
	public Boolean isChestEmpty() {
		return this.getInventory().getContents().length == 0;
	}
	
	public Integer chestStockCount(Material material) {
		Integer count = 0;
		HashMap<Integer, ? extends ItemStack> stacks = this.getInventory().all(material);
		for (ItemStack stack : stacks.values()) {
			count += stack.getAmount();
		}
		return count;
	}
	
	public Boolean isTradingFrom() {
		Inventory inv = this.getInventory();
		Material from = this.getTradingFrom();
		
		Boolean sane = inv.contains(from, this.getTradingFromQty());
		sane &= inv.all(from).size() == inv.getContents().length;
		
		return sane;
	}

	public Boolean isTradingTo() {
		Inventory inv = this.getInventory();
		Material to = this.getTradingTo();
		
		Boolean sane = inv.contains(to, this.getTradingFromQty());
		sane &= inv.all(to).size() == inv.getContents().length;
		
		return sane;
	}
	
	private void setInventorySign(Block inventorySign) {
		this.inventorySign = inventorySign;
	}

	public Block getInventorySign() {
		return inventorySign;
	}

	private void setChest(Block chest) {
		this.chest = chest;
	}

	public Block getChest() {
		return chest;
	}

	private void setTradeSign(Block tradeSign) {
		this.tradeSign = tradeSign;
	}

	public Block getTradeSign() {
		return tradeSign;
	}

	private void setTradingTo(Material tradingTo) {
		this.tradingTo = tradingTo;
	}

	public Material getTradingTo() {
		return tradingTo;
	}

	private void setTradingFromQty(Integer tradingFromQty) {
		this.tradingFromQty = tradingFromQty;
	}

	public Integer getTradingFromQty() {
		return tradingFromQty;
	}

	private void setTradingFrom(Material tradingFrom) {
		this.tradingFrom = tradingFrom;
	}

	public Material getTradingFrom() {
		return tradingFrom;
	}

	private void setTradingToQty(Integer tradingToQty) {
		this.tradingToQty = tradingToQty;
	}

	public Integer getTradingToQty() {
		return tradingToQty;
	}

	private void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	private void setTradingFromStock(Integer tradingFromStock) {
		this.tradingFromStock = tradingFromStock;
	}

	public Integer getTradingFromStock() {
		return tradingFromStock;
	}

	private void setTradingToStock(Integer tradingToStock) {
		this.tradingToStock = tradingToStock;
	}

	public Integer getTradingToStock() {
		return tradingToStock;
	}
	
}
