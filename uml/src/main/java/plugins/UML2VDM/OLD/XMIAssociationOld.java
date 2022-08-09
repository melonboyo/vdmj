package plugins.UML2VDM.OLD;

/* import org.w3c.dom.Document;
import org.w3c.dom.Node; */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMIAssociationOld 
{
    private String name;
    private String endID;
    private String startID;
    private String type;

    public XMIAssociationOld(Element rElement){
            
        this.setName(rElement.getAttribute("name"));

        NodeList relAttList = rElement.getElementsByTagName("UML:AssociationEnd");
        
        Element relStart  = (Element) relAttList.item(0);
        this.setStartID(relStart.getAttribute("type"));	
        
        Element relEnd  = (Element) relAttList.item(1);
        this.setEndID(relEnd.getAttribute("type"));

    }

    private void setStartID(String ID)
    {
        this.startID = ID;
    }

    private void setEndID(String ID)
    {
        this.endID = ID;
    }

    private void setName(String newname)
    {
        this.name = newname;

        if(name.contains("«type»"))
            setType("type");
        if(name.contains("«value»"))
            setType("value");
        else setType("variable");
    }

    private void setType(String newtype)
    {
        this.type = newtype;
    }

    public String getStartID()
    {
        return startID;
    }

    public String getEndID()
    {
        return endID;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }
}