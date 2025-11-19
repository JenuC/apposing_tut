import org.apposed.appose.Appose;
import org.apposed.appose.Environment;
import org.apposed.appose.Service;
import org.apposed.appose.NDArray;
import static org.apposed.appose.NDArray.Shape.Order.F_ORDER;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

//Environment cellpose3Env = Appose.uv()
//                .include("cellpose==3")
//                .name("cellpose3-uv")
//                .build();

final Environment cellpose3Env = Appose.wrap("/home/lociuser/.local/share/appose/cellpose3-uv/");
Service service = cellpose3Env.python() 


String checkScript = """import sys
import numpy
import cellpose
task.outputs['python_exe'] = sys.executable
task.outputs['cellpose_version'] = cellpose.version;
task.outputs['result'] = str(img.ndarray())
""";

final NDArray.DType dType = NDArray.DType.FLOAT32;
final NDArray.Shape shape = new NDArray.Shape(F_ORDER, 4, 3, 2);
final NDArray ndArray = new NDArray(dType, shape);

final FloatBuffer buf = ndArray.buffer().asFloatBuffer();
final long len = ndArray.shape().numElements();
for ( int i = 0; i < len; ++i ) {
    buf.put(i, i);
}

final Map< String, Object > inputs = new HashMap<>();
inputs.put( "img", ndArray);
Service.Task task = service.task(checkScript, inputs );
task.waitFor();
final String result = ( String ) task.outputs.get("result");
println("    nd.array  = " + result);
println("    python4   = " + task.outputs.get("python_exe"));
println("    cellpose  = " + task.outputs.get("cellpose_version"));
ndArray.close();


