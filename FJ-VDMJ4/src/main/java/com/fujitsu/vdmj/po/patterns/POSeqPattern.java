/*******************************************************************************
 *
 *	Copyright (c) 2016 Fujitsu Services Ltd.
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
 *
 ******************************************************************************/

package com.fujitsu.vdmj.po.patterns;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.po.definitions.PODefinitionList;
import com.fujitsu.vdmj.po.expressions.POExpression;
import com.fujitsu.vdmj.po.expressions.POExpressionList;
import com.fujitsu.vdmj.po.expressions.POSeqEnumExpression;
import com.fujitsu.vdmj.po.patterns.visitors.POPatternVisitor;
import com.fujitsu.vdmj.tc.types.TCType;


public class POSeqPattern extends POPattern
{
	private static final long serialVersionUID = 1L;
	public final POPatternList plist;

	public POSeqPattern(LexLocation location, POPatternList list)
	{
		super(location);
		this.plist = list;
	}

	@Override
	public String toString()
	{
		return "[" + plist.toString() + "]";
	}

	@Override
	public POExpression getMatchingExpression()
	{
		POExpressionList list = new POExpressionList();

		for (POPattern p: plist)
		{
			list.add(p.getMatchingExpression());
		}

		return new POSeqEnumExpression(location, list, null);
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public PODefinitionList getAllDefinitions(TCType type)
	{
		PODefinitionList defs = new PODefinitionList();
		TCType elem = type.getSeq().seqof;

		for (POPattern p: plist)
		{
			defs.addAll(p.getAllDefinitions(elem));
		}

		return defs;
	}

	@Override
	public boolean isSimple()
	{
		return plist.isSimple();
	}

	@Override
	public <R, S> R apply(POPatternVisitor<R, S> visitor, S arg)
	{
		return visitor.caseSeqPattern(this, arg);
	}
}
