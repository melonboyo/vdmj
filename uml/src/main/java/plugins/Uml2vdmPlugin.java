package plugins;

import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.runtime.Interpreter;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;
import org.

public class Uml2vdmPlugin extends CommandPlugin {

    { parse, parseFile, formatters } = require('plantuml-parser');
    
    public Uml2vdmPlugin(Interpreter interpreter)
	{
		super(interpreter);
	}

	@Override
	public boolean run(String[] argv) throws Exception
	{		
		return true;
	}

	@Override
	public String help()
	{
		return "uml2vdm - generate VDM++ or VDM-RT from PlantUML";
	}
}
