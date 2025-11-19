
import org.apposed.appose.Appose;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.apposed.appose.NDArray.Shape.Order.F_ORDER;


final NDArray.DType dType = NDArray.DType.FLOAT32;
final NDArray.Shape shape = new NDArray.Shape(F_ORDER, 4, 3, 2);
final NDArray ndArray = new NDArray(dType, shape);

// Fill with values 0..23 in flat iteration order.
final FloatBuffer buf = ndArray.buffer().asFloatBuffer();
final long len = ndArray.shape().numElements();
for ( int i = 0; i < len; ++i ) {
    buf.put(i, i);
}

System.out.println("ndArray.shm().size() = " + ndArray.shm().size());
System.out.println("ndArray.shm().name() = " + ndArray.shm().name());