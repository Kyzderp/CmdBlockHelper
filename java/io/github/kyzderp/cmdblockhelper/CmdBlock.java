package io.github.kyzderp.cmdblockhelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class CmdBlock 
{
	private LinesReader reader;
	private String status; // set | replace | append | prepend | idle

	private List<String> lines;
	private int lineIndex;

	private String regex;
	private String replacement;
	
	public CmdBlock(LiteModCmdBlockHelper main, LinesReader reader)
	{
		this.reader = reader;
		this.status = "idle";
		this.lines = new LinkedList<String>();
		this.lineIndex = 0;
	}
	// TODO: symbol with index, with hover showing what the next line is
	// TODO: copy from cmd blocks into file
	

	//////////////////////////// From File /////////////////////////////

	/**
	 * Loads a file from .minecraft/liteconfig/1.8/CmdBlockHelper/
	 */
	public void loadFile(String filename)
	{
		List<String> newlines = this.reader.loadFile(filename);
		if (newlines == null)
			LiteModCmdBlockHelper.logError("Failed to load file: " + filename);
		else
		{
			this.lines = newlines;
			LiteModCmdBlockHelper.logMessage("Loaded file: " + filename
					+ " Auto-filling has been turned on and index reset to 0.", true);
			this.lineIndex = 0;
			this.status = "set";
		}
	}

	/**
	 * Sets the index
	 */
	public void setIndex(int i)
	{
		if (i < 0 || i >= this.lines.size())
		{
			LiteModCmdBlockHelper.logError("The index must be from 0 to " + this.lines.size());
			return;
		}
		this.lineIndex = i;
		LiteModCmdBlockHelper.logMessage("Index set to " + i, true);
	}

	/**
	 * Increases the index
	 */
	public void next() 
	{ 
		if (this.lineIndex >= this.lines.size())
		{
			LiteModCmdBlockHelper.logError("You are already at the end of the file.");
			return;
		}
		this.lineIndex++; 
		LiteModCmdBlockHelper.logMessage("Index increased to " + this.lineIndex, true);
	}

	/**
	 * Decreases the index
	 */
	public void prev() 
	{ 
		if (this.lineIndex <= 0)
		{
			LiteModCmdBlockHelper.logError("You are already at the beginning of the file.");
			return;
		}
		this.lineIndex--; 
		LiteModCmdBlockHelper.logMessage("Index decreased to " + this.lineIndex, true);
	}
	
	
	///////////////////////// Replace ///////////////////////////
	
	public void replace(String regex, String replacement)
	{
		this.regex = regex;
		this.replacement = replacement;
		this.status = "replace";
	}
	
	///////////////////////// Controls //////////////////////////
	
	public void stop()
	{
		this.status = "idle";
	}
	

	/////////////////////////////////////////////////////////////
	///////////////////////// Running ///////////////////////////
	/////////////////////////////////////////////////////////////

	/**
	 * What actually gets called when a command block is opened
	 */
	public void run(GuiScreen screen) throws Exception
	{
		if (this.status.equals("idle"))
			return; // Nothing is happening
		else if (this.status.equals("set"))
			this.setCommand(screen);
		else if (this.status.equals("replace"))
			this.replaceCommand(screen);
		else
			return;
	}

	/**
	 * Sets the command inside of the command block when called
	 */
	private void setCommand(GuiScreen screen) throws Exception
	{
		Field field = GuiCommandBlock.class.getDeclaredField("commandTextField");
		field.setAccessible(true);
		GuiTextField textfield = (GuiTextField) field.get(screen);
		textfield.setText(lines.get(lineIndex));
		screen.updateScreen();
		
		Method clickmethod = GuiCommandBlock.class.getDeclaredMethod("actionPerformed", GuiButton.class); 
		clickmethod.setAccessible(true);
		clickmethod.invoke(screen, new GuiButton(0, 0, 0, ""));
		
		this.lineIndex++;
		if (this.lineIndex >= this.lines.size()) // Reached the end, stop
		{
			LiteModCmdBlockHelper.logMessage("End of file reached, command setting stopped.", true);
			this.status = "idle";
		}
		return;
	}

	/**
	 * Replaces parts of the command in the command block
	 */
	private void replaceCommand(GuiScreen screen) throws Exception
	{
		Field field = GuiCommandBlock.class.getDeclaredField("commandTextField");
		field.setAccessible(true);
		GuiTextField textfield = (GuiTextField) field.get(screen);
		textfield.setText(textfield.getText().replaceAll(this.regex, this.replacement));
		screen.updateScreen();
		
		Method clickmethod = GuiCommandBlock.class.getDeclaredMethod("actionPerformed", GuiButton.class); 
		clickmethod.setAccessible(true);
		clickmethod.invoke(screen, new GuiButton(0, 0, 0, ""));
		return;
	}
}