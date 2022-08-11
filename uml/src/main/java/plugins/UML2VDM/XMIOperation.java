package plugins.UML2VDM;


import org.w3c.dom.Element;

public class XMIOperation {
    
    public enum OpTypes {operation, function}
    
    private String signature;
    private OpTypes opType;

    private String visibility;

    public XMIOperation(Element aElement)
    {     
        String xmiName = (aElement.getAttribute("name"));

        if (aElement.getAttribute("name").contains("«function»"))
        {
            this.opType = OpTypes.function;
            xmiName = remove(xmiName, "«function»");      
        }	
        else
            this.opType = OpTypes.operation;
        
        
        String seg1[] = xmiName.split("\\(");
        String OpName = seg1[0];

        String seg2[] = (seg1[seg1.length - 1]).split("\\)");
        
        String argLine = seg2[0];

        String args[] = argLine.split(",");

        String vdmArgLine = " " + args[0];

        
        for(int n = 1 ; n < args.length ; n++)
        {
            vdmArgLine = vdmArgLine + " *" + args[n]; 
        }

        String seg3[] =  xmiName.split(":");
        String opOut = seg3[seg3.length - 1];

        if(this.opType == OpTypes.operation)
            this.signature = OpName + ":" + vdmArgLine + " ==>" + opOut; 

        else
            this.signature = OpName + ":" + vdmArgLine + " ->" + opOut;
        
        this.visibility = visibility(aElement);


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

    public String getVisibility()
    {
        return visibility;
    }

    public String getSignature()
    {
        return signature;
    }

    public OpTypes getOpType()
    {
        return opType;
    }


    
}
    

