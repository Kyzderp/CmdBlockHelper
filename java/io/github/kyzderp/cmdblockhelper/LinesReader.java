package io.github.kyzderp.cmdblockhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class LinesReader 
{
	private final File dirs = new File(Minecraft.getMinecraft().mcDataDir, "liteconfig" + File.separator 
			+ "config.1.8" + File.separator + "CmdBlockHelper");

	
	public LinesReader()
	{
	}
	
	public String loadCommand()
	{
		File path = new File(dirs.getPath() + File.separator + "command.txt");
		if (!path.exists())
			return "";
		Scanner scan;
		try {
			scan = new Scanner(path);
		} catch (FileNotFoundException e) {
			return "";
		}
		if (scan.hasNext())
		{
			String line = scan.nextLine();
			scan.close();
			return line;
		}
		scan.close();
		return "";
	}
	
	public List<String> loadFile(String filename)
	{
		if (!filename.matches(".*\\..*"))
			filename = filename + ".txt";
		File path = new File(dirs.getPath() + File.separator + filename);

		if (!path.exists())
			return null;
		Scanner scan;
		try {
			scan = new Scanner(path);
		} catch (FileNotFoundException e) {
			return null;
		}
		List<String> toReturn = new LinkedList<String>();
		while (scan.hasNext())
		{
			String line = scan.nextLine();
			if (line.matches("^[A-Za-z/].*"))
				toReturn.add(line);
		}
		scan.close();
		return toReturn;
	}
}
