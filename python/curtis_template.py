from pathlib import Path

import appose
from appose.service import TaskStatus

env = appose.base(Path('/Users/curtis/.local/share/appose/demo-pandas')).build()

with env.python() as service:
    task = service.task("import pandas\n" +
        "'good'")
    task.wait_for()
    assert task.status == TaskStatus.COMPLETE
    result = task.outputs['result']
    print(result)

    with appose.NDArray(dtype='float32', shape=[2,3,4]) as ndarray:
        print(type(ndarray))
        narr = ndarray.ndarray()
        print(type(narr))
        narr.fill(123)
        # Also works: narr[:,:,:] = 123

        inputs = {
            "ndarr": ndarray,
            "a": 55,
            "b": 66,
        }
        task = service.task(
            """
            task.update('starting')
            import pandas
            arr = ndarr.ndarray()
            arr[0,1,2] = a + b
            import sys
            print(task, file=sys.stderr)
            task.update('hello')
            task.update(str(type(arr)))
            task.outputs['value'] = float(arr[0,1,2])
            task.outputs['arr_type'] = str(type(arr))
            """,
            inputs
        )
        task.listen(lambda e: print("WORKER: " + e.message))
        task.wait_for()
        if task.status != TaskStatus.COMPLETE:
            print(f"SOMETHING WENT WRONG OH NO:\n{task.error}")

        print(f"value = {task.outputs.get("value")}")
        print(f"arr_type = {task.outputs.get("arr_type")}")

        #print(f"FINAL VALUE: {ndarray.ndarray()[0,1,2]}")
