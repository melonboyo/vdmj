package plugins.UML2VDM;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMIAttribute {
    
    public enum AttTypes {type, value, var}
    public enum MulTypes {set, seq, map}

    private String name;
    private String relName;
    private String startID;
    private String endID;
    private AttTypes attType;
    private MulTypes mulType;

    private String visibility;
    private Boolean isAssociative;


    public XMIAttribute(Element aElement)
    {     
        this.isAssociative = false;

        this.name = (aElement.getAttribute("name"));

        setAttType(aElement);

        this.visibility = visibility(aElement);

        if(aElement.getAttribute("xmi.id").contains("ass"))
        {
            this.isAssociative = true;
            initializeAssoc(aElement);
        }

  
    }

    private void initializeAssoc(Element rElement)
    {
        NodeList relAttList = rElement.getElementsByTagName("UML:AssociationEnd");
        
        Element relStart  = (Element) relAttList.item(0);
        
        this.startID = relStart.getAttribute("type");
       
        Element relEnd  = (Element) relAttList.item(1);
        this.endID = relEnd.getAttribute("type");

        String mult = relEnd.getAttribute("name");
        
        if(mult.equals("*"))
            this.mulType = MulTypes.set;    

        if(mult.equals("(*)"))
        this.mulType = MulTypes.seq;    

    }


    private void setAttType(Element aElement)
    {
        if (aElement.getAttribute("name").contains("«value»"))
        {
            this.attType = AttTypes.value;
            this.name = remove(this.name, "«value»");      
        }		

        if (aElement.getAttribute("name").contains("«type»"))
        {
            this.attType = AttTypes.type;
            this.name = remove(this.name, " «type»");      
        }
        
        if (! (aElement.getAttribute("name").contains("«type»") || 
                aElement.getAttribute("name").contains("«value»")))
        {
            this.attType = AttTypes.var;
        }
    }

   
    private String remove(String s, String r)
	{
        return s.replace(r, "");
	}
    
    private String visibility(Element element)
	{
		if (element.getAttribute("visibility").contains("private")) 
			return "private ";
	
		if (element.getAttribute("visibility").contains("public"))
            return "public ";

        else return "";
	}

    public void setRelName(String parent)
    {
        if (parent.equals(this.name))
        this.relName = "undef";

        else
            this.relName = parent;
    }

    public void setVisibility(String newVis)
    {
        this.visibility = newVis;
    }

    public String getStartID()
    {
        return startID;
    }

    public String getEndID()
    {
        return endID;
    }

    public String getVisibility()
    {
        return visibility;
    }

    public Boolean getIsAssociative()
    {
        return isAssociative;
    }

    public String getName()
    {
        return name;
    }

    public String getRelName()
    {
        return relName;
    }  

    public AttTypes getAttType()
    {
        return attType;
    }

    public String getMulType()
    {
        if (this.mulType == MulTypes.set)
            return "set of ";
        
        if (this.mulType == MulTypes.seq)
            return "seq of ";
        
            
        else
            return "undef ";
    } 
    
}
    

