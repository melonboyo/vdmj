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

package plugins;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;

public class UMLGenerator extends TCDefinitionVisitor<Object, StringBuilder>
{
	@Override
	public Object caseDefinition(TCDefinition node, StringBuilder arg)
	{
		return null;
	}

	@Override
	public Object caseClassDefinition(TCClassDefinition node, StringBuilder arg)
	{
		arg.append("class ");
		arg.append(node.name.getName());
		arg.append("\n{\n");
		
		for (TCDefinition def: node.definitions)
		{
			def.apply(this, arg);
		}
		
		arg.append("}\n");
		return null;
	}
	
	@Override
	public Object caseInstanceVariableDefinition(TCInstanceVariableDefinition node, StringBuilder arg)
	{
		arg.append(node.accessSpecifier);
		arg.append(" ");
		arg.append(node.name.getName() + "::" + node.getType());
		arg.append("\n");

		return null;
	}
	
	@Override
	public Object caseTypeDefinition(TCTypeDefinition node, StringBuilder arg)
	{
		arg.append(node.accessSpecifier);
		arg.append(" ");
		arg.append(node.name.getName());
		arg.append("\n");

		return null;
	}
	
	@Override
	public Object caseExplicitFunctionDefinition(TCExplicitFunctionDefinition node, StringBuilder arg)
	{
		arg.append(node.accessSpecifier);
		arg.append(" ");
		arg.append(node.name.getName());
		arg.append("\n");

		return null;
	}
	
	@Override
	public Object caseExplicitOperationDefinition(TCExplicitOperationDefinition node, StringBuilder arg)
	{
		arg.append(node.accessSpecifier);
		arg.append(" ");
		arg.append(node.name.getName());
		arg.append("\n");

		return null;
	}
	
	@Override
	public Object caseValueDefinition(TCValueDefinition node, StringBuilder arg)
	{
		for (TCDefinition def: node.getDefinitions())
		{
			arg.append(def.accessSpecifier);
			arg.append(" ");
			arg.append(def.name.getName());
			arg.append("\n");
		}

		return null;
	}
}
