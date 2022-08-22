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

import java.beans.Visibility;
import java.util.ArrayList;
import java.util.Map;

import javax.lang.model.util.ElementScanner14;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.lex.Token;
import com.fujitsu.vdmj.tc.definitions.TCAccessSpecifier;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;
import com.fujitsu.vdmj.tc.lex.TCNameToken;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.typechecker.NameScope;

public class UMLGenerator extends TCDefinitionVisitor<Object, Buffers>
{
	@Override
	public Object caseDefinition(TCDefinition node, Buffers arg)
	{
		return null;
	}

	@Override
	public Object caseClassDefinition(TCClassDefinition node, Buffers arg)
	{
		arg.defs.append("class ");
		
		arg.defs.append(node.name.getName());
		arg.defs.append("\n{\n");

		for (TCDefinition def: node.definitions)
		{
			def.apply(this, arg);
		}

		arg.defs.append("}\n\n");
		return null;
	}
	
	@Override
	public Object caseInstanceVariableDefinition(TCInstanceVariableDefinition node, Buffers arg)
	{	
		if (node.getType().isMap(LexLocation.ANY))
		{
			/** Creates qualified association */
			String mapName = node.name.getName();
			String className = node.classDefinition.name.getName();

			String mapping = removeBrackets(node.getType().toString());
			mapping = remove(mapping, "map");
			
			String[] seg1 = mapping.split("to");
			String qualifier = remove(seg1[0], " ");
			String endClass = seg1[seg1.length - 1];
			String mult = "";
			
			if (seg1[seg1.length - 1].contains("set"))
			{
				mult = " \"*\" ";
				endClass = remove(endClass, " set of ");
			} else 			
			if (seg1[seg1.length - 1].contains("seq"))
			{
				mult = " \"(*)\" ";
				endClass = remove(endClass, " seq of ");
			} else 				
			if (seg1[seg1.length - 1].contains("seq1"))
			{
				mult = " \"(*)\" ";
				endClass = remove(endClass, " seq1 of ");
			}
		
			arg.asocs.append(className + " \"[" + qualifier +"]\"" + " -->" + 
			mult + endClass + " : " + visibility(node.accessSpecifier) + mapName);
			arg.asocs.append("\n");

		} else if (typeContainsClass(node.getType(), arg.classes)) 
		{
			/** Creates associations with no qualifier
			 *  Does not yet account for complex types (union, product, set of (seq of *), etc.)
			 */
			String asocName = node.name.getName();
			String className = node.classDefinition.name.getName();
			String endClass = removeBrackets(node.getType().toString());
			String mult = "";
			
			if (endClass.contains("set"))
			{
				mult = "\"*\" ";
				endClass = remove(endClass, "set of ");
			} else
			if (endClass.contains("seq"))
			{
				mult = "\"(*)\" ";
				endClass = remove(endClass, "seq of ");
			} else				
			if (endClass.contains("seq1"))
			{
				mult = "\"(1..*)\" ";
				endClass = remove(endClass, "seq1 of ");
			}
		
			arg.asocs.append(className + " --> " + 
			mult + endClass + " : " + visibility(node.accessSpecifier) + asocName);
			arg.asocs.append("\n");
		} else
		{
			arg.defs.append("\t");
			arg.defs.append(visibility(node.accessSpecifier));
			arg.defs.append(" ");
			arg.defs.append(node.name.getName() + " : " + removeBrackets(node.getType().toString()));
			arg.defs.append("\n");
		}
		
		return null;
	}
	
	@Override
	public Object caseTypeDefinition(TCTypeDefinition node, Buffers arg)
	{
		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(" ");
		arg.defs.append(node.name.getName());
		//arg.defs.append(node.getType());
		arg.defs.append(" <<type>>");
		arg.defs.append("\n");		

		return null; 
	}

	@Override
	public Object caseValueDefinition(TCValueDefinition node, Buffers arg)
	{
		for (TCDefinition def: node.getDefinitions())
		{
			arg.defs.append("\t");
			arg.defs.append(visibility(def.accessSpecifier));
			arg.defs.append(" ");
			arg.defs.append(def.name.getName());
			arg.defs.append(" : ");
			arg.defs.append(def.getType());
			arg.defs.append(" <<value>>");
			arg.defs.append("\n");
		}

		return null;
	}
	

	@Override
	public Object caseExplicitFunctionDefinition(TCExplicitFunctionDefinition node, Buffers arg)
	{
		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(" ");
		arg.defs.append(node.name.getName());

		arg.defs.append(getPlantArgs(node.getType().toString()));

		arg.defs.append(" <<function>>");
		arg.defs.append("\n");

		return null;
	}
	
	@Override
	public Object caseExplicitOperationDefinition(TCExplicitOperationDefinition node, Buffers arg)
	{
		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(" ");
		arg.defs.append(node.name.getName());
		arg.defs.append(getPlantArgs(node.getType().toString()));
		arg.defs.append("\n");

		return null;
	}


	private String getPlantArgs(String args)
	{
		String str0 = removeBrackets(args);
		String splitter = "";
		if(str0.contains("->"))
			splitter = " ->";

		if(str0.contains("==>"))
			splitter = " ==>";
		
		String seg1[] = str0.split(splitter);
		String out = seg1[seg1.length - 1];
		String vdmArgLine = seg1[0];

		if(args.contains("*"))
		{
			String seg2[] = seg1[0].split(" \\* "); 
			vdmArgLine = seg2[0];
			for(int n = 1 ; n < seg2.length ; n++)
			{
				vdmArgLine = vdmArgLine + ", " + seg2[n];            
			} 
		}
		return "(" + vdmArgLine + ")" + ":" + out; 
	}

	private String visibility(TCAccessSpecifier access)
	{	
		String res = "";

		if (access.access == Token.PUBLIC)
			res += "+";
		else if (access.access == Token.PRIVATE)
			res += "-";
		else if (access.access == Token.PROTECTED)
			res += "#";
		
		if (access.isStatic)
			res += "[St]";
		
		return res;
	}  

	private String removeBrackets(String str)
	{
		if (str.contains("("))
			str = str.replace("(", "");
		
		if (str.contains(")"))
			str = str.replace(")", "");

		return str;
	}

	private String remove(String str, String remove)
	{
		return str.replace(remove, "");
	}

	private Boolean typeContainsClass(TCType type, ArrayList<String> classList)
	{	
		if (type.isClass(Buffers.env))
			return true;
		
		if (type.definitions == null)
			System.out.println("No definitions");
		else
			for (TCDefinition def : type.definitions)
				System.out.println("Defintion: " + def.name.toString());		
		
		String typeString = removeBrackets(type.toString());
		String[] segs = typeString.split(" of | to ");
		for (String seg : segs)
			if (classList.contains(seg))
			{
				return true;
			}
		return false;
	}
}	

