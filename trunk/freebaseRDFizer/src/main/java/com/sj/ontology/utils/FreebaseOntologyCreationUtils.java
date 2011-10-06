package com.sj.ontology.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.sj.freebase.data.constants.RdfizerConstants;
import com.sj.freebase.ontology.FreebaseJenaOntologyHandler;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;
import com.sj.freebase.schema.rdf.MQLFreebaseSchemaFetcher;

public class FreebaseOntologyCreationUtils {

    private static Logger logger = Logger
        .getLogger(FreebaseOntologyCreationUtils.class);


    public static OntModel getOntology(String ontologyFilepath) {
        OntModel model = ModelFactory.createOntologyModel();
        InputStream in = FileManager.get().open(ontologyFilepath);
        model.read(in, null);

        return model;
    }


    /**
     * @param args
     * @throws Exception
     */
    public static OntModel getOntologyModel(Set<String> domainsToSkip)
    throws Exception {

        if (domainsToSkip == null) {
            domainsToSkip = Collections.emptySet();
        }
        FreebaseJenaOntologyHandler creator =
            new FreebaseJenaOntologyHandler(RdfizerConstants.FREEBASE_NAMESPACE);
        MQLFreebaseSchemaFetcher fetcher = new MQLFreebaseSchemaFetcher();
        List<CharSequence> xClasses = null;
        List<CharSequence> domains = new ArrayList<CharSequence>();
        fetcher.getDomain(domains);
        //
        // System.out.println(domains.size());

        for (CharSequence domain : domains) {
            xClasses = new ArrayList<CharSequence>();
            creator.createDomain((String) domain);
            if (!domainsToSkip.contains(domain)) {
                fetcher.getType(fetcher.formatNames((String) domain), fetcher
                    .createEnvelope(FbSchemaGlobals.CURSOR_FIELD, true),
                    xClasses);
                for (CharSequence xClass : xClasses) {
                    creator.createClass((String) xClass, (String) domain);
                }
            } else {
                logger.debug("Skipping types for domain : " + domain);
            }
        }

        xClasses = null;
        fetcher.signout();
        fetcher = new MQLFreebaseSchemaFetcher();

        domains = (List<CharSequence>) creator.getAllDomains();
        for (CharSequence domain : domains) {
            if (!domainsToSkip.contains(domain)) {
                xClasses = null;
                // System.out.println(domain);
                xClasses =
                    (ArrayList<CharSequence>) creator
                        .getAllClasses((String) domain);
                for (CharSequence xClass : xClasses) {
                    List<Map<CharSequence, List<CharSequence>>> propertyMap =
                        new ArrayList<Map<CharSequence, List<CharSequence>>>();
                    fetcher.getProperties(fetcher.formatNames((String) domain),
                        fetcher.formatNames((String) xClass),
                        fetcher.createEnvelope(FbSchemaGlobals.CURSOR_FIELD,
                            true), propertyMap);

                    for (Map<CharSequence, List<CharSequence>> property : propertyMap) {
                        Iterator<CharSequence> it =
                            property.keySet().iterator();
                        while (it.hasNext()) {
                            CharSequence prop = it.next();
                            creator.createProperty((String) prop,
                                (String) xClass, property.get(prop));
                            // System.out.println("Property : " + prop +
                            // "Expected Type : " + property.get(prop));
                        }
                    }
                }
            } else {
                logger.debug("Skipping properties for domain : " + domain);
            }
        }

        // creator.write(new FileOutputStream(
        // new File(FREEBASE_ONTOLOGY_FILE_PATH)));

        OntModel model = creator.returnOntologyModel();

        fetcher.signout();

        return model;
        // creator.close();
    }


    public static void main(String [] args) throws Exception {
        OntModel model = getOntologyModel(null);

        model.write(new FileOutputStream(new File(
            RdfizerConstants.RELATIVE_SCHEMA_FILE_PATH)));
        model.close();
    }

}
