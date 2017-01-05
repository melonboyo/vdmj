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

package com.fujitsu.vdmj;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.fujitsu.vdmj.Settings;

import com.fujitsu.vdmj.config.Properties;
import com.fujitsu.vdmj.lex.Dialect;
import com.fujitsu.vdmj.messages.Console;
import com.fujitsu.vdmj.runtime.Interpreter;

/**
 * The main class of the VDMJ parser/checker/interpreter.
 */
abstract public class VDMJ
{
	protected static boolean warnings = true;
	protected static boolean interpret = false;
	protected static boolean pog = false;
	protected static boolean quiet = false;
	protected static String script = null;
	protected static String logfile = null;

	public static String filecharset = Charset.defaultCharset().name();

	/**
	 * The main method. This validates the arguments, then parses and type
	 * checks the files provided (if any), and finally enters the interpreter
	 * if required.
	 *
	 * @param args Arguments passed to the program.
	 */

	public static void main(String[] args)
	{
		List<File> filenames = new Vector<File>();
		List<File> pathnames = new Vector<File>();
		List<String> largs = Arrays.asList(args);
		VDMJ controller = null;
		Dialect dialect = Dialect.VDM_SL;
		String remoteName = null;
		Class<RemoteControl> remoteClass = null;
		String defaultName = null;

		Properties.init();		// Read properties file, if any
		Settings.usingCmdLine = true;

		for (Iterator<String> i = largs.iterator(); i.hasNext();)
		{
			String arg = i.next();

    		if (arg.equals("-vdmsl"))
    		{
    			controller = new VDMSL();
    			dialect = Dialect.VDM_SL;
    		}
    		else if (arg.equals("-vdmpp"))
    		{
    			controller = new VDMPP();
    			dialect = Dialect.VDM_PP;
    		}
    		else if (arg.equals("-vdmrt"))
    		{
    			controller = new VDMRT();
    			dialect = Dialect.VDM_RT;
    		}
    		else if (arg.equals("-v"))		// Exit if this option is used.
    		{
    			String version = getVersion();

    			if (version == null)
    			{
    				println("Cannot determine VDMJ version");
        			System.exit(1);
    			}
    			else
    			{
    				println("VDMJ version = " + version);
        			System.exit(0);
    			}
    			
    		}
    		else if (arg.equals("-w"))
    		{
    			warnings = false;
    		}
    		else if (arg.equals("-i"))
    		{
    			interpret = true;
    		}
    		else if (arg.equals("-p"))
    		{
    			pog = true;
    			interpret = false;
    		}
    		else if (arg.equals("-q"))
    		{
    			quiet = true;
    		}
    		else if (arg.equals("-e"))
    		{
    			interpret = true;
    			pog = false;

    			if (i.hasNext())
    			{
    				script = i.next();
    			}
    			else
    			{
    				usage("-e option requires an expression");
    			}
    		}
    		else if (arg.equals("-c"))
    		{
    			if (i.hasNext())
    			{
    				filecharset = validateCharset(i.next());
    			}
    			else
    			{
    				usage("-c option requires a charset name");
    			}
    		}
    		else if (arg.equals("-t"))
    		{
    			if (i.hasNext())
    			{
    				Console.setCharset(validateCharset(i.next()));
    			}
    			else
    			{
    				usage("-t option requires a charset name");
    			}
    		}
    		else if (arg.equals("-r"))
    		{
    			if (i.hasNext())
    			{
    				Settings.release = Release.lookup(i.next());

    				if (Settings.release == null)
    				{
    					usage("-r option must be " + Release.list());
    				}
    			}
    			else
    			{
    				usage("-r option requires a VDM release");
    			}
    		}
    		else if (arg.equals("-pre"))
    		{
    			Settings.prechecks = false;
    		}
    		else if (arg.equals("-post"))
    		{
    			Settings.postchecks = false;
    		}
    		else if (arg.equals("-inv"))
    		{
    			Settings.invchecks = false;
    		}
    		else if (arg.equals("-dtc"))
    		{
    			// NB. Turn off both when no DTC
    			Settings.invchecks = false;
    			Settings.dynamictypechecks = false;
    		}
    		else if (arg.equals("-exceptions"))
    		{
    			Settings.exceptions = true;
    		}
    		else if (arg.equals("-measures"))
    		{
    			Settings.measureChecks = false;
    		}
    		else if (arg.equals("-log"))
    		{
    			if (i.hasNext())
    			{
    				logfile = i.next();
    			}
    			else
    			{
    				usage("-log option requires a filename");
    			}
    		}
    		else if (arg.equals("-remote"))
    		{
    			if (i.hasNext())
    			{
    				interpret = true;
    				remoteName = i.next();
    			}
    			else
    			{
    				usage("-remote option requires a Java classname");
    			}
    		}
    		else if (arg.equals("-default"))
    		{
    			if (i.hasNext())
    			{
       				defaultName = i.next();
    			}
    			else
    			{
    				usage("-default option requires a name");
    			}
    		}
    		else if (arg.equals("-path"))
    		{
    			if (i.hasNext())
    			{
       				File path = new File(i.next());
       				
       				if (path.isDirectory())
       				{
       					pathnames.add(path);
       				}
       				else
       				{
       					usage(path + " is not a directory");
       				}
    			}
    			else
    			{
    				usage("-path option requires a directory");
    			}
    		}
    		else if (arg.equals("-precision"))
    		{
    			if (i.hasNext())
    			{
       				int precision = Integer.parseInt(i.next());
       				
       				if (precision < 10)
       				{
       					usage("Precision argument must be >= 10");
       				}
       				else
       				{
       					Settings.precision = new MathContext(precision, RoundingMode.HALF_UP);
       				}
    			}
    			else
    			{
    				usage("-precision option requires a value");
    			}
    		}
    		else if (arg.startsWith("-"))
    		{
    			usage("Unknown option " + arg);
    		}
    		else
    		{
    			// It's a file or a directory
    			File file = new File(arg);

				if (file.isDirectory())
				{
 					for (File subfile: file.listFiles(dialect.getFilter()))
					{
						if (subfile.isFile())
						{
							filenames.add(subfile);
						}
					}
				}
    			else
    			{
    				if (file.exists())
    				{
    					filenames.add(file);
    				}
    				else
    				{
    					boolean OK = false;
    					
    					for (File path: pathnames)
    					{
    						File pfile = new File(path, arg);
    						
    						if (pfile.exists())
    						{
    							filenames.add(pfile);
    							OK = true;
    							break;
    						}
    					}
    					
    					if (!OK)
    					{
    						usage("Cannot find file " + file);
    					}
    				}
    			}
    		}
		}

		if (controller == null)
		{
			usage("You must specify either -vdmsl, -vdmpp or -vdmrt");
		}

		if (logfile != null)// && !(controller instanceof VDMRT))
		{
			usage("The -log option can only be used with -vdmrt");
		}

		if (remoteName != null)
		{
			remoteClass = getRemoteClass(remoteName);
		}

		ExitStatus status = null;

		if (filenames.isEmpty() && (!interpret || remoteClass != null))
		{
			usage("You didn't specify any files");
			status = ExitStatus.EXIT_ERRORS;
		}
		else
		{
			do
			{
				if (filenames.isEmpty())
				{
					status = controller.interpret(filenames, null);
				}
				else
				{
            		status = controller.parse(filenames);

            		if (status == ExitStatus.EXIT_OK)
            		{
            			status = controller.typeCheck();

            			if (status == ExitStatus.EXIT_OK && interpret)
            			{
            				if (remoteClass == null)
            				{
								status = controller.interpret(filenames, defaultName);
            				}
        					else
        					{
        						try
								{
									RemoteControl remote = remoteClass.newInstance();
									Interpreter i = controller.getInterpreter();

									if (defaultName != null)
									{
										i.setDefaultName(defaultName);
									}

									i.init();

									try
									{
										remote.run(new RemoteInterpreter(i));
										status = ExitStatus.EXIT_OK;
									}
									catch (Exception e)
									{
										while (e instanceof InvocationTargetException)
										{
											e = (Exception)e.getCause();
										}
										
										println(e.getMessage());
										status = ExitStatus.EXIT_ERRORS;
									}
								}
								catch (InstantiationException e)
								{
									usage("Cannot instantiate " + remoteName);
								}
								catch (Exception e)
								{
									usage(e.getMessage());
								}
        					}
            			}
            		}
				}
			}
			while (status == ExitStatus.RELOAD);
		}

		if (interpret)
		{
			infoln("Bye");
		}

		System.exit(status == ExitStatus.EXIT_OK ? 0 : 1);
	}

