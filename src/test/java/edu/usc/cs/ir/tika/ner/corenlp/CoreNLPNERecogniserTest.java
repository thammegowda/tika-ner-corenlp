package edu.usc.cs.ir.tika.ner.corenlp;

import org.apache.tika.parser.ner.NERecogniser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by tg on 10/29/15.
 */
public class CoreNLPNERecogniserTest {

    @Test
    public void testIsAvailable() throws Exception {

        System.setProperty(CoreNLPNERecogniser.MODEL_PROP_NAME, "invalid/path");
        CoreNLPNERecogniser ner = new CoreNLPNERecogniser();
        assertFalse(ner.isAvailable());

        System.setProperty(CoreNLPNERecogniser.MODEL_PROP_NAME, CoreNLPNERecogniser.DEFAULT_MODEL_PATH);
        ner = new CoreNLPNERecogniser();
        assertTrue(ner.isAvailable());

        assertTrue(new CoreNLPNERecogniser(CoreNLPNERecogniser.NER_7CLASS_MODEL).isAvailable());
        assertTrue(new CoreNLPNERecogniser(CoreNLPNERecogniser.NER_4CLASS_MODEL).isAvailable());
        assertTrue(new CoreNLPNERecogniser(CoreNLPNERecogniser.NER_3CLASS_MODEL).isAvailable());
    }

    @Test
    public void testGetEntityTypes() throws Exception {

        Set<String> types = new CoreNLPNERecogniser(CoreNLPNERecogniser.DEFAULT_MODEL_PATH).getEntityTypes();
        assertTrue(!types.isEmpty());
    }

    @Test
    public void testRecognise() throws Exception {
        NERecogniser ner = new CoreNLPNERecogniser(CoreNLPNERecogniser.DEFAULT_MODEL_PATH);

        String usc = "University of Southern California";
        String la = "Los Angeles";
        String august = "August 2015";

        String text = usc + " is located in " + la + ". I started my graduate studies in " + august ;
        Map<String, Set<String>> names = ner.recognise(text);
        assertTrue(names.get(CoreNLPNERecogniser.DATE).contains(august));
        assertTrue(names.get(CoreNLPNERecogniser.LOCATION).contains(la));
        assertTrue(names.get(CoreNLPNERecogniser.ORGANIZATION).contains(usc));

    }


}