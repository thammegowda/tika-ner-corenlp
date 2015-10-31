# Stanford CoreNLP addon for Apache Tika's NER Parser

This project provides bridges Stanford CoreNLP's NER classifiers to Apache Tika.


# requires

+ newer maven - tested on maven 3.3
+ newer JDK - Tested on JDK 1.8
+ (Other dependencies will be fetched from Maven repositories)

# how to build :

+ to get jar for dropping into tika's classpath  
 `mvn clean compile assembly:single -PtikaAddon`
 
+ To test :
 `mvn exec:java -Dexec.args=README.md`

# How to configure tika
  
  1. Get the jar obtained in previous step (`target/tika-ner-corenlp-addon-1.0-SNAPSHOT-jar-with-dependencies.jar`) and add it 
  to tika's classpath (requires Tika 1.12).

  
  2. Set a system property `ner.impl.class` to `edu.usc.cs.ir.tika.ner.corenlp.CoreNLPNERecogniser`.
     An example usage is shown in test case `src/test/java/edu/usc/cs/ir/tika/ner/NamedEntityParserTest.java`

  3. Activate `org.apache.tika.parser.ner.NamedEntityParser`. An example configuration is at `src/main/resources/tika-config.xml`


# Contributors :

+ Thamme Gowda N - `tgowdan` at `gmail.com`
 