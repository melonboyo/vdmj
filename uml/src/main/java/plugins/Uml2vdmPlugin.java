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


/* import com.fujitsu.vdmj.runtime.ClassInterpreter;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList; */ 

/* import java.io.BufferedReader;
import java.io.FileReader;
import javax.print.Doc;
import java.io.File; */



public class Uml2vdmPlugin extends CommandPlugin {
    
    private Hashtable<String, Element> cHash = new Hashtable<String, Element>(); 
	private List<XMIAssociation> asList =new ArrayList<XMIAssociation>();  

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


		//Mapping class xmi.id to xml element 
		for (int count = 0; count < cList.getLength(); count++) 
		{
			Element cElement = (Element) cList.item(count);

			if (! (cElement.getAttribute("xmi.id") == null || (cElement == null)))
			{
				cHash.put(cElement.getAttribute("xmi.id"), cElement);
			}
		}

		createAssociations(rList);

		for (int temp = 0; temp < cList.getLength(); temp++) 
		{
			Node nNode = cList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
					
				NodeList attributeList = eElement.getElementsByTagName("UML:Attribute");
				NodeList operationList = eElement.getElementsByTagName("UML:Operation");		
				
				classes(eElement, isInherited(eElement, gList));
				values(attributeList);
				types(attributeList);
				instanceVariables(attributeList, eElement.getAttribute("xmi.id"));
				
				functions(operationList);
				operations(operationList);
				System.out.println("\n");
			}
		}	
		return true;
	}

	private void createAssociations(NodeList list)
	{		
		for (int count = 0; count < list.getLength(); count++) 
		{
			Element rElement = (Element) list.item(count);

			XMIAssociation rel = new XMIAssociation(rElement);
			
			asList.add(rel);
		}
	}

	private void instanceVariables(NodeList list, String ID){

		System.out.println("instance variables \n" );

		for (int n = 0; n < asList.size(); n++) 
		{	
			String startID = asList.get(n).getStartID();
			String relType = asList.get(n).getType();
			
			if (startID.equals(ID) && relType.equals("variable"))
			{
				String endName = cHash.get(asList.get(n).getEndID()).getAttribute("name");
				System.out.println(asList.get(n).getName() + " : " + endName + ";");
			}
		}
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element aElement  = (Element) list.item(count);
			
			if (! (aElement.getAttribute("name").contains("«type»") || aElement.getAttribute("name").contains("«value»")))
			{		
				System.out.println(visibility(aElement) + aElement.getAttribute("name"));
			}
		}
	}

	//returns the xmi.id of the parent class. return empty string if class is not inherited.
	private String isInherited(Element el, NodeList iList)
	{
		String childId = el.getAttribute("xmi.id");

		for (int count = 0; count < iList.getLength(); count++) {
			Element iElement = (Element) iList.item(count);

			if(childId.equals(iElement.getAttribute("child")))
			{
				Element parent = cHash.get(iElement.getAttribute("parent"));
				return parent.getAttribute("name");
			}	
		}
		return "";
	}
	
	private void classes(Element el, String relation)
	{
		if(relation.isEmpty())
			System.out.println("Class " + el.getAttribute("name"));
		
		else
			System.out.println("Class " + el.getAttribute("name") + 
			" is subclass of " + relation);
	}



	private void types(NodeList list){

		System.out.println("types\n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element aElement  = (Element) list.item(count);
			
			if (aElement.getAttribute("name").contains("«type»"))
			{		
				System.out.println(visibility(aElement) + aElement.getAttribute("name"));
			}
		} 
	}

	private void values(NodeList list){

		System.out.println("values\n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element aElement  = (Element) list.item(count);
			
			if (aElement.getAttribute("name").contains("«value»"))
			{		
				System.out.println(visibility(aElement) + aElement.getAttribute("name"));
			}
		} 
	}

	private void functions(NodeList list)
	{
		System.out.println("functions\n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element oElement  = (Element) list.item(count);
			
			if (oElement.getAttribute("name").contains("«function»"))
			{
				System.out.println(visibility(oElement) + oElement.getAttribute("name"));
			}
		}
	}

	private void operations(NodeList list)
	{
		System.out.println("operations\n" );
	
		for (int count = 0; count < list.getLength(); count++) {
					
			Element oElement  = (Element) list.item(count);
			
			if (!oElement.getAttribute("name").contains("«function»"))
			{
				System.out.println(visibility(oElement) + oElement.getAttribute("name"));
			}
		}
	}

	private String visibility(Element element)
	{
		if (element.getAttribute("visibility").contains("private")) 
			return "private ";
	
		else return "public ";
	}

	@Override
	public String help()
	{
		return "uml2vdm - generate VDM++ or VDM-RT from PlantUML";
	}
}
