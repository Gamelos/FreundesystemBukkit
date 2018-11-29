package de.gamelos.friends;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class Partymenu {

	public static void openinv(Player p) {
		Freundesysteminfo.setSeite(p.getUniqueId().toString(), "3");
		Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GRAY+"Partymenü");
		inv.setItem(27, Main.createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", false, "Hier gelangst du zu deinen Freunden"));
		inv.setItem(28, Main.createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", false, "Hier gelangst du zu deinem Clan"));
		inv.setItem(29, Main.createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", true, "Hier gelangst du zu deiner Party"));
		inv.setItem(30, Main.createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", false, "Hier gelangst du zu den Einstellungen"));
		inv.setItem(35, Main.createitem(Material.DIAMOND, ChatColor.GREEN+"Neue Party erstellen", false, null));
		
		
		if(PartyID.playerExists(p.getUniqueId().toString())) {
		String id = PartyID.getID(p.getUniqueId().toString());
		List<String> partylist = SQLParty.StringToList(SQLParty.getPartylist(id));
		List<String> modlist =  SQLParty.StringToList(SQLParty.getModlist(id));
		String leader = SQLParty.getLeader(id);
		
		for(String name:partylist) {
			
			String rang = "Spieler";
			if(modlist.contains(name)) {
				rang = "Moderator";
			}
			if(leader.equals(name)) {
				rang = "Leader";
			}
			
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>(); 
		lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(name));
		meta.setLore(lore);
		meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name+ChatColor.GRAY+" ("+rang+")");
		meta.setOwner(name);
		item.setItemMeta(meta);
		inv.addItem(item);
		}
		}else {
			List<String> publicpartys = SQLParty.getpublicpartys();
			if(publicpartys.size()>0) {
				for(String id:publicpartys) {
					String leader = SQLParty.getLeader(id);
					int size = SQLParty.StringToList(SQLParty.getPartylist(id)).size();
					String type = SQLParty.getType(id);
					
					
				ItemStack item = new ItemStack(getitem(type));
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<>(); 
				lore.add(ChatColor.GRAY+"ID: "+ChatColor.YELLOW+id);
				lore.add(ChatColor.GRAY+"Size: "+ChatColor.RED+size);
				String addtype = type.substring(1, type.length());
				String a = type.substring(0, 1).toUpperCase();
				lore.add(ChatColor.GRAY+"Type: "+a+addtype);
				meta.setLore(lore);
				meta.setDisplayName(ChatColor.GRAY+"Party von "+MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(leader.toUpperCase()))+leader);
				item.setItemMeta(meta);
				inv.addItem(item);
				
				
				}
			}else {
			inv.setItem(13, Main.createitem(Material.BARRIER, ChatColor.RED+"Es gibt keine Öffentlichen Partys", false, null));
			}
		}
		p.openInventory(inv);
	}
	
	public static Material getitem(String type) {
		Material m = Material.BARRIER;
		if(type.equals("bedwars")) {
			m = Material.BED;
		}else if(type.equals("skywars")) {
			m = Material.GRASS;
		}if(type.equals("trump")) {
			m = Material.GOLD_BLOCK;
		}if(type.equals("mixed")) {
			m = Material.EMERALD;
		}if(type.equals("knockout")) {
			m = Material.STICK;
		}
		return m;
	}
	
}
