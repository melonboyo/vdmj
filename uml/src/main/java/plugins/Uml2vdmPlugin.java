package plugins;

import java.io.File;
import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.Interpreter;

import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList; 

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Uml2vdmPlugin extends CommandPlugin {
    
    public Uml2vdmPlugin(Interpreter interpreter)
	{
		super(interpreter);
	}

	@Override
	public boolean run(String[] argv) throws Exception
	{
		String path = null;
		
		if (argv.length == 2)
		{
			path = argv[1];
		}
		else if (argv.length != 1)
		{
			help();
			return true;
		}	

		try 
		{
			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         	Document doc = dBuilder.parse(inputFile);
         	doc.getDocumentElement().normalize();
			
			
			vdmGenerator(doc);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}




	private boolean vdmGenerator(Document doc)
	{		
		NodeList nList = doc.getElementsByTagName("UML:Class");

		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			Node nNode = nList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				System.out.println("Class " + eElement.getAttribute("name"));
				
				NodeList attributeList = eElement.getElementsByTagName("UML:Attribute");
				NodeList operationList = eElement.getElementsByTagName("UML:Operation");
				
				instanceVariables(attributeList);
				operations(operationList);
				
				System.out.println("\n");
			}
		}	
		return true;
	}

	private void instanceVariables(NodeList list){

		System.out.println("instance variables \n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element aElement  = (Element) list.item(count);
			
			if (! (aElement.getAttribute("name").contains("«type»")) 
				|| aElement.getAttribute("name").contains("«value»"))
			{
				System.out.println(aElement.getAttribute("name"));
			}

		}

	}

	private void operations(NodeList list){

		System.out.println("operations \n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element oElement  = (Element) list.item(count);
			
			if (!oElement.getAttribute("name").contains("«function»"))
			{
				System.out.println(oElement.getAttribute("name"));
			}

		}

	}


	@Override
	public String help()
	{
		return "uml2vdm - generate VDM++ or VDM-RT from PlantUML";
	}
}
