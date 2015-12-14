package io.github.kyzderp.cmdblockhelper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.BlockCommandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.PlayerInteractionListener;
import com.mumfrey.liteloader.Tickable;

public class LiteModCmdBlockHelper implements Tickable, OutboundChatFilter
{
	private CmdBlock cmdblock;
	private LinesReader reader;
	private Commands commands;
	private boolean justOpened;

	@Override
	public String getName() {return "Command Block Helper";}

	@Override
	public String getVersion() {return "1.0.0";}

	@Override
	public void init(File configPath) 
	{
		this.reader = new LinesReader();
		this.cmdblock = new CmdBlock(this, this.reader);
		this.commands = new Commands(this.cmdblock, this);
		this.justOpened = true;
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) 
	{
		if (!inGame)
			return;

		if (minecraft.currentScreen instanceof GuiCommandBlock && this.justOpened)
		{
			this.justOpened = false;
			GuiCommandBlock screen = (GuiCommandBlock) minecraft.currentScreen;
			try {
				this.cmdblock.run(screen);
				this.justOpened = true;
			} catch (Exception e) {
				e.printStackTrace();
				logError("Unable to access command block!");
			}
		}
	}

	@Override
	public boolean onSendChatMessage(String message) 
	{
		String[] tokens = message.split(" ");
		if (tokens[0].equalsIgnoreCase("/cmd"))
		{
			this.commands.handleCommand(message);
			return false;
		}
		return true;
	}

	/**
	 * Logs the message to the user
	 * @param message The message to log
	 * @param addPrefix Whether to add the mod-specific prefix or not
	 */
	public static void logMessage(String message, boolean addPrefix)
	{// "§8[§2§8] §a"
		if (addPrefix)
			message = "§8[§2CmdBlk§8] §a" + message;
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.GREEN));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	public static void logError(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText("§8[§4!§8] §c" + message + " §8[§4!§8]");
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.RED));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}
}
