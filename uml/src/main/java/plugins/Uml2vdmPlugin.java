package plugins;

import java.io.File;
import com.fujitsu.vdmj.commands.CommandPlugin;
import com.fujitsu.vdmj.runtime.Interpreter;

import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class Uml2vdmPlugin extends CommandPlugin {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList =new ArrayList<XMIClass>();  

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
	
		NodeList cList = doc.getElementsByTagName("UML:Class");
		NodeList gList = doc.getElementsByTagName("UML:Generalization");
		NodeList rList = doc.getElementsByTagName("UML:Association");

		createClasses(cList);
		addInheritance(gList);
		addAssociations(rList);

		VDMPrinter printer = new VDMPrinter(classList);

		printer.printVDM();

		return true;
	}
	
	private void createClasses(NodeList list)
	{
		for (int temp = 0; temp < list.getLength(); temp++) 
		{
			Node nNode = list.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element cElement = (Element) nNode;
				
				XMIClass c = new XMIClass(cElement);
				classList.add(c);
				
				if (! (cElement.getAttribute("xmi.id") == null || (cElement == null)))
				{
					cHash.put(cElement.getAttribute("xmi.id"), c);
				}
			}
		}		
	}		

	private void addAssociations(NodeList list)
	{		
		for (int count = 0; count < list.getLength(); count++) 
		{
			Element rElement = (Element) list.item(count);

			XMIAssociation rel = new XMIAssociation(rElement);
			
			rel.setParentName(cHash.get(rel.getEndID()).getName());
			
			XMIClass c = cHash.get(rel.getStartID());
			c.addAssoc(rel);
		}
	}

	private void addInheritance(NodeList list)
	{	
		
		for (int count = 0; count < list.getLength(); count++) 
		{	
			Element iElement = (Element) list.item(count);
			
			String cID = iElement.getAttribute("child");

			XMIClass childClass = cHash.get(cID);
			childClass.setInheritance(true);
			
			String pID = iElement.getAttribute("parent");
			XMIClass parentClass = cHash.get(pID);
			
			childClass.setParent(parentClass.getName());
		}
	}


	@Override
	public String help()
	{
		return "uml2vdm - generate VDM++ or VDM-RT from PlantUML";
	}
}
