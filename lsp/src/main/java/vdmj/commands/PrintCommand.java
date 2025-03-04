/*******************************************************************************
 *
 *	Copyright (c) 2020 Nick Battle.
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

package vdmj.commands;

import java.io.IOException;

import dap.DAPMessageList;
import dap.DAPRequest;
import dap.ExpressionExecutor;
import workspace.DAPWorkspaceManager;

public class PrintCommand extends Command implements InitRunnable, ScriptRunnable
{
	public static final String USAGE = "Usage: print <expression>";
	public static final String[] HELP = { "print", "print <exp> - evaluate an expression" };
	
	public final String expression;

	public PrintCommand(String line)
	{
		String[] parts = line.split("\\s+", 2);
		
		if (parts.length == 2)
		{
			this.expression = parts[1];
		}
		else
		{
			throw new IllegalArgumentException(USAGE);
		}
	}
	
	@Override
	public DAPMessageList run(DAPRequest request)
	{
		ExpressionExecutor executor = new ExpressionExecutor("print", request, expression);
		executor.start();
		return null;
	}

	@Override
	public String initRun(DAPRequest request)
	{
		try
		{
			DAPWorkspaceManager manager = DAPWorkspaceManager.getInstance();
			return manager.getInterpreter().execute(expression).toString();
		}
		catch (Exception e)
		{
			return "Cannot evaluate " + expression + " : " + e.getMessage();
		}
	}
	
	@Override
	public String getExpression()
	{
		return expression;
	}
	
	@Override
	public String format(String result)
	{
		return expression + " = " + result;
	}

	@Override
	public boolean notWhenRunning()
	{
		return true;
	}

	@Override
	public String scriptRun(DAPRequest request) throws IOException
	{
		/**
		 * We are executing this on the main DAP thread from ScriptCommand. So we cannot
		 * stop at a breakpoint because the DAP thread is not listening for Client messages.
		 */
		return initRun(request);	// NB. No debug reader
	}
}
