import org.apposed.appose.Appose;
import org.apposed.appose.Environment;
import org.apposed.appose.Service;
//@Grab('org.codehaus.groovy:groovy-json:4.0.26')
//import groovy.json.*

Environment cellpose3Env = Appose.uv()
                .include("cellpose==3")
                .name("cellpose3-uv")
                .build();
                
String checkScript = """import sys
import numpy
#import cellpose
#task.outputs['cellpose_version'] = cellpose.version;
task.outputs['python_exe'] = sys.executable
""";
           
println("starting service");
Service cellpose3 = cellpose3Env.python();
Service.Task t3 = cellpose3.task(checkScript);
println("waiting");
t3.waitFor();
println("    python4  = " + t3.outputs.get("python_exe"));
println("    cellpose  = " + t3.outputs.get("cellpose_version"));
println(">>> Done.");