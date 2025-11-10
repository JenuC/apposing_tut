import org.apposed.appose.Service.Task;
import org.apposed.appose.builder.MambaBuilder;
import org.apposed.appose.builder.PixiBuilder;
import org.apposed.appose.builder.UvBuilder;
import org.apposed.appose.util.FilePaths;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apposed.appose.NDArray.Shape.Order.F_ORDER;
import static org.junit.jupiter.api.Assertions.*;

/** End-to-end tests for the Appose builder subsystem and implementations. */
public class Demo {

    public static void main(String... args) throws Exception {
        Environment pandas = Appose.uv().include("pandas").name("demo-pandas").build();
        Environment seaborn = Appose.uv().include("seaborn").name("demo-seaborn").build();
        try (
            Service pandaWorker = pandas.python();
            Service seabornWorker = seaborn.python()
        ) {
            //pandaWorker.debug(s -> System.out.println("SERVICE: " + s));
            Map<String, Object> inputs = new HashMap<>();

            // create a FLOAT32 NDArray with shape (4,3,2) in F_ORDER
            // respectively (2,3,4) in C_ORDER
            final NDArray.DType dType = NDArray.DType.FLOAT32;
            final NDArray.Shape shape = new NDArray.Shape(F_ORDER, 4, 3, 2);
            final NDArray ndArray = new NDArray(dType, shape);

            {
                // fill with values 0..23 in flat iteration order
                final FloatBuffer buf = ndArray.buffer().asFloatBuffer();
                final long len = ndArray.shape().numElements();
                for (int i = 0; i < len; ++i) {
                    buf.put(i, i);
                }
            }

            inputs.put("ndarr", ndArray);
            inputs.put("a", 55);
            inputs.put("b", 66);
            Task task = pandaWorker.task(
                "import pandas\n" +
                    "arr = ndarr.ndarray()\n" +
                    "arr[0,1,2] = a + b\n" +
                    "import sys\n" +
                    "print(task, file=sys.stderr)\n" +
                    "task.update('hello')\n" +
                    "task.update(str(type(arr)))\n" +
                    "task.outputs['value'] = float(arr[0,1,2])\n" +
                    "task.outputs['arr_type'] = str(type(arr))\n",
                    inputs
            );
            task.listen(e -> {
                System.out.println("WORKER: " + e.message);
            });
            task.waitFor();
            if (task.status != Service.TaskStatus.COMPLETE) {
                System.out.println("SOMETHING WENT WRONG OH NO:\n" + task.error);
            }
            System.out.println("value = " + task.outputs.get("value"));
            System.out.println("arr_type = " + task.outputs.get("arr_type"));

            {
                final FloatBuffer buf = ndArray.buffer().asFloatBuffer();
                final long len = ndArray.shape().numElements();
                for (int i = 0; i < len; ++i) {
                    System.out.println(buf.get(i));
                }
            }

            inputs.clear();
            inputs.put("ndarr", ndArray);
            Task seaTask = seabornWorker.task(
                "float(ndarr.ndarray()[0,1,2])",
                inputs
            );
            seaTask.waitFor();
            if (seaTask.status != Service.TaskStatus.COMPLETE) throw new RuntimeException(seaTask.error);

            System.out.println("FROM SEABORN: " + seaTask.result());

            //ndArray.close();
        }
    }
}
