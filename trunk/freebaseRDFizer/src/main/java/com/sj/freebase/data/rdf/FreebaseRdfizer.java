package com.sj.freebase.data.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sj.data.transform.ExtDataTransformer;
import com.sj.data.transform.MalFormedAssertionException;
import com.sj.data.transform.SkippedAssertionException;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;
import com.sj.freebase.utils.StringUtils;
import com.sj.ontology.alignment.SimpleFreebaseDatatypeMap;
import com.sj.ontology.utils.FreebaseOntologyCreationUtils;

public class FreebaseRdfizer implements ExtDataTransformer<List<String>> {

    private CharSequence fieldSeparator = "\t";
    private String freebaseNsPrefix = "http://rdf.freebase.com/ns#";
    private Set<String> skipPredicateRegexList = new HashSet<String>();
    private Set<String> keyRegexList = new HashSet<String>();
    private Set<String> domainsToSkip = new HashSet<String>();
    private SimpleFreebaseDatatypeMap freebaseDataTypeMap =
        new SimpleFreebaseDatatypeMap();
    private OntModel freebaseOntModel = null;

    private static final String FB_NAMESPACE =
        "<http://rdf.freebase.com/ns/type.key.namespace>";
    private static final String FB_VALUE =
        "<http://rdf.freebase.com/ns/type.value.value>";
    private static final String DEFAULT_DOMAIN_TO_SKIP = "user";
    private static final String DEFAULT_LANG_REGEX = "/lang/";
    private static final String KEY_TYPE1_REGEX = "type.key.namespace";
    private static final String KEY_TYPE2_REGEX = "type.object.key";


    public FreebaseRdfizer (OntModel freebaseSchemaOntModel) {
        this(null, null, null, null, null, freebaseSchemaOntModel);
    }


    public FreebaseRdfizer (CharSequence fieldSeparator, String prefix,
        Set<String> predicatesToSkip, Set<String> keyPredicateRegexList,
        Set<String> domainsToSkip, OntModel freebaseSchemaOntModel) {
        if (fieldSeparator != null) {
            this.fieldSeparator = fieldSeparator;
        }

        if (prefix != null) {
            freebaseNsPrefix = prefix;
        }

        this.domainsToSkip.add(DEFAULT_DOMAIN_TO_SKIP);
        if (domainsToSkip != null) {
            this.domainsToSkip.addAll(domainsToSkip);
        }

        if (predicatesToSkip != null) {
            skipPredicateRegexList.addAll(predicatesToSkip);
        }

        if (keyPredicateRegexList == null) {
            keyRegexList.add(KEY_TYPE1_REGEX);
            keyRegexList.add(KEY_TYPE2_REGEX);
        } else {
            keyRegexList.addAll(keyPredicateRegexList);
        }

        freebaseOntModel = freebaseSchemaOntModel;
    }


    private String convertId(String id) throws NullPointerException {
        if (id == null)
            throw new NullPointerException();

        return id.replace("/", ".");
    }


    // Should linear search to be replaced by something better?
    private boolean skipAssertion(String predicate) {

        if (skipPredicateRegexList.contains(predicate)) {
            return true;
        }

        return false;
    }


    private boolean isItKeyTypeAssertion(String predicate) {

        /*
         * for (String regex: keyRegexList) { if (regex.contains(predicate))
         * return true; }
         */

        if (KEY_TYPE1_REGEX.equals(predicate) ||
            KEY_TYPE2_REGEX.equals(predicate))
            return true;

        return false;
    }


