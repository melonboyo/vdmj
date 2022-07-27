package plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;

import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.runtime.Interpreter;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;
public class Uml2vdmPlugin extends CommandPlugin {
    
    public Uml2vdmPlugin(Interpreter interpreter)
	{
		super(interpreter);
	}

	@Override
	public boolean run(String[] argv) throws Exception
	{
		String inputfilepath = null;

		if (argv.length == 2)
		{
			inputfilepath = argv[1];
		}
		else if (argv.length != 1)
		{
			help();
			return true;
		}
		File inputfile = new File(inputfilepath);
		BufferedReader br
            = new BufferedReader(new FileReader(inputfile));
 
        String st;
        while ((st = br.readLine()) != null)
            System.out.println(st);
		br.close();

		return true;
	}

	@Override
	public String help()
	{
		return "uml2vdm [plantuml model path] - Generate VDM++ from PlantUML file";
	}
}
