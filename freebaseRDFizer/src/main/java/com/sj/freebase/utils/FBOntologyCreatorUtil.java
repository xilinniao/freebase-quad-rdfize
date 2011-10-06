package com.sj.freebase.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FBOntologyCreatorUtil {
    private static final Logger log = Logger.getLogger(FBOntologyCreatorUtil.class);


    public static String getFreebaseOntology(String baseURI,
        Map<String, List<String>> schemaMap) throws IOException {
        String schemaOntology = "";
        OntModel model = ModelFactory.createOntologyModel();
        model.setNsPrefix("fbBase", baseURI);

        Set<Entry<String, List<String>>> schemaEntrySet = schemaMap.entrySet();

        for (Entry<String, List<String>> schemaEntry : schemaEntrySet) {
            String key = schemaEntry.getKey();
            List<String> categories = schemaEntry.getValue();
            OntClass ontClass = model.createClass(baseURI + "#" + key);
            for (String category : categories) {
                OntClass subClass =
                    model.createClass(baseURI + "#" +
                        category.replaceAll("/", "-"));
                subClass.setLabel(category, "en");
                ontClass.addSubClass(subClass);
            }
        }

        StringWriter writer = new StringWriter();
        model.write(writer);

        schemaOntology = writer.getBuffer().toString();
        writer.close();
        model.close();
        return schemaOntology;
    }


    public static void main(String... args)
    throws MalformedURLException, IOException {
        FacebookInterestSchema schema =
            new FacebookInterestSchema(
                "http://www.facebook.com/pages/create.php");
        System.out.println(getFreebaseOntology("http://www.freebase.schema",
            schema.getInterestSchemaMap()));
    }
}
