package plugins.UML2VDM;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;


public class XMIClassOld {

    private String name;
    private String ID;
    private String visibility;
    private String parent;
    private Boolean isInherited;

    private List<Element> typeList = new ArrayList<Element>();   
    private List<Element> valueList = new ArrayList<Element>();   
    private List<Element> iVariableList = new ArrayList<Element>();   
    private List<Element> operationList = new ArrayList<Element>();   
    private List<Element> functionList = new ArrayList<Element>();   
    
    private List<XMIAssociation> assocList = new ArrayList<XMIAssociation>();  
    

    public XMIClassOld(Element cElement){
        this.name = cElement.getAttribute("name");
        this.ID = cElement.getAttribute("xmi.id");
        setInheritance(false); 

        NodeList attributeList = cElement.getElementsByTagName("UML:Attribute");
        if(! (attributeList.getLength() == 0))
            createAttributes(attributeList);
    
        NodeList operationList = cElement.getElementsByTagName("UML:Operation");
        if(! (operationList.getLength() == 0))
            createOperations(operationList);
        
    }

    public void addAssoc(XMIAssociation assoc)
    {
        assocList.add(assoc);
    }

    private void createOperations(NodeList list)
    {
        for (int count = 0; count < list.getLength(); count++) {
					
			Element oElement  = (Element) list.item(count);
			
			if (!oElement.getAttribute("name").contains("«function»"))
                operationList.add(oElement);

            if (oElement.getAttribute("name").contains("«function»"))
                functionList.add(oElement);
		}
    }

    
    private void createAttributes(NodeList list)
    {
        for (int count = 0; count < list.getLength(); count++) {
                        
            Element aElement  = (Element) list.item(count);
            if (aElement.getAttribute("name").contains("«value»"))		
                valueList.add(aElement);

            if (aElement.getAttribute("name").contains("«type»"))
                typeList.add(aElement);

            if (! (aElement.getAttribute("name").contains("«type»") || 
                    aElement.getAttribute("name").contains("«value»")))
                    
                iVariableList.add(aElement);            
        }
    }

    public void setParent(String parentName)
    {
        this.parent = parentName;
    }

    public void setInheritance(Boolean bool)
    {
        this.isInherited = bool;
    }

    public Boolean getInheritance()
    {
        return isInherited;
    }

    public String getName()
    {
        return name;
    }

    public String getID()
    {
        return ID;
    }

    public String getVisibility()
    {
        return visibility;
    }

    public String getParent()
    {
        return parent;
    }

    public List<Element> getIVariables()
    {
        return iVariableList;
    }

    
    public List<Element> getTypes()
    {
        return typeList;
    }
    
    public List<Element> getValues()
    {
        return valueList;
    }

    
    public List<Element> getOperations()
    {
        return operationList;
    }

    
    public List<Element> getFunctions()
    {
        return functionList;
    }

    
    public List<XMIAssociation> getAssociations()
    {
        return assocList;
    }


}

