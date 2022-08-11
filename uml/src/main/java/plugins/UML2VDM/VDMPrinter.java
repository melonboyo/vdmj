package plugins.UML2VDM;

import java.util.*;
import org.w3c.dom.Element;
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
                
               vdmFile.createNewFile();
                    
                FileWriter writer = new FileWriter(vdmFile.getAbsolutePath());
                
                printClass(writer, c);
                printValues(writer, c);
                printTypes(writer, c);
                printIVariables(writer, c);
                printOperations(writer, c);
                printFunctions(writer, c);

                writer.write("\n\nend " + c.getName());
                writer.close();

                
            }

            System.out.println("generated vdm files");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printClass(FileWriter writer, XMIClass c)
    {
        try {
            if(c.getInheritance())
                writer.write("class " + c.getName() + " is subclass of " + c.getParent() + "\n\n");
            
            else 
                writer.write("class " + c.getName() + "\n\n");
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }     
    
    private void printValues(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getValues().isEmpty())
            {
                List<XMIAttribute> valueList = c.getValues();

                writer.write("values\n");

                for (int count = 0; count < valueList.size(); count++) 
                {
                    XMIAttribute val = valueList.get(count);
                    writer.write(val.getVisibility() + val.getName() + " = ;\n");
                }

                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 

    private void printTypes(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getTypes().isEmpty())
            {
                List<XMIAttribute> typeList = c.getTypes();

                writer.write("types\n");

                for (int count = 0; count < typeList.size(); count++) 
                {
                    XMIAttribute type = typeList.get(count);
                    
                    String segments[] = type.getName().split(":");

                    writer.write(type.getVisibility() + segments[0] + "=" + segments[segments.length - 1] + ";\n");
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    private void printIVariables(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getIVariables().isEmpty())
            {
                List<XMIAttribute> varList = c.getIVariables();

                writer.write("instance variables\n");

                for (int count = 0; count < varList.size(); count++) 
                {
                    XMIAttribute var = varList.get(count);
                    
                    if (var.getIsAssociative())
                        writer.write(var.getVisibility() + var.getName() + " : " + var.getRelName() + ";\n");

                    else
                        writer.write(var.getVisibility() + var.getName() + ";\n");
                        
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 

    private void printOperations(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getOperations().isEmpty())
            {
                List<Element> opList = c.getOperations();

                writer.write("operations\n");

                for (int count = 0; count < opList.size(); count++) 
                {
                    String op = opList.get(count).getAttribute("name");

                    writer.write(visibility(opList.get(count)) + op + ";\n");
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 

    private void printFunctions(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getFunctions().isEmpty())
            {
                List<Element> funList = c.getFunctions();

                writer.write("functions\n");

                for (int count = 0; count < funList.size(); count++) 
                {
                    String fun = remove(funList.get(count).getAttribute("name"), "«function»");
                    
                    writer.write(visibility(funList.get(count)) + fun + ";\n");
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 

    private String visibility(Element element)
	{
		if (element.getAttribute("visibility").contains("private")) 
			return "private ";
	
		if (element.getAttribute("visibility").contains("public"))
            return "public ";

        else return "";
	}

    private String remove(String s, String r)
	{
        return s.replace(r, "");
	}



}

