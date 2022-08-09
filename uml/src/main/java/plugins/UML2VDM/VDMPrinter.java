package plugins;
import java.util.*;

public class VDMPrinter {
    
    private List<XMIClass> classList;

    public VDMPrinter(List<XMIClass> classList)
    {
        this.classList = classList;
    }

    public void printVDM()
    {
        for (int n = 0; n < classList.size(); n++) 
		{	
            XMIClass c = classList.get(n);
            
            if (c.getInheritance() == true)  
		}
        
     
    }
}
