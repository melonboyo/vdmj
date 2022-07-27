package plugins;

import java.io.BufferedReader;
import java.io.File;
import java.nio.Buffer;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;

public class UMLAnalyser {

    public BufferedReader br;

    public UMLAnalyser(String path) {
        File file = new File(path);
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public void Analyse() throws IOException{
        System.out.println("--------------------------------\n" +
                           "---BEGIN ANALYSIS OF PLANTUML---\n" +
                           "--------------------------------\n");

        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("elements")) {
                break;
            }
        }

        while ((line = br.readLine()) != null) {
            if (line.contains("{")) {
                caseClass();
            }
        }
        
        System.out.println("-----------------------\n" +
                           "---ANALYSIS COMPLETE---\n" +
                           "-----------------------");
    }

    public void caseClass() throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            
        }
    }
}
