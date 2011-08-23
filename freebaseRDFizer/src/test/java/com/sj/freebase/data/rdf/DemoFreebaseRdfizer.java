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

import com.sj.data.transform.MalFormedAssertionException;
import com.sj.data.transform.SkippedAssertionException;
import com.sj.freebase.data.rdf.FreebaseRdfizer;

public class DemoFreebaseRdfizer {

    public static Logger logger = Logger.getLogger("DemoFreebaseRdfizer");


    private static void display(List<StringBuffer> assertions) {
        for (StringBuffer assertion : assertions)
            System.out.println(assertion);
    }


    public static void main(String [] args) throws Exception {
        logger.setLevel(Level.INFO);
        List<String> predicatesToSkip = new ArrayList<String>();
        predicatesToSkip.add("common.topic.notable_for");

        Set<String> domainsToSkip = new HashSet<String>();
        domainsToSkip.add("music");
        FreebaseRdfizer rdfizer =
            new FreebaseRdfizer(null, null, predicatesToSkip, null,
                domainsToSkip);

        if (args.length < 1 || args.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Usage: DemoFreebaseRdfizer list{<tsv file> <converted rdf file name>}");
        }

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

                    List<StringBuffer> triples =
                        rdfizer.transformData(assertion);
                    for (StringBuffer triple : triples) {
                        writer.write(triple.toString() + "\n");
                    }
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    throw e;
                } catch (MalFormedAssertionException e) {
                    // TODO Auto-generated catch block
                    // System.out.println (e.getAssertion());
                    logger.warn("Malformed Assertion:" + e.getAssertion());

                } catch (SkippedAssertionException e) {
                    // TODO Auto-generated catch block
                    // System.out.println (e.getAssertion());
                    logger.warn("Skipped Assertion:" + e.getAssertion());
                }
            }
            reader.close();
            writer.close();
        }

    }
}
