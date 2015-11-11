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

package edu.usc.cs.ir.tika.ner;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

/**
 * A test case for NamedEntityParser with CoreNLP recogniser
 */
public class NamedEntityParserTest {

    public static final String CONFIG_FILE = "tika-config.xml";
    @Test
    public void testTikaNER() throws Exception {
        System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL,
                CoreNLPNERecogniser.class.getName());


        //test config is added to resources directory
        TikaConfig config = new TikaConfig(getClass().getClassLoader()
                .getResourceAsStream(CONFIG_FILE));
        Tika tika = new Tika(config);

        String text = "I am student at University of Southern California(USC)," +
                " located in Los Angeles, California. USC's football team is called by name Trojans." +
                " Mr. John McKay was the head coach of the team from 1960 - 1975.";
        Metadata md = new Metadata();
        tika.parse(new ByteArrayInputStream(text.getBytes()), md);

        HashSet<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(md.getValues("X-Parsed-By")));
        assertTrue(set.contains(NamedEntityParser.class.getName()));

        set.clear();
        set.addAll(Arrays.asList(md.getValues("NER_PERSON")));
        assertTrue(set.contains("John McKay"));

        set.clear();
        set.addAll(Arrays.asList(md.getValues("NER_LOCATION")));
        assertTrue(set.contains("Los Angeles"));
        assertTrue(set.contains("California"));

        set.clear();
        set.addAll(Arrays.asList(md.getValues("NER_ORGANIZATION")));
        assertTrue(set.contains("University of Southern California"));

        set.clear();
        set.addAll(Arrays.asList(md.getValues("NER_DATE")));
        assertTrue(set.contains("1975"));
        assertTrue(set.contains("1960"));

    }
}