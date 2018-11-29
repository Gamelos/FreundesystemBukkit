package de.gamelos.friends;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class Clanmenu {
@SuppressWarnings("unchecked")
public static void openinv(Player p) {
	Freundesysteminfo.setSeite(p.getUniqueId().toString(), "2");
	Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GRAY+"Clanmenü");
	inv.setItem(27, Main.createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", false, "Hier gelangst du zu deinen Freunden"));
	inv.setItem(28, Main.createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", true, "Hier gelangst du zu deinem Clan"));
	inv.setItem(29, Main.createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", false, "Hier gelangst du zu deiner Party"));
	inv.setItem(30, Main.createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", false, "Hier gelangst du zu den Einstellungen"));

	if(Claninfo.playerExists(p.getUniqueId().toString())) {
	String clanname = Claninfo.getClanname(p.getUniqueId().toString());
	List<String> member = MySQLClan.getClanmember(clanname);
	List<String> leader = MySQLClan.getClanleader(clanname);
	List<String> mods = MySQLClan.getClanmod(clanname);
	
	for(String name : leader) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(Online.playerExists(SpielerUUID.getSpielername(name))) {
		lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(SpielerUUID.getSpielername(name)));
		}else {
			lore.add(ChatColor.RED+"Offline");
		}
		meta.setLore(lore);
		meta.setDisplayName(MySQLRang.getchatprefix(name)+SpielerUUID.getSpielername(name)+ChatColor.GRAY+" (Clanleader)");
		meta.setOwner(SpielerUUID.getSpielername(name));
		item.setItemMeta(meta);
		inv.addItem(item);
	}
	
	for(String name : mods) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(Online.playerExists(SpielerUUID.getSpielername(name))) {
			lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(SpielerUUID.getSpielername(name)));
			}else {
				lore.add(ChatColor.RED+"Offline");
			}
		meta.setLore(lore);
		meta.setDisplayName(MySQLRang.getchatprefix(name)+SpielerUUID.getSpielername(name)+ChatColor.GRAY+" (Clanmoderator)");
		meta.setOwner(SpielerUUID.getSpielername(name));
		item.setItemMeta(meta);
		inv.addItem(item);
	}
	
	for(String name : member) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(Online.playerExists(SpielerUUID.getSpielername(name))) {
			lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(SpielerUUID.getSpielername(name)));
			}else {
				lore.add(ChatColor.RED+"Offline");
			}
		meta.setLore(lore);
		meta.setDisplayName(MySQLRang.getchatprefix(name)+SpielerUUID.getSpielername(name)+ChatColor.GRAY+" (Clanmember)");
		meta.setOwner(SpielerUUID.getSpielername(name));
		item.setItemMeta(meta);
		inv.addItem(item);
	}
	
	
	}else {
		inv.setItem(13,  Main.createitem(Material.BARRIER, ChatColor.RED+"Du bist in keinem Clan", false, null));
	}
	
	p.openInventory(inv);
}
}