    private List<String> processQuadAssertion(String subject, String predicate,
        String to, String val) throws MalFormedAssertionException, Exception {
        List<String> triples = new ArrayList<String>();

        String triple = "";

        val = val.replace("\\", "\\\\");
        val = val.replace("\"", "\\\"");
        // val = val.replace("`", "\\`");

        triple =
            StringUtils.combine("<", freebaseNsPrefix, subject, ">\t<",
                freebaseNsPrefix, predicate, ">\t");
        // triple.append("<");
        // triple.append(freebaseNsPrefix);
        // triple.append(subject);
        // triple.append(">\t<");
        // triple.append(freebaseNsPrefix);
        // triple.append(predicate);
        // triple.append(">\t");
        if (to.length() == 0) {
            // triple.append("\"");
            OntProperty property = null;
            try {
                property =
                    freebaseOntModel.getOntProperty(freebaseNsPrefix +
                        predicate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            triple =
                StringUtils.combine(triple,
                    applyDatatypeToValue(val, property), " .");
            // triple.append(applyDatatypeToValue(val, range.getURI()));
            // triple.append(" .");
            triples.add(triple);

        } else if (isItKeyTypeAssertion(predicate)) {
            String blankNodeId =
                subject.replaceAll("[^a-zA-Z0-9]", "_") +
                    to.replaceAll("[^a-zA-Z0-9]", "_");
            // Create blanknode
            to = convertId(to.substring(1, to.length()));// to.substring(1,
                                                         // to.length()).replace("/",
                                                         // ".");
            triple = StringUtils.combine(triple, "_:blank", blankNodeId, " .");
            // triple.append("_:blank");
            // triple.append(blankNodeId);
            // // triple.append(to.replace(".", "_"));
            // triple.append(" .");
            triples.add(triple);

            triple =
                StringUtils.combine("_:blank", blankNodeId, "\t", FB_NAMESPACE,
                    "\t<", freebaseNsPrefix, to, "> .");
            // triple.append("_:blank");
            // triple.append(blankNodeId);
            // // triple.append(to.replace(".", "_"));
            // triple.append("\t");
            // triple.append(FB_NAMESPACE);
            // triple.append("\t<");
            // triple.append(freebaseNsPrefix);
            // triple.append(to);
            // triple.append("> .");
            triples.add(triple);

            triple =
                StringUtils.combine("_:blank", blankNodeId, "\t", FB_VALUE,
                    "\t", "\"", val, "\" .");
            // triple.append("_:blank");
            // triple.append(blankNodeId);
            // // triple.append(to.replace(".", "_"));
            // triple.append("\t");
            // triple.append(FB_VALUE);
            // triple.append("\t");
            // triple.append("\"");
            // triple.append(val);
            // triple.append("\" .");
            triples.add(triple);

        } else if (to.contains(DEFAULT_LANG_REGEX)) {
            to = to.replace(DEFAULT_LANG_REGEX, "");

            triple = StringUtils.combine(triple, "\"", val, "\"@", to, " .");
            // triple.append("\"");
            // triple.append(val);
            // triple.append("\"@");
            // triple.append(to); // lang
            // triple.append(" .");
            triples.add(triple);
        } else {
            throw new MalFormedAssertionException(subject + "\t" + predicate +
                "\t" + to + "\t" + val);
        }

        return triples;
    }


    @Override
    public List<String> transformData(String assertion)
    throws NullPointerException, MalFormedAssertionException,
    SkippedAssertionException, UnsupportedEncodingException, Exception {
        if (assertion == null)
            throw new NullPointerException();

        String [] splits = assertion.split(fieldSeparator.toString());
        if (splits.length < 3 || splits.length > 4) {
            throw new MalFormedAssertionException(assertion);
        }

        List<String> triples = Collections.emptyList();
        String predicate =
            convertId(splits[1].substring(1, splits[1].length()));

        // skipDomain(predicate) ||
        if (skipAssertion(predicate)) {
            throw new SkippedAssertionException(assertion);
        }

        if (splits.length == 3) {
            String transformedAssertion = "";

            transformedAssertion =
                StringUtils.combine("<", freebaseNsPrefix, convertId(splits[0]
                    .substring(1, splits[0].length())), ">\t<",
                    freebaseNsPrefix, predicate, ">\t<", freebaseNsPrefix,
                    convertId(splits[2].substring(1, splits[2].length())),
                    "> . ");
            // transformedAssertion.append("<");
            // transformedAssertion.append(freebaseNsPrefix);
            // transformedAssertion.append(convertId(splits[0].substring(1,
            // splits[0].length())));
            // transformedAssertion.append(">\t<");
            // transformedAssertion.append(freebaseNsPrefix);
            // transformedAssertion.append(predicate);
            // transformedAssertion.append(">\t<");
            // transformedAssertion.append(freebaseNsPrefix);
            // transformedAssertion.append(convertId(splits[2].substring(1,
            // splits[2].length())));
            // transformedAssertion.append("> . ");
            triples = new ArrayList<String>();
            triples.add(transformedAssertion);
        } else if (splits.length == 4) {
            triples =
                processQuadAssertion(convertId(splits[0].substring(1, splits[0]
                    .length())), predicate, new String(splits[2]
                    .getBytes("UTF-8"), "UTF-8"), new String(splits[3]
                    .getBytes("UTF-8"), "UTF-8"));
        }

        return triples;
    }


    private static void display(List<String> assertions) {
        for (String assertion : assertions)
            System.out.println(assertion);
    }


    private boolean skipDomain(String predicate) {
        String [] splitPredicate = predicate.split("\\.");

        if (splitPredicate == null || splitPredicate.length != 3) {
            return false;
        }

        if (this.domainsToSkip.contains(splitPredicate[0])) {
            return true;
        }

        return false;
    }


    private String applyDatatypeToValue(String value,
        OntProperty freebaseProperty) {
        String xmlDataType = null;

        if (freebaseProperty != null) {
            xmlDataType =
                freebaseDataTypeMap.getXmlDatatype(freebaseProperty.getRange()
                    .getURI());
        }

        String typedValue = "";
        if (xmlDataType != null && !xmlDataType.isEmpty()) {
            typedValue =
                StringUtils.combine("\"", value, "\"^^ <", xmlDataType, ">");
        } else {
            typedValue = StringUtils.combine("\"", value, "\"");
        }

        return typedValue;
    }


    public static void main(String [] args) throws Exception {
        Set<String> predicatesToSkip = new HashSet<String>();
        predicatesToSkip.add("common.topic.notable_for");

        FreebaseRdfizer rdfizer =
            new FreebaseRdfizer(null, null, predicatesToSkip, null, null,
                FreebaseOntologyCreationUtils.getOntologyModel(null));

        FreebaseRdfizer.display(rdfizer
            .transformData("/m/0p_47\t/film/actor/film\t/m/02vcwc8"));
        FreebaseRdfizer.display(rdfizer
            .transformData("/m/0p_47	/film/actor/film	/m/02vcwc8"));
        FreebaseRdfizer.display(rdfizer
            .transformData("/m/0p_47\t/people/person/height_meters\t\t1.83"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/01hf9dc\t/type/object/name\t\t\"Don`t Shoot Me I`m Only the Piano Player\""));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/0p_47\t/type/object/name\t/lang/en\tSteve Martin"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/0p_47\t/type/object/key\t/wikipedia/pt\tSteve_Martin"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/0p_47\t/type/object/name\t/lang/en\tSteve'Martin"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/0p_47\t/type/object/name\t/lang/en\tSteve\"Martin"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/063q09g	/freebase/labs_project/publicized_date		2009-06-04"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/063q09g\t/base/labs_project/publicized_date\t\t2009-06-04"));
        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/063q09g\t/music/labs_project/publicized_date\t\t2009-06-04"));

        FreebaseRdfizer
            .display(rdfizer
                .transformData("/m/026jl_d\t/music/object/name\t/guid/9202a8c04000641f8000000004684bec\t\"2004-11-22\""));

    }
}
