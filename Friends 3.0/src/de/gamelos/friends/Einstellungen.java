package de.gamelos.friends;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;

public class Einstellungen {
public static void openinv(Player p) {
	Freundesysteminfo.setSeite(p.getUniqueId().toString(), "4");
	Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GRAY+"Einstellungen");
	inv.setItem(27, Main.createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", false, "Hier gelangst du zu deinen Freunden"));
	inv.setItem(28, Main.createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", false, "Hier gelangst du zu deinem Clan"));
	inv.setItem(29, Main.createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", false, "Hier gelangst du zu deiner Party"));
	inv.setItem(30, Main.createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", true, "Hier gelangst du zu den Einstellungen"));
	inv.setItem(35, Main.createitem(Material.PAPER, ChatColor.YELLOW+"Status", false, "Ändere deinen Status"));
	if(Settings.playerExists(p.getUniqueId().toString())) {
		
		if(Settings.getjump(p.getUniqueId().toString())) {
			inv.setItem(10, Main.createitem(Material.ENDER_PEARL, ChatColor.GREEN+"Nachspringen", true, ChatColor.GREEN+"Aktiviert"));	
		}else {
			inv.setItem(10, Main.createitem(Material.ENDER_PEARL, ChatColor.RED+"Nachspringen", false, ChatColor.RED+"Deaktiviert"));	
		}
		
		if(Settings.getmsg(p.getUniqueId().toString())) {
			inv.setItem(12, Main.createitem(Material.BOOK, ChatColor.GREEN+"Private Nachrichten", true, ChatColor.GREEN+"Aktiviert"));
		}else {
			inv.setItem(12, Main.createitem(Material.BOOK, ChatColor.RED+"Private Nachrichten", false, ChatColor.RED+"Deaktiviert"));
		}
		
		if(Settings.getfriend(p.getUniqueId().toString())) {
			inv.setItem(14, Main.createitem(Material.MAP, ChatColor.GREEN+"Freundschaftsanfragen", true, ChatColor.GREEN+"Aktiviert"));
		}else {
			inv.setItem(14, Main.createitem(Material.MAP, ChatColor.RED+"Freundschaftsanfragen", false, ChatColor.RED+"Deaktiviert"));
		}
		
		if(Settings.getparty(p.getUniqueId().toString())) {
			inv.setItem(16, Main.createitem(Material.CAKE, ChatColor.GREEN+"Partyeinladungen", true, ChatColor.GREEN+"Aktiviert"));
		}else {
			inv.setItem(16, Main.createitem(Material.CAKE, ChatColor.RED+"Partyeinladungen", false, ChatColor.RED+"Deaktiviert"));
		}
	}else {
	inv.setItem(10, Main.createitem(Material.ENDER_PEARL, ChatColor.GREEN+"Nachspringen", true, ChatColor.GREEN+"Aktiviert"));
	inv.setItem(12, Main.createitem(Material.BOOK, ChatColor.GREEN+"Private Nachrichten", true, ChatColor.GREEN+"Aktiviert"));
	inv.setItem(14, Main.createitem(Material.MAP, ChatColor.GREEN+"Freundschaftsanfragen", true, ChatColor.GREEN+"Aktiviert"));
	inv.setItem(16, Main.createitem(Material.CAKE, ChatColor.GREEN+"Partyeinladungen", true, ChatColor.GREEN+"Aktiviert"));
	}
	
	p.openInventory(inv);
}
}
