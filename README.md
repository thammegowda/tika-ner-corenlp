# Stanford CoreNLP addon for Apache Tika's NER Parser

This project supplies necessary resources to Apache Tika's `NamedEntityParser` and demonstrates how to activate the NER implementation based on Stanford CoreNLP's CRF classifiers.

The usage of this addon has been documented at http://wiki.apache.org/tika/TikaAndNER#Using_Stanford_CoreNLP_NER

# Requirements

+ newer maven - tested on maven 3.3
+ newer JDK - Tested on JDK 1.8
+ (Other dependencies will be fetched from Maven repositories, requires internet)

# How to Build :

+ to get jar for dropping into tika's classpath  
 `mvn clean compile assembly:single -PtikaAddon`
 
+ To test :
 `mvn exec:java -Dexec.args=README.md`
   NOTE: README.md is a CLI argument

# How to configure tika
  
  1. Get the jar obtained in previous step (`target/tika-ner-corenlp-addon-1.0-SNAPSHOT-jar-with-dependencies.jar`) and add it to tika's classpath (requires Tika 1.12).
> Alternatively, it is simple if your are using maven.
>>+ Build and install this project to local maven repo by running `mvn install` on this project
>>+ Add this dependency to your project
    ```xml
    <dependency>
     <groupId>edu.usc.ir.tika</groupId>
     <artifactId>tika-ner-corenlp</artifactId>
     <version>1.0-SNAPSHOT</version>
    </dependency>
    ```

  2. Set system property `ner.impl.class` to `org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser`.
     An example usage is shown in test case [`NamedEntityParserTest.java`](src/test/java/edu/usc/cs/ir/tika/ner/NamedEntityParserTest.java)

  3. Activate `org.apache.tika.parser.ner.NamedEntityParser`. An example configuration is at [src/main/resources/tika-config.xml](src/main/resources/tika-config.xml)


# Contributors :

+ Thamme Gowda N - `tgowdan` at `gmail.com`
 
