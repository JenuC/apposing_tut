import ij.ImagePlus
import qupath.imagej.tools.IJTools
import qupath.lib.regions.RegionRequest

// Get current image
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Convert to PathImagePlus (QuPath's wrapper)
def pathImage = IJTools.convertToImagePlus(server, RegionRequest.createInstance(server))
println "PathImagePlus: " + pathImage

// Get the actual ImagePlus from PathImagePlus
def imp = pathImage.getImage()
println "ImagePlus: " + imp
println "Title: " + imp.getTitle()
println "Width: " + imp.getWidth()
println "Height: " + imp.getHeight()
println "Channels: " + imp.getNChannels()