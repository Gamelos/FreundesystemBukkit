package de.gamelos.friends;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class Friendrequests {
public static void openinv(Player p) {
	Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GRAY+"Freundschaftsanfragen");
	List<String> freundschaftsanfragen = SQLFriends.Anfragenliste(p.getUniqueId().toString());
	
	if(freundschaftsanfragen.size()>0) {
		for(String uuid: freundschaftsanfragen) {
			String name = SpielerUUID.getSpielername(uuid);
			String clanshort = Claninfo.getshort(uuid);
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if(Claninfo.playerExists(uuid)) {
				meta.setDisplayName(MySQLRang.getchatprefix(uuid)+name+ChatColor.GRAY+" ["+ChatColor.YELLOW+clanshort+ChatColor.GRAY+"]");
				}else {
					meta.setDisplayName(MySQLRang.getchatprefix(uuid)+name);
				}
			meta.setOwner(name);
			item.setItemMeta(meta);
			inv.addItem(item);
		}
	}else {
		inv.setItem(13, Main.createitem(Material.BARRIER, ChatColor.RED+"Du hast keine Freundschaftsanfragen", false, null));
	}
	
	p.openInventory(inv);
}
}
