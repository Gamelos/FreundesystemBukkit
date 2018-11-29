package de.gamelos.friends;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class Freundesmen {
public static void openinventory(Player p) {
	Freundesysteminfo.setSeite(p.getUniqueId().toString(), "1");
	Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GREEN+"Deine Freunde "+ChatColor.GRAY+"- Seite1");
	inv.setItem(27, Main.createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", true, "Hier gelangst du zu deinen Freunden"));
	inv.setItem(28, Main.createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", false, "Hier gelangst du zu deinem Clan"));
	inv.setItem(29, Main.createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", false, "Hier gelangst du zu deiner Party"));
	inv.setItem(30, Main.createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", false, "Hier gelangst du zu den Einstellungen"));
	
	inv.setItem(32, Main.createitem(Material.MAP, ChatColor.GRAY+"Freundschaftsanfragen: "+ChatColor.YELLOW+SQLFriends.Anfragenliste(p.getUniqueId().toString()).size(), false, null));
	
	inv.setItem(35, Main.createitem(Material.PAPER, ChatColor.GRAY+"Nächste Seite "+ChatColor.YELLOW+">>", false, null));
	
	List<String> friendlist = SQLFriends.friendlist(p.getUniqueId().toString());
	int i = 0;
	
	List<String> online = new ArrayList<>();
	List<String> offline = new ArrayList<>();
	
	for(String uuid : friendlist) {
		String name = SpielerUUID.getSpielername(uuid);
		if(i<27) {
		i++;
		if(Online.playerExists(name)) {
			online.add(uuid);
		}else {
			offline.add(uuid);
		}
		}else {
			break;
		}
	}
	
	for(String uuid:online) {
		String name = SpielerUUID.getSpielername(uuid);
		String clanshort = Claninfo.getshort(uuid);
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(name));
		if(Settings.playerExists(uuid)) {
			if(!Settings.getstatus(uuid).equals("null")) {
				lore.add(" ");
				lore.add(ChatColor.GRAY+"Status: "+ChatColor.YELLOW+Settings.getstatus(uuid));
			}
		}
		meta.setLore(lore);
		if(Claninfo.playerExists(uuid)) {
		meta.setDisplayName(MySQLRang.getchatprefix(uuid)+name+ChatColor.GRAY+" ["+ChatColor.YELLOW+clanshort+ChatColor.GRAY+"]");
		}else {
			meta.setDisplayName(MySQLRang.getchatprefix(uuid)+name);
		}
		meta.setOwner(name);
		item.setItemMeta(meta);
		inv.addItem(item);
	}
	
	
	if(offline.size() >0) {
	for(String uuid:offline) {
		String name = SpielerUUID.getSpielername(uuid);
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,(byte) 0);
		ItemMeta meta = skull.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY+name);
		List<String> lore = new ArrayList<>();
			String days = "?";
			if(Freundesysteminfo.playerExists(uuid)) {
				String time = Freundesysteminfo.getLastOnline(uuid);
				Calendar c = Calendar.getInstance();
				c.setTime(new Timestamp(System.currentTimeMillis()));
				SimpleDateFormat sdf = new SimpleDateFormat("'Datum:' dd MMMM yyyy  'Uhrzeit:'HH:mm:ss", Locale.GERMAN);
				try {
					c.setTime(sdf.parse(time));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Calendar c1 = Calendar.getInstance();
				c1.setTime(new Timestamp(System.currentTimeMillis()));
				
				long distance = c1.getTimeInMillis() - c.getTimeInMillis();
				int seconds = (int) (distance/1000);
//				p.sendMessage(""+seconds);
//				int rest = seconds%60;
//				p.sendMessage(""+(seconds-rest)/60);
				
				int minutes = 0;
				if(seconds >= 60) {
					minutes = seconds/60;
				}
				
				int hower = 0;
				if(minutes >= 60) {
				hower = minutes/60;
				}
				
				int day = 0;
				if(hower >= 24) {
					day = hower/24;
				}
				
				if(day == 0) {
					if(hower == 0) {
						if(minutes == 0) {
							days = "einem Moment";	
						}else {
						days = minutes+" Minuten";
						}
					}else {
						days = hower+" Stunden";
					}
				}else {
					days = day+" Tage";
				}
			}
			
		lore.add(ChatColor.RED+"Offline "+ChatColor.DARK_GRAY+"("+ChatColor.GRAY+"zul. Online vor "+ChatColor.YELLOW+days+ChatColor.DARK_GRAY+")");
		meta.setLore(lore);
		skull.setItemMeta(meta);
		inv.addItem(skull);
	}
	}
	
	p.openInventory(inv);

	}
}
