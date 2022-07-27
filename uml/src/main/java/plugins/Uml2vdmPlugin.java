package plugins;

import java.io.File;
import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.runtime.Interpreter;
/* import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList; */

import java.io.BufferedReader;
import java.io.FileReader;

public class Uml2vdmPlugin extends CommandPlugin {
    
    public Uml2vdmPlugin(Interpreter interpreter)
	{
		super(interpreter);
	}

	@Override
	public boolean run(String[] argv) throws Exception
	{
		String path = null;
		
		if (argv.length == 2)
		{
			path = argv[1];
		}
		else if (argv.length != 1)
		{
			help();
			return true;
		}	

		File file = new File(path);

		try (BufferedReader br = new BufferedReader(new FileReader(file.getName()))) 
		{
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} 
		
		return true;
	}
	
	@Override
	public String help()
	{
		return "uml2vdm - generate VDM++ or VDM-RT from PlantUML";
	}
}
