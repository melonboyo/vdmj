package plugins;

import java.util.*;
import java.io.File; 
import java.io.IOException;
import java.io.FileWriter;  

public class VDMPrinter {
    
    private List<XMIClass> classList;

    public VDMPrinter(List<XMIClass> classList)
    {
        this.classList = classList;
    }

    public void printVDM(String path)
    {   
        try {
            new File(path + "generated").mkdirs();

            for (int n = 0; n < classList.size(); n++) 
            {	
                XMIClass c = classList.get(n);
                
                File vdmFile = new File(path + "generated/" + c.getName() + ".vdmpp");
                
                if (!vdmFile.createNewFile());
                    //System.out.println("File already exists.");
                    
                FileWriter writer = new FileWriter(vdmFile.getAbsolutePath());
                
                writeFiles(writer, c);
                
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFiles(FileWriter writer, XMIClass c)
    {
        try {
            writer.write("class " + c.getName());



        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
                
}