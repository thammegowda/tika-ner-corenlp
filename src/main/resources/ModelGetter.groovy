/*
 * Tika NER using CoreNLP
 * Copyright (C)  Thamme Gowda N <tgowdan@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.apache.commons.io.IOUtils

import java.util.jar.JarFile
import java.util.zip.ZipEntry

/**
 * Copies input stream to output stream, additionally printing the progress.
 * NOTE: this is optimized for large content
 * @param inStr
 * @param outStr
 * @param totalLength the total length of the content (used to calculate progress)
 * @return
 */
def copyWithProgress(InputStream inStr, OutputStream outStr, long totalLength){
    PROGRESS_DELAY = 1000
    byte[] buffer = new byte[1024 * 4]
    long count = 0
    int len
    long tt = System.currentTimeMillis()
    while ((len = inStr.read(buffer)) > 0) {
        outStr.write(buffer, 0, len)
        count += len
        if (System.currentTimeMillis() - tt > PROGRESS_DELAY) {
            println "${count * 100.0/totalLength}% : $count bytes of $totalLength"
            tt = System.currentTimeMillis()
        }
    }
    println "Copy complete. "
    IOUtils.closeQuietly(inStr)
    IOUtils.closeQuietly(outStr)
}

/**
 * Downloads file
 * @param urlStr url of file
 * @param file path to store file
 * @return
 */
def downloadFile(def urlStr, def file) {
    println "GET : $urlStr"
    urlConn = new URL(urlStr).openConnection()
    contentLength = urlConn.getContentLengthLong()
    println "Content Length : $contentLength"

    inStream = urlConn.getInputStream()
    outStream = new FileOutputStream(file)
    //IOUtils.copyLarge(inStream, outStream)
    copyWithProgress(inStream, outStream, contentLength)
    IOUtils.closeQuietly(outStream)
    IOUtils.closeQuietly(inStream)
    println "Download Complete.."
}

/**
 * Copy resource inside a zip to project's resources directory
 * @param jarFile the jar file having resources
 * @param resources the list of resources to be picked from jar and put
 * inside resources directory
 */
def copyResources(File jarFile, List resources) {
    JarFile jf = new JarFile(jarFile)
    for (String res : resources) {
        println "Copying $res"
        destiny = new File("src/main/resources/$res")
        destiny.getParentFile().mkdirs()
        ZipEntry entry = jf.getEntry(res)
        InputStream inStr = jf.getInputStream(entry)
        OutputStream outStr = new FileOutputStream(destiny)
        IOUtils.copy(inStr, outStr)
        IOUtils.closeQuietly(outStr)
        IOUtils.closeQuietly(inStr)
    }
}

//Checking if model was downloaded previously
modelPath = "src/main/resources/edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz"
modelFile = new File(modelPath)
if (!modelFile.exists()) {
    println "File $modelFile doesnt exists. Going to get NER Models."
    modelsUrlStr = "http://central.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.6.0/stanford-corenlp-3.6.0-models.jar"
    file = File.createTempFile("corenlp-models", ".tmp")
    file.deleteOnExit()
    downloadFile(modelsUrlStr, file)
    resources = [
            "english.muc.7class.distsim.crf.ser.gz",
            "english.all.3class.distsim.crf.ser.gz",
            "english.all.3class.distsim.prop",
            "english.conll.4class.distsim.crf.ser.gz",
            "english.conll.4class.distsim.prop",
            "english.muc.7class.distsim.prop"
    ].collect { "edu/stanford/nlp/models/ner/" + it }
    copyResources(file, resources)
}