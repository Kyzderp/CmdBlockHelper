package io.github.kyzderp.cmdblockhelper;

public class Commands 
{
	private CmdBlock executor;
	private LiteModCmdBlockHelper main;
	private LinesReader reader;

	public Commands(CmdBlock executor, LiteModCmdBlockHelper main, LinesReader reader)
	{
		this.executor = executor;
		this.main = main;
		this.reader = reader;
	}

	public void handleCommand(String message)
	{
		String[] tokens = message.split(" ");

		if (tokens.length < 2 || tokens[1].equalsIgnoreCase("help"))
		{
			String[] commands = {
					"load <filename> - Loads file under .minecraft/litemodconfig/1.8/CmdBlockHelper/",
					"index <#> - Skips to specified line number (starts from 0)",
					"next - Skips to the next line",
					"prev - Skips backwards one line",
					"replace <<regex>> <<replacement>> - Include the angle brackets around",
					"command - Run the command in .minecraft/litemodconfig/1.8/CmdBlockHelper/command.txt"
			};
			LiteModCmdBlockHelper.logMessage("§2" + this.main.getName() + " §8[§2v" 
					+ this.main.getVersion() + "§8] §2commands:", false);
			for (String command: commands)
				LiteModCmdBlockHelper.logMessage("/cmd " + command, false);
		} // HELP

		else if (tokens[1].equalsIgnoreCase("command"))
		{
			String command = this.reader.loadCommand();
			if (command == null || command.equals(""))
			{
				LiteModCmdBlockHelper.logError("Cannot find command.txt!");
				return;
			}
			else if (command.matches("(?i)/cmd"))
			{
				LiteModCmdBlockHelper.logError("This is not a /cmd command!");
				return;
			}
			this.handleCommand(command);
			return;
		}

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
			try {
				String[] args = message.split("<");
				//cmd replace <\.9 ~> <.2 ~>
				if (args.length != 3)
					LiteModCmdBlockHelper.logError("Usage: /cmd replace <<regex>> <<replacement>>");
				else
				{
					String regex = args[1].split(">")[0];
					String replacement = args[2].split(">")[0];
					this.executor.replace(regex, replacement);
					LiteModCmdBlockHelper.logMessage("Replacing " + regex + " with " + replacement, true);
				}
			} catch (Exception e) {
				LiteModCmdBlockHelper.logError("Usage: /cmd replace <<regex>> <<replacement>>");
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
