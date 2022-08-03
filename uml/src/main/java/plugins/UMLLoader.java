package plugins;

import java.io.BufferedReader;
import java.io.File;
import java.nio.Buffer;
import java.util.Iterator;
import java.util.List;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.lang.StringBuilder;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class UMLLoader {

    public BufferedReader br;
    public File file;

    public UMLLoader(String path) {
        file = new File(path);
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public void Load() throws Exception {
        System.out.println("--------------------------------\n" +
                           "-----BEGIN LOADING PLANTUML-----\n" +
                           "--------------------------------\n");
        
        try 
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("UML:Class");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) 
            {
                Node nNode = nList.item(temp);
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Class : " 
                        + eElement.getAttribute("name"));

                
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("--------------------------\n" +
                           "-----LOADING COMPLETE-----\n" +
                           "--------------------------");
    }
    
    public void caseClass(int classId) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("name")) {
                String name = line.substring(17, line.length() - 2);
                System.out.println("Class #" + classId + ": " + name + "\n");
            }
        }
    }
}
