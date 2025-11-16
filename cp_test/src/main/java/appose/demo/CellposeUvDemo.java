package appose.demo;

import org.apposed.appose.Appose;
import org.apposed.appose.Environment;
import org.apposed.appose.Service;

public class CellposeUvDemo {

    public static void main(String[] args) throws Exception {
        System.out.println(">>> Starting CellposeUvDemo");

        System.out.println(">>> Before building cellpose3 env");
        Environment cellpose3Env = Appose.uv()
                .include("cellpose==3")
                .name("cellpose3-uv")
                .build();
        System.out.println(">>> Finished building cellpose3 env");

        System.out.println(">>> Before building cellpose4 env");
        Environment cellpose4Env = Appose.uv()
                .include("cellpose==4.0.7")
                .name("cellpose4-uv")
                .build();
        System.out.println(">>> Finished building cellpose4 env");

        String checkScript = """
            import sys
            #import cellpose
            #print(cellpose.version)
            #print(task)
            #task.outputs["cellpose_version"] = cellpose.version
            task.outputs["python_exe"] = sys.executable
            """;

        try (
            Service cellpose3 = cellpose3Env.python();
            Service cellpose4 = cellpose4Env.python()
        ) {
            System.out.println(">>> Running check in cellpose3 env");
            Service.Task t3 = cellpose3.task(checkScript);
            
            t3.waitFor();
            System.out.println("    status3 = " + t3.status);
            System.out.println("    version3 = " + t3.outputs.get("cellpose_version"));
            System.out.println("    python3  = " + t3.outputs.get("python_exe"));

            System.out.println(">>> Running check in cellpose4 env");
            Service.Task t4 = cellpose4.task(checkScript);
            t4.waitFor();
            System.out.println("    status4 = " + t4.status);
            System.out.println("    version4 = " + t4.outputs.get("cellpose_version"));
            System.out.println("    python4  = " + t4.outputs.get("python_exe"));
        }

        System.out.println(">>> Done.");
    }
}
