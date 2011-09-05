package com.sj.freebase.data.rdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.sj.data.transform.MalFormedAssertionException;
import com.sj.data.transform.SkippedAssertionException;
import com.sj.freebase.data.rdf.FreebaseRdfizer;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;
import com.sj.freebase.utils.ConfigUtils;
import com.sj.ontology.utils.FreebaseOntologyCreationUtils;

public class DemoFreebaseRdfizer {

    public static Logger logger = Logger.getLogger("DemoFreebaseRdfizer");


    private static void display(List<StringBuffer> assertions) {
        for (StringBuffer assertion : assertions)
            System.out.println(assertion);
    }


    public static void main(String [] args) throws Exception {
        logger.setLevel(Level.INFO);
        Set<String> predicatesToSkip = ConfigUtils.getPropertiesToSkip();
        predicatesToSkip.addAll(ConfigUtils.getTypesToSkip());
        Set<String> domainsToSkip = ConfigUtils.getDomainsToSkip();

        OntModel model =
            FreebaseOntologyCreationUtils.getOntologyModel(domainsToSkip);
        FreebaseRdfizer rdfizer =
            new FreebaseRdfizer(null, null, predicatesToSkip, null,
                domainsToSkip, model);

        if (args.length < 1 || args.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Usage: DemoFreebaseRdfizer list{<tsv file> <converted rdf file name>}");
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < args.length; i++) {

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(args[i]))));
            // String src = args[i];
            i++;
            // String dest = args [i];
            // System.out.println (src + "--->" + dest);
            BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    new File(args[i]))));
            String assertion = null;
            while ((assertion = reader.readLine()) != null) {
                try {
                    // display (rdfizer.transformData(assertion));

                    List<String> triples = rdfizer.transformData(assertion);
                    for (String triple : triples) {
                        System.out.println(triple);
                        // writer.write(triple.toString() + "\n");
                    }
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    model.close();
                    reader.close();
                    writer.close();
                    throw e;
                } catch (MalFormedAssertionException e) {
                    // TODO Auto-generated catch block
                    // System.out.println (e.getAssertion());
                    logger.warn("Malformed Assertion:" + e.getAssertion());

                } catch (SkippedAssertionException e) {
                    // TODO Auto-generated catch block
                    // System.out.println (e.getAssertion());
                    if (e.toString().contains("/m/03frbs5")) {
                        throw e;
                    }
                    logger.warn("Skipped Assertion:   ---   " +
                        e.getAssertion());
                } 
            }
            reader.close();
            writer.close();
        }
        
        model.close();
        System.out.println("Total time taken : " +
            (System.currentTimeMillis() - startTime));

    }
}
