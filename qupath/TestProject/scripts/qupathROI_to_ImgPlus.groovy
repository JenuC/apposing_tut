import qupath.imagej.tools.IJTools
import qupath.lib.regions.RegionRequest

def imageData = getCurrentImageData()
def server = imageData.getServer()

// Method 1: Create request with server path and downsample
def request = RegionRequest.createInstance(
    server.getPath(),  // Server path as String
    1.0,               // Downsample factor
    0,                 // x
    0,                 // y
    1000,              // width
    1000               // height
)

def pathImage = IJTools.convertToImagePlus(server, request)
def imp = pathImage.getImage()
println "Got region: ${imp.getWidth()} x ${imp.getHeight()}"