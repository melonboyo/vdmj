package plugins.UML2VDM;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import plugins.UML2VDM.OLD.XMIAssociation;
import plugins.UML2VDM.XMIAttribute.AttTypes;

import java.util.*;


public class XMIClass {

    private String name;
    private String ID;
    private String visibility;
    private String parent;
    private Boolean isInherited;

    private List<XMIAttribute> typeList = new ArrayList<XMIAttribute>();   
    private List<XMIAttribute> valueList = new ArrayList<XMIAttribute>();   
    private List<XMIAttribute> varList = new ArrayList<XMIAttribute>();   
    private List<Element> operationList = new ArrayList<Element>();   
    private List<Element> functionList = new ArrayList<Element>();   
    
    private List<XMIAssociation> assocList = new ArrayList<XMIAssociation>();  
    

    public XMIClass(Element cElement){
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

            XMIAttribute att = new XMIAttribute(aElement);

            if (att.attType == AttTypes.value)		                
                valueList.add(att);

            if (att.attType == AttTypes.type)		                
                typeList.add(att);

            if (att.attType == AttTypes.var)		                
                varList.add(att);          
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

    public List<XMIAttribute> getIVariables()
    {
        return varList;
    }

    
    public List<XMIAttribute> getTypes()
    {
        return typeList;
    }
    
    public List<XMIAttribute> getValues()
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

