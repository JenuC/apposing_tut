import qupath.lib.regions.RegionRequest
import qupath.imagej.tools.IJTools

import org.apposed.appose.Appose
import org.apposed.appose.Environment
import org.apposed.appose.Service
import org.apposed.appose.NDArray
import static org.apposed.appose.NDArray.Shape.Order.F_ORDER

import java.nio.FloatBuffer

// --- 1. Get current image + selected ROI ---
def imageData   = getCurrentImageData()
def server      = imageData.getServer()
def pathObject  = getSelectedObject()

if (pathObject == null || pathObject.getROI() == null) {
    print 'No ROI selected!'
    return
}

def roi = pathObject.getROI()
def bx  = (int) roi.getBoundsX()
def by  = (int) roi.getBoundsY()
def bw  = (int) roi.getBoundsWidth()
def bh  = (int) roi.getBoundsHeight()

println "Selected ROI bbox: x=${bx}, y=${by}, w=${bw}, h=${bh}"

// --- 2. Read the ROI region as a BufferedImage ---
def request = RegionRequest.createInstance(
        server.getPath(),
        1.0,   // downsample
        bx, by, bw, bh
)
def bufferedImage = server.readBufferedImage(request)

// --- 3. Convert BufferedImage -> Appose NDArray (FLOAT32, shape [h, w]) ---
final NDArray.DType dType = NDArray.DType.FLOAT32
final NDArray.Shape shape = new NDArray.Shape(F_ORDER, bh, bw)
final NDArray ndArray     = new NDArray(dType, shape)

final FloatBuffer buf = ndArray.buffer().asFloatBuffer()
final long len        = ndArray.shape().numElements()

int w = bufferedImage.getWidth()
int h = bufferedImage.getHeight()

int idx = 0
for (int y = 0; y < h; y++) {
    for (int x = 0; x < w; x++) {
        int rgb = bufferedImage.getRGB(x, y)
        int r   = (rgb >> 16) & 0xFF
        int g   = (rgb >> 8)  & 0xFF
        int b   = (rgb      ) & 0xFF
        float gray = (r + g + b) / 3.0f
        if (idx < len)
            buf.put(idx++, gray)
    }
}

println "Filled NDArray with ${idx} pixels (shape ${bh}x${bw})"

// --- 4. Send NDArray to Python via Appose ---
final Environment cellpose3Env = Appose.wrap("/home/lociuser/.local/share/appose/cellpose3-uv/")
Service service = cellpose3Env.python()

String pyScript = """import numpy as np
arr = img.ndarray()
task.outputs['shape'] = str(arr.shape)
task.outputs['dtype'] = str(arr.dtype)
task.outputs['summary'] = f"min={arr.min()}, max={arr.max()}, mean={arr.mean()}"
"""

def inputs = ['img': ndArray] as Map<String, Object>
Service.Task task = service.task(pyScript, inputs)
task.waitFor()

println "Python shape   : " + task.outputs.get("shape")
println "Python dtype   : " + task.outputs.get("dtype")
println "Python summary : " + task.outputs.get("summary")

ndArray.close()
service.close()
