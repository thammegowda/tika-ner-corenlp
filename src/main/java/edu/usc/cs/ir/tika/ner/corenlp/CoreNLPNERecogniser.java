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

package edu.usc.cs.ir.tika.ner.corenlp;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;
import org.apache.tika.io.IOUtils;
import org.apache.tika.parser.ner.NERecogniser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 *  An implementation of {@link org.apache.tika.parser.ner.NERecogniser} is powered by
 *  Stanford CoreNLP's CRF classifier with NER models.
 *  @see org.apache.tika.parser.ner.NERecogniser
 *
 */
public class CoreNLPNERecogniser implements NERecogniser {

    public static final Logger LOG = LoggerFactory.getLogger(CoreNLPNERecogniser.class);

    //default model paths
    public static final String NER_3CLASS_MODEL = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";
    public static final String NER_4CLASS_MODEL = "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz";
    public static final String NER_7CLASS_MODEL = "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz";

    /**
     * default Model path
     */
    public static final String DEFAULT_MODEL_PATH = NER_7CLASS_MODEL;
    public static final Set<String> ENTITY_TYPES = new HashSet<>();
    public static final String MODEL_PROP_NAME = "ner.corenlp.model";

    static {
        ENTITY_TYPES.add(PERSON);
        ENTITY_TYPES.add(TIME);
        ENTITY_TYPES.add(LOCATION);
        ENTITY_TYPES.add(ORGANIZATION);
        ENTITY_TYPES.add(MONEY);
        ENTITY_TYPES.add(PERCENT);
        ENTITY_TYPES.add(DATE);
    }

    private CRFClassifier<CoreMap> nerClassifier;
    private boolean available = false;

    public CoreNLPNERecogniser(){
        this(System.getProperty(MODEL_PROP_NAME, DEFAULT_MODEL_PATH));
    }

    /**
     * Creates a NERecogniser by loading model from given path
     * @param modelPath path to NER model file
     */
    public CoreNLPNERecogniser(String modelPath) {
        try {
            Properties props = new Properties();
            this.nerClassifier = CRFClassifier.getClassifier(modelPath, props);
            this.available = true;
        } catch (Exception e) {
            LOG.warn("{} while trying to load the model from {}", e.getMessage(), modelPath);
        }
        LOG.info("Available for service ? {}", available);
    }

    /**
     *
     * @return true if model was available, valid and was able to initialise the classifier
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @return set of entity classes/types
     */
    public Set<String> getEntityTypes() {
        return ENTITY_TYPES;
    }

    /**
     * recognises names from text
     * @param text text with possibly contains names
     * @return map of entityType -> set of names
     */
    public Map<String, Set<String>> recognise(String text) {
        Map<String, Set<String>> names = new HashMap<String, Set<String>>();
        List<Triple<String, Integer, Integer>> entities =
                nerClassifier.classifyToCharacterOffsets(text);
        for (Triple<String, Integer, Integer> entity : entities) {
            if (!names.containsKey(entity.first)) {
                names.put(entity.first, new HashSet<String>());
            }
            String name = text.substring(entity.second, entity.third);
            //Clean repeating spaces, replace line breaks and tabs with single space
            name = name.trim().replaceAll("(\\s\\s+)|\n|\t", " ");
            if (!name.isEmpty()) {
                names.get(entity.first).add(name);
            }
        }
        return names;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Error: Invalid Args");
            System.err.println("This tool finds names inside text");
            System.err.println("Usage: <path/to/text/file>");
            return;
        }
        String text = IOUtils.toString(new FileInputStream(args[0]));
        CoreNLPNERecogniser ner = new CoreNLPNERecogniser();
        Map<String, Set<String>> names = ner.recognise(text);
        JSONObject jNames = new JSONObject(names);
        System.out.println(jNames.toString(2));
    }
}
