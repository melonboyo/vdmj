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

package com.fujitsu.vdmj.po.expressions;

import java.io.Serializable;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.po.PONode;
import com.fujitsu.vdmj.pog.POContextStack;
import com.fujitsu.vdmj.pog.ProofObligationList;

/**
 *	The parent class of all VDM expressions.
 */
public abstract class POExpression extends PONode implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Generate an expression at the given location.
	 *
	 * @param location	The location of the new expression.
	 */

	public POExpression(LexLocation location)
	{
		super(location);
	}

	/**
	 * Generate an expression at the same location as the expression passed.
	 * This is used when a compound expression, comprising several
	 * subexpressions, is being constructed. The expression passed "up" is
	 * considered the location of the overall expression. For example, a
	 * function application involves an expression for the function to apply,
	 * plus a list of expressions for the arguments. The location of the
	 * expression for the function (often just a variable name) is considered
	 * the location of the entire function application.
	 *
	 * @param exp The expression containing the location.
	 */
	public POExpression(POExpression exp)
	{
		this(exp.location);
	}

	@Override
	public abstract String toString();

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof POExpression)
		{
			POExpression oe = (POExpression)other;
			return toString().equals(oe.toString());	// For now...
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * Get a list of proof obligations from the expression.
	 *
	 * @param ctxt The call context.
	 * @return The list of proof obligations.
	 */

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return new ProofObligationList();
	}
	
	/**
	 * Get the name of the precondition function of this expression, if it is
	 * a function expression that identifies a function with a precondition.
	 * This is used during proof obligation generation. It is implemented in
	 * the VariableExpression class.
	 */
	public String getPreName()
	{
		return null;
	}

	/**
	 * Implemented by all expressions to allow visitor processing.
	 */
	abstract public <R, S> R apply(POExpressionVisitor<R, S> visitor, S arg);
}
