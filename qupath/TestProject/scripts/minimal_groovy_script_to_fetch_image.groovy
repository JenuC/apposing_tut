

def imageData = getCurrentImageData()

// Check if an image is open
if (imageData == null) {
    print "No image is currently open"
    return
}

// Get the image server (contains the actual image)
def server = imageData.getServer()

// Print basic image information
println "Image: " + server.getMetadata().getName()
println "Width: " + server.getWidth() + " pixels"
println "Height: " + server.getHeight() + " pixels"
println "Channels: " + server.nChannels()
println "Pixel type: " + server.getPixelType()


// Request full image at full resolution
def request = RegionRequest.createInstance(server, 1.0)
def img = server.readBufferedImage(request)
println "BufferedImage: " + img
println "Image type: " + img.getType()


// Convert to ImagePlus
import ij.ImagePlus
import qupath.imagej.tools.IJTools
def imp = IJTools.convertToImagePlus(server, RegionRequest.createInstance(server))
println "ImagePlus: " + imp
println "Title: " + imp.getTitle()



