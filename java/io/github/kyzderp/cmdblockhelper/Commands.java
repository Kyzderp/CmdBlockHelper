package io.github.kyzderp.cmdblockhelper;

public class Commands 
{
	private CmdBlock executor;
	private LiteModCmdBlockHelper main;
	
	public Commands(CmdBlock executor, LiteModCmdBlockHelper main)
	{
		this.executor = executor;
		this.main = main;
	}
	
	public void handleCommand(String message)
	{
		System.out.println("blah");
		String[] tokens = message.split(" ");

		if (tokens.length < 2 || tokens[1].equalsIgnoreCase("help"))
		{
			String[] commands = {
				"load <filename> - Loads file under .minecraft/litemodconfig/1.8/CmdBlockHelper/",
				"index <#> - Skips to specified line number (starts from 0)",
				"next - Skips to the next line",
				"prev - Skips backwards one line"
			};
			LiteModCmdBlockHelper.logMessage("§2" + this.main.getName() + " §8[§2v" 
				+ this.main.getVersion() + "§8] §2commands:", false);
			for (String command: commands)
				LiteModCmdBlockHelper.logMessage("/cmd " + command, false);
		} // HELP


		/////////////// LOADING FROM FILE //////////////////
		else if (tokens[1].equalsIgnoreCase("load"))
		{
			if (tokens.length != 3)
				LiteModCmdBlockHelper.logError("Usage: /cmd load <filename>");
			else
				this.executor.loadFile(tokens[2]);
		} // LOAD

		else if (tokens[1].equalsIgnoreCase("index"))
		{
			try {
				this.executor.setIndex(Integer.parseInt(tokens[1]));
			} catch (Exception e) {
				LiteModCmdBlockHelper.logError("Usage: /cmd index <integer>");
			}
		} // INDEX

		else if (tokens[1].equalsIgnoreCase("next"))
		{
			this.executor.next();
		} // NEXT

		else if (tokens[1].equalsIgnoreCase("prev"))
		{
			this.executor.prev();
		} // PREV


		///////////////// REPLACING //////////////////
		else if (tokens[1].equalsIgnoreCase("replace"))
		{
			if (tokens.length != 4)
				LiteModCmdBlockHelper.logError("Usage: /cmd replace <regex> <replacement>");
			else
			{
				this.executor.replace(tokens[2], tokens[3]);
				LiteModCmdBlockHelper.logMessage("Replacing " + tokens[2] + " with " + tokens[3], true);
			}
		} // REPLACE
		
		
		////////////////// CONTROL ///////////////////
		
		else if (tokens[1].equalsIgnoreCase("off"))
		{
			this.executor.stop();
			LiteModCmdBlockHelper.logMessage("Turned off Command Block Helper.", true);
		}
	}
}
