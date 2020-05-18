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
 *
 ******************************************************************************/

package lsp.workspace;

import java.io.File;
import java.net.URISyntaxException;

import json.JSONArray;
import json.JSONObject;
import lsp.LSPHandler;
import lsp.LSPServerState;
import lsp.Utils;
import lsp.textdocument.WatchKind;
import rpc.RPCErrors;
import rpc.RPCMessageList;
import rpc.RPCRequest;

public class DidChangeWSHandler extends LSPHandler
{
	public DidChangeWSHandler(LSPServerState state)
	{
		super(state);
	}

	@Override
	public RPCMessageList request(RPCRequest request)
	{
		switch (request.getMethod())
		{
			case "workspace/didChangeWatchedFiles":
				return didChangeWatchedFiles(request);
			
			case "workspace/didChangeWorkspaceFolders":
				return didChangeWorkspaceFolders(request);
				
			default:
				return new RPCMessageList(request, RPCErrors.InternalError, "Unexpected workspace message");
		}
	}
	
	private RPCMessageList didChangeWorkspaceFolders(RPCRequest request)
	{
		return new RPCMessageList(request, RPCErrors.InvalidRequest, request.getMethod());
	}

	private RPCMessageList didChangeWatchedFiles(RPCRequest request)
	{
		try
		{
			JSONObject params = request.get("params");
			JSONArray changes = params.get("changes");
			RPCMessageList responses = new RPCMessageList();
			
			for (Object fileEvent: changes)
			{
				if (fileEvent instanceof JSONObject)
				{
					JSONObject change = (JSONObject)fileEvent; 
					File file = Utils.uriToFile(change.get("uri"));
					WatchKind type = WatchKind.kindOf(change.get("type"));
					lspServerState.getManager().changeWatchedFile(request, file, type);
					
					if (type == WatchKind.DELETE)
					{
						// clear all diagnostics from deleted files
						JSONObject diags = new JSONObject("uri", file.toURI().toString(), "diagnostics", new JSONArray());
						responses.add(new RPCRequest("textDocument/publishDiagnostics", diags));
					}
				}
			}
			
			// Do type checking after the changes are processed
			responses.addAll(lspServerState.getManager().afterChangeWatchedFiles(request));
			
			return responses;
		}
		catch (URISyntaxException e)
		{
			return new RPCMessageList(request, "URI syntax error");
		}
		catch (Exception e)
		{
			return new RPCMessageList(request, RPCErrors.InternalError, e.getMessage());
		}
	}
}
