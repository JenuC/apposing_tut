import qupath.lib.regions.RegionRequest

def imageData = getCurrentImageData()
def server = imageData.getServer()

// Get full image at downsample factor 1 (full resolution)
def request = RegionRequest.createInstance(server, 1.0)
def bufferedImage = server.readBufferedImage(request)

println "BufferedImage dimensions: ${bufferedImage.getWidth()} x ${bufferedImage.getHeight()}"
println "Type: ${bufferedImage.getType()}"