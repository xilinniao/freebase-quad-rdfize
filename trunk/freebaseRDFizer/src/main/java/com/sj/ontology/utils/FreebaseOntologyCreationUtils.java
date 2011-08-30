package com.sj.ontology.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sj.freebase.ontology.FreebaseJenaOntologyHandler;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;
import com.sj.freebase.schema.rdf.MQLFreebaseSchemaFetcher;

public class FreebaseOntologyCreationUtils {
    public static String FREEBASE_ONTOLOGY_FILE_PATH =
        "freebaseOntology.rdfs";


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String [] args) throws Exception {
        FreebaseJenaOntologyHandler creator =
            new FreebaseJenaOntologyHandler("http://rdf.freebase.com/ns#");
        MQLFreebaseSchemaFetcher fetcher = new MQLFreebaseSchemaFetcher();
        ArrayList<CharSequence> domains = new ArrayList<CharSequence>();
        ArrayList<CharSequence> xClasses = null;
        fetcher.getDomain(domains);

        System.out.println(domains.size());

        for (CharSequence domain : domains) {
            xClasses = new ArrayList<CharSequence>();
            creator.createDomain((String) domain);
            fetcher.getType(fetcher.formatNames((String) domain), fetcher
                .createEnvelope(FbSchemaGlobals.CURSOR_FIELD, true), xClasses);
            for (CharSequence xClass : xClasses)
                creator.createClass((String) xClass, (String) domain);
        }

        domains = null;
        xClasses = null;
        fetcher.signout();
        fetcher = new MQLFreebaseSchemaFetcher();

        domains = (ArrayList<CharSequence>) creator.getAllDomains();
        for (CharSequence domain : domains) {
            xClasses = null;
            System.out.println(domain);
            xClasses =
                (ArrayList<CharSequence>) creator
                    .getAllClasses((String) domain);
            for (CharSequence xClass : xClasses) {
                List<Map<CharSequence, List<CharSequence>>> propertyMap =
                    new ArrayList<Map<CharSequence, List<CharSequence>>>();
                fetcher.getProperties(fetcher.formatNames((String) domain),
                    fetcher.formatNames((String) xClass), fetcher
                        .createEnvelope(FbSchemaGlobals.CURSOR_FIELD, true),
                    propertyMap);

                for (Map<CharSequence, List<CharSequence>> property : propertyMap) {
                    Iterator<CharSequence> it = property.keySet().iterator();
                    while (it.hasNext()) {
                        CharSequence prop = it.next();
                        creator.createProperty((String) prop, (String) xClass,
                            property.get(prop));
                        System.out.println("Property : " + prop +
                            "Expected Type : " + property.get(prop));
                    }
                }
            }
        }

        creator.write(new FileOutputStream(
            new File(FREEBASE_ONTOLOGY_FILE_PATH)));

        fetcher.signout();
        creator.close();

    }
}
