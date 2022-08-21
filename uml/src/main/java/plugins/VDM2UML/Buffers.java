package plugins.VDM2UML;

import java.util.ArrayList;

import com.fujitsu.vdmj.tc.definitions.TCClassList;
import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class Buffers 
{
    public StringBuilder defs;
    public StringBuilder asocs;
    public ArrayList<String> classes;
    public static PublicClassEnvironment env;


    public Buffers(TCClassList classList)
    {
        defs = new StringBuilder();
        asocs = new StringBuilder();
        classes = new ArrayList<String>();
        env = new PublicClassEnvironment(classList);
    }

   /*  public StringBuilder getDef()
    {
        return defs;
    }
    
    public StringBuilder getAsoc()
    {
        return asocs;
    } */
}