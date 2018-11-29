package de.gamelos.friends;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.FutureIds;
import com.avaje.ebeaninternal.server.autofetch.StatisticsNodeUsage;
import com.avaje.ebeaninternal.server.lib.sql.DataSourcePool.Status;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.gamelos.jaylosapi.JaylosAPI;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.BlockPosition;

public class Main extends JavaPlugin implements Listener{

	public static MySQL mysql;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "info");
		ConnectMySQL();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		super.onEnable();
	}
	
	
	private void ConnectMySQL(){
		mysql = new MySQL(JaylosAPI.gethost(), JaylosAPI.getuser(), JaylosAPI.getdatabase(), JaylosAPI.getpassword());
		mysql.update("CREATE TABLE IF NOT EXISTS Raenge(UUID varchar(64), RANGNAME varchar(1000), PREFIX varchar(1000), TIME varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Online(UUID varchar(64), Servername varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS SpielerUUID(UUID varchar(64), Spielername varchar(1000), id varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Frienddata(UUID varchar(64), Freundesliste varchar(1000), Anfragen varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Settingsdata(UUID varchar(64), msg varchar(1000), party varchar(1000), requests varchar(1000), jump varchar(1000), status varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Settings(UUID varchar(64), jump varchar(1000), msg varchar(1000), party varchar(1000), friend varchar(1000), status varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Party(ID varchar(64), PARTYLIST varchar(1000), LEADER varchar(1000), MODLIST varchar(1000), PRIVATE varchar(1000), TYPE varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Partyid(UUID varchar(64), ID varchar(1000));");
		mysql.update("CREATE TABLE IF NOT EXISTS Freundesysteminfo(UUID varchar(64), Seite varchar(1000), LastOnline varchar(1000));");
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
	}

	@EventHandler
	public void onklick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK||e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(p.getItemInHand().getType() == Material.SKULL_ITEM) {
				if(Freundesysteminfo.playerExists(p.getUniqueId().toString())) {
					int seite = Integer.parseInt(Freundesysteminfo.getSeite(p.getUniqueId().toString()));
					
					if(seite == 1) {
						Freundesmen.openinventory(p);
					}else if(seite == 2) {
						Clanmenu.openinv(p);
					}else if(seite == 3) {
						Partymenu.openinv(p);
					}else {
						Einstellungen.openinv(p);
					}
					
					
				}else {
				Freundesmen.openinventory(p);
				}
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onklick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getClickedInventory() != null && e.getClickedInventory().getTitle() != null) {
		if(ChatColor.stripColor(e.getClickedInventory().getTitle()).contains("Deine Freunde")) {
			if(e.getCurrentItem().getType() == Material.PAPER) {
				if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).contains("Nächste Seite")){
			int seite = Integer.parseInt(ChatColor.stripColor(e.getClickedInventory().getTitle()).split("-")[1].substring(6, ChatColor.stripColor(e.getClickedInventory().getTitle()).split("-")[1].length()));
			seite++;
			if(seite<=9) {
			Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GREEN+"Deine Freunde "+ChatColor.GRAY+"- Seite"+seite);
			inv.setItem(27, createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", true, "Hier gelangst du zu deinen Freunden"));
			inv.setItem(28, createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", false, "Hier gelangst du zu deinem Clan"));
			inv.setItem(29, createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", false, "Hier gelangst du zu deiner Party"));
			inv.setItem(30, createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", false, "Hier gelangst du zu den Einstellungen"));
			
			inv.setItem(32, createitem(Material.MAP, ChatColor.GRAY+"Freundschaftsanfragen: "+ChatColor.YELLOW+SQLFriends.Anfragenliste(p.getUniqueId().toString()).size(), false, null));
			
			inv.setItem(34, createitem(Material.PAPER, ChatColor.YELLOW+"<<"+ChatColor.GRAY+"Vorherige Seite ", false, null));
			inv.setItem(35, createitem(Material.PAPER, ChatColor.GRAY+"Nächste Seite "+ChatColor.YELLOW+">>", false, null));
			
			List<String> friendlist = SQLFriends.friendlist(p.getUniqueId().toString());
			
			int i = 0;
			
			List<String> online = new ArrayList<>();
			List<String> offline = new ArrayList<>();
			
			for(String uuid : friendlist) {
				String name = SpielerUUID.getSpielername(uuid);
				i++;
				if((i>(27*(seite-1)))&&(i<=(27*seite))) {
				if(Online.playerExists(name)) {
					online.add(uuid);
				}else {
					offline.add(uuid);
				}
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
			
			for(String uuid:offline) {
				String name = SpielerUUID.getSpielername(uuid);
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,(byte) 0);
				ItemMeta meta = skull.getItemMeta();
				meta.setDisplayName(ChatColor.GRAY+name);
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.RED+"Offline");
				meta.setLore(lore);
				skull.setItemMeta(meta);
				inv.addItem(skull);
			}
			p.openInventory(inv);
				}else {
					p.sendMessage(Prefix+ChatColor.RED+"Es gibt keine weiteren Seiten!");
				}
			}else {
				int seite = Integer.parseInt(ChatColor.stripColor(e.getClickedInventory().getTitle()).split("-")[1].substring(6, ChatColor.stripColor(e.getClickedInventory().getTitle()).split("-")[1].length()));
				seite--;
				if(seite<=9) {
				Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GREEN+"Deine Freunde "+ChatColor.GRAY+"- Seite"+seite);
				inv.setItem(27, createitem(Material.GOLD_HELMET, ChatColor.YELLOW+"Freundemenü", true, "Hier gelangst du zu deinen Freunden"));
				inv.setItem(28, createitem(Material.IRON_CHESTPLATE, ChatColor.AQUA+"Clanmenü", false, "Hier gelangst du zu deinem Clan"));
				inv.setItem(29, createitem(Material.FIREWORK, ChatColor.LIGHT_PURPLE+"Partymenü", false, "Hier gelangst du zu deiner Party"));
				inv.setItem(30, createitem(Material.REDSTONE_COMPARATOR, ChatColor.RED+"Einstellungen", false, "Hier gelangst du zu den Einstellungen"));
				
				inv.setItem(32, createitem(Material.MAP, ChatColor.GRAY+"Freundschaftsanfragen: "+ChatColor.YELLOW+SQLFriends.Anfragenliste(p.getUniqueId().toString()).size(), false, null));
				
				if(seite>1) {
				inv.setItem(34, createitem(Material.PAPER, ChatColor.YELLOW+"<<"+ChatColor.GRAY+"Vorherige Seite ", false, null));
				}
				inv.setItem(35, createitem(Material.PAPER, ChatColor.GRAY+"Nächste Seite "+ChatColor.YELLOW+">>", false, null));
				
				List<String> friendlist = SQLFriends.friendlist(p.getUniqueId().toString());
				
				int i = 0;
				
				List<String> online = new ArrayList<>();
				List<String> offline = new ArrayList<>();
				
				
				for(String uuid : friendlist) {
					String name = SpielerUUID.getSpielername(uuid);
					i++;
					if((i>(27*(seite-1)))&&(i<=(27*seite))) {
					if(Online.playerExists(name)) {
						online.add(uuid);
					}else {
						offline.add(uuid);
					}
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
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						Calendar c1 = Calendar.getInstance();
						c1.setTime(new Timestamp(System.currentTimeMillis()));
						
						long distance = c1.getTimeInMillis() - c.getTimeInMillis();
						int seconds = (int) (distance/1000);
//						p.sendMessage(""+seconds);
//						int rest = seconds%60;
//						p.sendMessage(""+(seconds-rest)/60);
						
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
				p.openInventory(inv);
					}else {
						p.sendMessage(Prefix+ChatColor.RED+"Es gibt keine weiteren Seiten!");
					}	
			}
		
		}else if(e.getCurrentItem().getType() == Material.FIREWORK) {
			Partymenu.openinv(p);
		}else if(e.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
			Clanmenu.openinv(p);
		}else if(e.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
			Einstellungen.openinv(p);
		}else if(e.getCurrentItem().getType() == Material.MAP) {
			Friendrequests.openinv(p);
		}else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0)).contains("Offline")) {
				String name = "";
				if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).contains("[")) {
				String[] a = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split("[");
				name = a[0].substring(0, a[0].length()-1);
				}else {
					name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				}
				Inventory inv = Bukkit.createInventory(null, 9*3, "Einstellungen für "+name);
				
				for(int i = 0; i<9;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				for(int i = 18; i<27;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				String clanshort = Claninfo.getshort(SpielerUUID.getUUIDaboutid(name.toUpperCase()));
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.RED+"Offline");
				meta.setLore(lore);
				if(Claninfo.playerExists(SpielerUUID.getUUIDaboutid(name.toUpperCase()))) {
					meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name+ChatColor.GRAY+" ["+ChatColor.YELLOW+clanshort+ChatColor.GRAY+"]");
					}else {
						meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name);
					}
				meta.setOwner(name);
				item.setItemMeta(meta);
				inv.setItem(10, item);
				inv.setItem(13, createitem(Material.IRON_CHESTPLATE, ChatColor.DARK_AQUA+"Freund in Clan einladen", false, null));
				inv.setItem(16, createitem(Material.BARRIER, ChatColor.RED+"Freund entfernen", false, null));
				
				p.openInventory(inv);
			}else {
				String name = "";
				if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).contains("[")) {
				String[] a = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split(" ");
				name = a[0];
				}else {
					name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				}
				Inventory inv = Bukkit.createInventory(null, 9*3, "Einstellungen für "+name);
				for(int i = 0; i<9;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				for(int i = 18; i<27;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				
				String clanshort = Claninfo.getshort(SpielerUUID.getUUIDaboutid(name.toUpperCase()));
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GREEN+"Online"+ChatColor.GRAY+" auf "+ChatColor.YELLOW+Online.getServer(name));
				meta.setLore(lore);
				if(Claninfo.playerExists(SpielerUUID.getUUIDaboutid(name.toUpperCase()))) {
					meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name+ChatColor.GRAY+" ["+ChatColor.YELLOW+clanshort+ChatColor.GRAY+"]");
					}else {
						meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name);
					}
				meta.setOwner(name);
				item.setItemMeta(meta);
				inv.setItem(10, item);
				inv.setItem(12, createitem(Material.CAKE, ChatColor.LIGHT_PURPLE+"Freund in Party einladen", false, null));
				inv.setItem(13, createitem(Material.IRON_CHESTPLATE, ChatColor.DARK_AQUA+"Freund in Clan einladen", false, null));
				inv.setItem(14, createitem(Material.ENDER_PEARL, ChatColor.GREEN+"Freund nachspringen", false, null));
				inv.setItem(16, createitem(Material.BARRIER, ChatColor.RED+"Freund entfernen", false, null));
				
				
				p.openInventory(inv);
			}
		}
		}else if(e.getClickedInventory().getTitle().equals(ChatColor.GRAY+"Einstellungen")) {
			if(e.getCurrentItem().getType() == Material.ENDER_PEARL) {
				if(Settings.playerExists(p.getUniqueId().toString())) {
					if(Settings.getjump(p.getUniqueId().toString())) {
						Settings.setjump(p.getUniqueId().toString(), "false");
						e.getClickedInventory().setItem(10, createitem(Material.ENDER_PEARL, ChatColor.RED+"Nachspringen", false, ChatColor.RED+"Deaktiviert"));
					}else {
						Settings.setjump(p.getUniqueId().toString(), "true");	
						e.getClickedInventory().setItem(10, createitem(Material.ENDER_PEARL, ChatColor.GREEN+"Nachspringen", true, ChatColor.GREEN+"Aktiviert"));
					}
				}else {
					Settings.setjump(p.getUniqueId().toString(), "false");
					e.getClickedInventory().setItem(10, createitem(Material.ENDER_PEARL, ChatColor.RED+"Nachspringen", false, ChatColor.RED+"Deaktiviert"));
				}
			}else if(e.getCurrentItem().getType() == Material.BOOK) {
				if(Settings.playerExists(p.getUniqueId().toString())) {
					if(Settings.getmsg(p.getUniqueId().toString())) {
						Settings.setmsg(p.getUniqueId().toString(), "false");
						e.getClickedInventory().setItem(12, createitem(Material.BOOK, ChatColor.RED+"Private Nachrichten", false, ChatColor.RED+"Deaktiviert"));
					}else {
						Settings.setmsg(p.getUniqueId().toString(), "true");
						e.getClickedInventory().setItem(12, createitem(Material.BOOK, ChatColor.GREEN+"Private Nachrichten", true, ChatColor.GREEN+"Aktiviert"));
					}
				}else {
					Settings.setmsg(p.getUniqueId().toString(), "false");
					e.getClickedInventory().setItem(12, createitem(Material.BOOK, ChatColor.RED+"Private Nachrichten", false, ChatColor.RED+"Deaktiviert"));
				}
			}else if(e.getCurrentItem().getType() == Material.MAP) {
				if(Settings.playerExists(p.getUniqueId().toString())) {
					if(Settings.getfriend(p.getUniqueId().toString())) {
						Settings.setfriend(p.getUniqueId().toString(), "false");
						e.getClickedInventory().setItem(14, createitem(Material.MAP, ChatColor.RED+"Freundschaftsanfragen", false, ChatColor.RED+"Deaktiviert"));
					}else {
						Settings.setfriend(p.getUniqueId().toString(), "true");
						e.getClickedInventory().setItem(14, createitem(Material.MAP, ChatColor.GREEN+"Freundschaftsanfragen", true, ChatColor.GREEN+"Aktiviert"));
					}
				}else {
					Settings.setfriend(p.getUniqueId().toString(), "false");
					e.getClickedInventory().setItem(14, createitem(Material.MAP, ChatColor.RED+"Freundschaftsanfragen", false, ChatColor.RED+"Deaktiviert"));
				}
				
			}else if(e.getCurrentItem().getType() == Material.CAKE) {
				if(Settings.playerExists(p.getUniqueId().toString())) {
					if(Settings.getparty(p.getUniqueId().toString())) {
						Settings.setparty(p.getUniqueId().toString(), "false");
						e.getClickedInventory().setItem(16, createitem(Material.CAKE, ChatColor.RED+"Partyeinladungen", false, ChatColor.RED+"Deaktiviert"));
					}else {
						Settings.setparty(p.getUniqueId().toString(), "true");
						e.getClickedInventory().setItem(16, createitem(Material.CAKE, ChatColor.GREEN+"Partyeinladungen", true, ChatColor.GREEN+"Aktiviert"));
					}
				}else {
					Settings.setparty(p.getUniqueId().toString(), "false");
					e.getClickedInventory().setItem(16, createitem(Material.CAKE, ChatColor.RED+"Partyeinladungen", false, ChatColor.RED+"Deaktiviert"));
				}
			}else if(e.getCurrentItem().getType() ==Material.GOLD_HELMET) {
				Freundesmen.openinventory(p);
			}else if(e.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
				Clanmenu.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.FIREWORK) {
				Partymenu.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.PAPER) {
				p.closeInventory();
				if(hasPremiumfeatures(p)) {
				p.sendMessage(Prefix+ChatColor.GRAY+"Gebe nun deinen Status in den Chat ein!");
				Statusset.add(p);
				}else {
					p.sendMessage(Main.Prefix+ChatColor.RED+"Du brauchst "+ChatColor.GOLD+"Premium"+ChatColor.RED+" um dieses Feature nutzen zu können");
				}
			}
			
			
		}else if(ChatColor.stripColor(e.getInventory().getTitle()).equals("Clanmenü")) {
			if(e.getCurrentItem().getType() == Material.GOLD_HELMET) {
				Freundesmen.openinventory(p);
			}else if(e.getCurrentItem().getType() == Material.FIREWORK) {
				Partymenu.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
				Einstellungen.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
				if(Claninfo.playerExists(p.getUniqueId().toString())) {
					String clanname = Claninfo.getClanname(p.getUniqueId().toString());
					if(MySQLClan.getClanleader(clanname).contains(p.getUniqueId().toString())){
						String s = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
						String[] a = s.split(" ");
						String spieler = a[0];
						if(!MySQLClan.getClanleader(clanname).contains(SpielerUUID.getUUIDaboutid(spieler.toUpperCase()))) {
						Inventory inv = Bukkit.createInventory(null, 9*3, "Clansettings für "+spieler);
						for(int i = 0; i<9;i++) {
							inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
						}
						for(int i = 18; i<27;i++) {
							inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
						}
						
						ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
						SkullMeta meta = (SkullMeta) item.getItemMeta();
						meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(spieler.toUpperCase()))+spieler);
						meta.setOwner(spieler);
						item.setItemMeta(meta);
						inv.setItem(10, item);
						ItemStack rang = new ItemStack(Material.BOOK);
						ItemMeta rangmeta = rang.getItemMeta();
						rangmeta.setDisplayName(ChatColor.YELLOW+"Rang ändern");
						List<String> ranglore = new ArrayList<>();
						ranglore.add(ChatColor.GRAY+"Linksklick, um Spieler zu befördern");
						ranglore.add(ChatColor.GRAY+"Rechtsklick, um Spieler zu degradieren");
						rangmeta.setLore(ranglore);
						rang.setItemMeta(rangmeta);
						inv.setItem(12, rang);
						inv.setItem(16, createitem(Material.BARRIER, ChatColor.RED+"Aus Clan kicken", false, null));
						
						p.openInventory(inv);
						}
					}
				}
			}
		}else if(e.getClickedInventory().getTitle().equals(ChatColor.GRAY+"Partymenü")) {
			if(e.getCurrentItem().getType() == Material.GOLD_HELMET) {
				Freundesmen.openinventory(p);
			}else if(e.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
				Clanmenu.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
				Einstellungen.openinv(p);
			}else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
				if(PartyID.playerExists(p.getUniqueId().toString())) {
					if(SQLParty.getLeader(PartyID.getID(p.getUniqueId().toString())).equals(p.getName())) {
					String[] a = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split(" ");
					String name = a[0];
					Inventory inv = Bukkit.createInventory(null, 9*3, "Partysettings für "+name);
					for(int i = 0; i<9;i++) {
						inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
					}
					for(int i = 18; i<27;i++) {
						inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
					}
					ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					meta.setDisplayName(MySQLRang.getchatprefix(SpielerUUID.getUUIDaboutid(name.toUpperCase()))+name);
					meta.setOwner(name);
					item.setItemMeta(meta);
					inv.setItem(10, item);
					ItemStack rang = new ItemStack(Material.BOOK);
					ItemMeta rangmeta = rang.getItemMeta();
					rangmeta.setDisplayName(ChatColor.YELLOW+"Rang ändern");
					List<String> ranglore = new ArrayList<>();
					ranglore.add(ChatColor.GRAY+"Linksklick, um Spieler zu befördern");
					ranglore.add(ChatColor.GRAY+"Rechtsklick, um Spieler zu degradieren");
					rangmeta.setLore(ranglore);
					rang.setItemMeta(rangmeta);
					inv.setItem(12, rang);
					inv.setItem(16, createitem(Material.BARRIER, ChatColor.RED+"Aus Party kicken", false, null));
					
					p.openInventory(inv);
					}
				}
			}else if(e.getCurrentItem().getType() == Material.DIAMOND) {
				if(hasPremiumfeatures(p)) {
				Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GRAY+"Wähle einen Party-Typ aus");
				inv.addItem(createitem(Material.GRASS, ChatColor.GREEN+"SkyWars", false, null));
				inv.addItem(createitem(Material.GOLD_BLOCK, ChatColor.YELLOW+"Trump", false, null));
				inv.addItem(createitem(Material.STICK, ChatColor.RED+"Knockout", true, null));
				inv.addItem(createitem(Material.BED, ChatColor.RED+"Bedwars", false, null));
				inv.addItem(createitem(Material.EMERALD, ChatColor.AQUA+"Mixed", false, null));
				p.openInventory(inv);
				}else {
					p.sendMessage(Main.Prefix+ChatColor.RED+"Du brauchst "+ChatColor.GOLD+"Premium"+ChatColor.RED+" um dieses Feature nutzen zu können");
				}
			}else if(org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).contains("Party von")) {
				String[] a = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split(" ");
				String name = a[2];
				sendmsgtobungee("partyjoin/"+name, p);
				p.closeInventory();
			}
		}else if(e.getClickedInventory().getTitle().equals(ChatColor.GRAY+"Freundschaftsanfragen")) {
			if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
				String[] a = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split(" ");
				String name = a[0];
				Inventory inv = Bukkit.createInventory(null, 9*3, ChatColor.GRAY+"Anfrage von "+name);
				for(int i = 0; i<9;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				for(int i = 18; i<27;i++) {
					inv.setItem(i, createitem(Material.STAINED_GLASS_PANE, " ", false, null));
				}
				
				ItemStack b = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.GREEN.getData());
				ItemMeta meta = b.getItemMeta();
				meta.setDisplayName(ChatColor.GREEN+"Annehmen");
				b.setItemMeta(meta);
				ItemStack d = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData());
				ItemMeta metad = d.getItemMeta();
				metad.setDisplayName(ChatColor.RED+"Ablehnen");
				d.setItemMeta(metad);
				inv.setItem(11, b);
				inv.setItem(15, d);
				p.openInventory(inv);
			}
		}else if(e.getClickedInventory().getTitle().contains("Einstellungen für")) {
			String[] a = e.getClickedInventory().getTitle().split(" ");
			String name = a[2];
			if(e.getCurrentItem().getType() == Material.CAKE) {
				sendmsgtobungee("invite/"+name, p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.ENDER_PEARL) {
				sendmsgtobungee("jump/"+name, p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
				sendmsgtobungee("claninvite/"+name, p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.BARRIER) {
				sendmsgtobungee("remove/"+name, p);
				p.closeInventory();
			}
		}else if(e.getClickedInventory().getTitle().contains("Clansettings für")) {
			String[] a = e.getClickedInventory().getTitle().split(" ");
			String name = a[2];
			if(e.getCurrentItem().getType() == Material.BOOK) {
				if(e.isRightClick()) {
					sendmsgtobungee("clanreduce/"+name, p);
					p.closeInventory();
				}else {
					sendmsgtobungee("clanpromote/"+name, p);
					p.closeInventory();
				}
			}else if(e.getCurrentItem().getType() == Material.BARRIER) {
				sendmsgtobungee("clanremove/"+name, p);
				p.closeInventory();
			}
		}else if(e.getClickedInventory().getTitle().contains("Partysettings für")) {
			String[] a = e.getClickedInventory().getTitle().split(" ");
			String name = a[2];
			if(e.getCurrentItem().getType() == Material.BOOK) {
				if(e.isRightClick()) {
					sendmsgtobungee("partyreduce/"+name, p);
					p.closeInventory();
				}else {
					sendmsgtobungee("partypromote/"+name, p);
					p.closeInventory();
				}
			}else if(e.getCurrentItem().getType() == Material.BARRIER) {
				sendmsgtobungee("partyremove/"+name, p);
			}
		}else if(e.getClickedInventory().getTitle().contains(ChatColor.GRAY+"Anfrage von")) {
			String[] a = e.getClickedInventory().getTitle().split(" ");
			String name = a[2];
			
			if(e.getCurrentItem().getData().getData() == DyeColor.RED.getData()) {
				sendmsgtobungee("friendremove/"+name, p);
				p.closeInventory();
			}else if(e.getCurrentItem().getData().getData() == DyeColor.GREEN.getData()) {
				sendmsgtobungee("friendaccept/"+name, p);
				p.closeInventory();
			}
			
		}else if(e.getClickedInventory().getTitle().contains(ChatColor.GRAY+"Wähle einen Party-Typ aus")) {
			if(e.getCurrentItem().getType() == Material.GRASS) {
				sendmsgtobungee("partycreate/skywars", p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.GOLD_BLOCK) {
				sendmsgtobungee("partycreate/trump", p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.STICK) {
				sendmsgtobungee("partycreate/knockout", p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.BED) {
				sendmsgtobungee("partycreate/bedwars", p);
				p.closeInventory();
			}else if(e.getCurrentItem().getType() == Material.EMERALD) {
				sendmsgtobungee("partycreate/mixed", p);
				p.closeInventory();
			}
		}
		}
	}
	
    public void sendmsgtobungee(String msg, Player p) {
    	ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("data");
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.sendPluginMessage(Bukkit.getPluginManager().getPlugin("Friends"), "BungeeCord", b.toByteArray());
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onchat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		if(Statusset.contains(p)) {
			e.setCancelled(true);
			Statusset.remove(p);
			Settings.setstatus(p.getUniqueId().toString(), msg);
			p.sendMessage(Prefix+ChatColor.GREEN+"Der Status wurde erfolgreich gesetzt!");
		}
	}
	
	public static ArrayList<Player> Statusset = new ArrayList<>();
	
public static String Prefix = ChatColor.GRAY+"["+ChatColor.RED+"Friends"+ChatColor.GRAY+"] ";
	
	public static ItemStack createitem(Material m,String title, Boolean enchantet, String beschreibung) {
		ItemStack item = new ItemStack(m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		if(enchantet) {
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
		}
		if(beschreibung!=null) {
			List<String> list = new ArrayList<>();
			list.add(ChatColor.GRAY+beschreibung);
			meta.setLore(list);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	
	public void setCustomSkull_Item(ItemMeta im, String link){
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", link).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = im.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(im, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
	}
	
	public static boolean hasPremiumfeatures(Player p) {
		if(!MySQLRang.playerExists(p.getUniqueId().toString())) {
			return false;
		}
		String rang = MySQLRang.getRangname(p.getUniqueId().toString());
		if(rang.equals("Admin")||rang.equals("Mod")||rang.equals("Sup")||rang.equals("Builder")||rang.equals("Dev")||rang.equals("Youtuber")||rang.equals("Premium")||rang.equals("Prem+")||rang.equals("Contant")) {
			return true;
		}else {
			return false;
		}
	}
	
}
