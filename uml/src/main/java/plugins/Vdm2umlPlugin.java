/*******************************************************************************
 *
 *	Copyright (c) 2022 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *	SPDX-License-Identifier: GPL-3.0-or-later
 *
 ******************************************************************************/

package plugins.VDM2UML;

import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.runtime.Interpreter;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;



public class Vdm2umlPlugin extends CommandPlugin
{
	
	public Vdm2umlPlugin(Interpreter interpreter)
	{
		super(interpreter);
	}	

	@Override
	public boolean run(String[] argv) throws Exception
	{
		if (interpreter instanceof ClassInterpreter)
		{
			TCClassList classes = interpreter.getTC();
			
			Buffers buffers = new Buffers(); 

			for (TCClassDefinition cdef: classes)
			{
				cdef.apply(new UMLGenerator(), buffers);
			}
			
			System.out.println(buffers.defs.toString());
			System.out.println(buffers.asocs.toString());
		}
		else
		{
			System.err.println("Only available for VDM++ or VDM-RT");
		}
		
		return true;
	}

	@Override
	public String help()
	{
		return "vdm2uml - generate UML from VDM++ or VDM-RT";
	}
}