	@SuppressWarnings("unchecked")
	private static Class<RemoteControl> getRemoteClass(String remoteName)
	{
		try
		{
			return (Class<RemoteControl>) ClassLoader.getSystemClassLoader().loadClass(remoteName);
		}
		catch (ClassNotFoundException e)
		{
			usage("Cannot locate " + remoteName + " on the CLASSPATH");
		}
		catch (ClassCastException e)
		{
			usage(remoteName + " does not implement RemoteControl interface");
		}
		
		return null;
	}

	private static String getVersion()
	{
		try
		{
			String path = VDMJ.class.getName().replaceAll("\\.", "/");
			URL url = VDMJ.class.getResource("/" + path + ".class");
			JarURLConnection conn = (JarURLConnection)url.openConnection();
		    JarFile jar = conn.getJarFile();
			Manifest mf = jar.getManifest();
			String version = (String)mf.getMainAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION);
			return version;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static void usage(String msg)
	{
		System.err.println("VDMJ: " + msg + "\n");
		System.err.println("Usage: VDMJ <-vdmsl | -vdmpp | -vdmrt> [<options>] [<files or dirs>]");
		System.err.println("-vdmsl: parse files as VDM-SL");
		System.err.println("-vdmpp: parse files as VDM++");
		System.err.println("-vdmrt: parse files as VDM-RT");
		System.err.println("-path: search path for files");
		System.err.println("-v: show VDMJ jar version");
		System.err.println("-r <release>: VDM language release");
		System.err.println("-w: suppress warning messages");
		System.err.println("-q: suppress information messages");
		System.err.println("-i: run the interpreter if successfully type checked");
		System.err.println("-p: generate proof obligations and stop");
		System.err.println("-e <exp>: evaluate <exp> and stop");
		System.err.println("-c <charset>: select a file charset");
		System.err.println("-t <charset>: select a console charset");
		System.err.println("-default <name>: set the default module/class");
		System.err.println("-pre: disable precondition checks");
		System.err.println("-post: disable postcondition checks");
		System.err.println("-inv: disable type/state invariant checks");
		System.err.println("-dtc: disable all dynamic type checking");
		System.err.println("-exceptions: raise pre/post/inv violations as <RuntimeError>");
		System.err.println("-measures: disable recursive measure checking");
		System.err.println("-log <filename>: enable real-time event logging");
		System.err.println("-remote <class>: enable remote control");
		System.err.println("-precision <n>: set real number precision to n places");
		
		System.exit(1);
	}

	/**
	 * Parse the list of files passed. The value returned is the number of
	 * syntax errors encountered.
	 *
	 * @param files The files to parse.
	 * @return The number of syntax errors.
	 */

	public abstract ExitStatus parse(List<File> files);

	/**
	 * TCType check the files previously parsed by {@link #parse(List)}. The
	 * value returned is the number of type checking errors.
	 *
	 * @return The number of type check errors.
	 */

	public abstract ExitStatus typeCheck();

	/**
	 * Generate an interpreter from the classes parsed.
	 * @return An initialized interpreter.
	 * @throws Exception
	 */

	public abstract Interpreter getInterpreter() throws Exception;

	/**
	 * Interpret the type checked specification. The number returned is the
	 * number of runtime errors not caught by the interpreter (usually zero
	 * or one).
	 *
	 * @param filenames The filenames currently loaded.
	 * @param defaultName The default module or class (or null).
	 * @return The exit status of the interpreter.
	 */

	abstract protected ExitStatus interpret(List<File> filenames, String defaultName);

//	public void setWarnings(boolean w)
//	{
//		warnings = w;
//	}
//
//	public void setQuiet(boolean q)
//	{
//		quiet = q;
//	}
//
//	public void setCharset(String charset)
//	{
//		filecharset = charset;
//		Console.setCharset(charset);
//	}

	protected static void info(String m)
	{
		if (!quiet)
		{
			print(m);
		}
	}

	protected static void infoln(String m)
	{
		if (!quiet)
		{
			println(m);
		}
	}

	protected static void print(String m)
	{
		Console.out.print(m);
		Console.out.flush();
	}

	protected static void println(String m)
	{
		Console.out.println(m);
		Console.out.flush();
	}

	protected String plural(int n, String s, String pl)
	{
		return n + " " + (n != 1 ? s + pl : s);
	}

	private static String validateCharset(String cs)
	{
		if (!Charset.isSupported(cs))
		{
			println("Charset " + cs + " is not supported\n");
			println("Available charsets:");
			println("Default = " + Charset.defaultCharset());
			Map<String,Charset> available = Charset.availableCharsets();

			for (Entry<String, Charset> entry: available.entrySet())
			{
				println(entry.getKey() + " " + available.get(entry.getValue()).aliases());
			}

			println("");
			usage("Charset " + cs + " is not supported");
		}

		return cs;
	}
}
