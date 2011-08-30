package com.sj.freebase.data.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sj.data.transform.ExtDataTransformer;
import com.sj.data.transform.MalFormedAssertionException;
import com.sj.data.transform.SkippedAssertionException;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;
import com.sj.ontology.alignment.SimpleFreebaseDatatypeMap;

public class FreebaseRdfizer implements ExtDataTransformer<List<StringBuffer>> {
    private CharSequence fieldSeparator = "\t";
    private String freebaseNsPrefix = "http://rdf.freebase.com/ns#";
    private List<String> skipPredicateRegexList = new ArrayList<String>();
    private List<String> keyRegexList = new ArrayList<String>();
    private Set<String> domainsToSkip = new HashSet<String>();
    private SimpleFreebaseDatatypeMap freebaseDataTypeMap =
        new SimpleFreebaseDatatypeMap();
    private OntModel freebaseOntModel = ModelFactory
        .createOntologyModel(OntModelSpec.RDFS_MEM);

    private static final String FB_NAMESPACE =
        "<http://rdf.freebase.com/ns/type.key.namespace>";
    private static final String FB_VALUE =
        "<http://rdf.freebase.com/ns/type.value.value>";
    private static final String DEFAULT_DOMAIN_TO_SKIP = "user";
    private static final String DEFAULT_LANG_REGEX = "/lang/";
    private static final String KEY_TYPE1_REGEX = "type.key.namespace";
    private static final String KEY_TYPE2_REGEX = "type.object.key";

    public FreebaseRdfizer (InputStream ipStream) {
        this(null, null, null, null, null, ipStream);
    }


    public FreebaseRdfizer (CharSequence fieldSeparator, String prefix,
        List<String> predicatesToSkip, List<String> keyPredicateRegexList,
        Set<String> domainsToSkip, InputStream ipStream) {
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

        freebaseOntModel.read(ipStream, freebaseNsPrefix);
    }


    private String convertId(String id) throws NullPointerException {
        if (id == null)
            throw new NullPointerException();

        return id.replace("/", ".");
    }


    // Should linear search to be replaced by something better?
    private boolean skipAssertion(String predicate) {
        for (String regex : skipPredicateRegexList) {
            if (regex.contains(predicate))
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


    private List<StringBuffer> processQuadAssertion(String subject,
        String predicate, String to, String val)
    throws MalFormedAssertionException {
        List<StringBuffer> triples = new ArrayList<StringBuffer>();
        ;
        StringBuffer triple = new StringBuffer();

        val = val.replace("\\", "\\\\");
        val = val.replace("\"", "\\\"");
        // val = val.replace("`", "\\`");
        triple.append("<");
        triple.append(freebaseNsPrefix);
        triple.append(subject);
        triple.append(">\t<");
        triple.append(freebaseNsPrefix);
        triple.append(predicate);
        triple.append(">\t");
        if (to.length() == 0) {
            // triple.append("\"");
            System.out.println(freebaseOntModel.getOntProperty(
                freebaseNsPrefix + predicate).getClass().getName());
            OntResource range =
                freebaseOntModel.getOntProperty(freebaseNsPrefix + predicate)
                    .getRange();
            triple.append(applyDatatypeToValue(val, range.getURI()));
            triple.append(" .");
            triples.add(triple);
        } else if (isItKeyTypeAssertion(predicate)) {
            String blankNodeId =
                subject.replaceAll("[^a-zA-Z0-9]", "_") +
                    to.replaceAll("[^a-zA-Z0-9]", "_");
            // Create blanknode
            to = convertId(to.substring(1, to.length()));// to.substring(1,
                                                         // to.length()).replace("/",
                                                         // ".");
            triple.append("_:blank");
            triple.append(blankNodeId);
            // triple.append(to.replace(".", "_"));
            triple.append(" .");
            triples.add(triple);
            triple = null;
            triple = new StringBuffer();
            triple.append("_:blank");
            triple.append(blankNodeId);
            // triple.append(to.replace(".", "_"));
            triple.append("\t");
            triple.append(FB_NAMESPACE);
            triple.append("\t<");
            triple.append(freebaseNsPrefix);
            triple.append(to);
            triple.append("> .");
            triples.add(triple);
            triple = null;
            triple = new StringBuffer();
            triple.append("_:blank");
            triple.append(blankNodeId);
            // triple.append(to.replace(".", "_"));
            triple.append("\t");
            triple.append(FB_VALUE);
            triple.append("\t");
            triple.append("\"");
            triple.append(val);
            triple.append("\" .");
            triples.add(triple);

        } else if (to.contains(DEFAULT_LANG_REGEX)) {
            to = to.replace(DEFAULT_LANG_REGEX, "");
            triple.append("\"");
            triple.append(val);
            triple.append("\"@");
            triple.append(to); // lang
            triple.append(" .");
            triples.add(triple);
        } else {
            throw new MalFormedAssertionException(subject + "\t" + predicate +
                "\t" + to + "\t" + val);
        }

        return triples;
    }


    @Override
    public List<StringBuffer> transformData(String assertion)
    throws NullPointerException, MalFormedAssertionException,
    SkippedAssertionException, UnsupportedEncodingException {
        if (assertion == null)
            throw new NullPointerException();

        String [] splits = assertion.split(fieldSeparator.toString());
        if (splits.length < 3 || splits.length > 4) {
            throw new MalFormedAssertionException(assertion);
        }

        List<StringBuffer> triples = null;
        String predicate =
            convertId(splits[1].substring(1, splits[1].length()));

        if (skipDomain(predicate) || skipAssertion(predicate)) {
            throw new SkippedAssertionException(assertion);
        }

        if (splits.length == 3) {
            StringBuffer transformedAssertion = null;
            transformedAssertion = new StringBuffer();
            transformedAssertion.append("<");
            transformedAssertion.append(freebaseNsPrefix);
            transformedAssertion.append(convertId(splits[0].substring(1,
                splits[0].length())));
            transformedAssertion.append(">\t<");
            transformedAssertion.append(freebaseNsPrefix);
            transformedAssertion.append(predicate);
            transformedAssertion.append(">\t<");
            transformedAssertion.append(freebaseNsPrefix);
            transformedAssertion.append(convertId(splits[2].substring(1,
                splits[2].length())));
            transformedAssertion.append("> . ");
            triples = new ArrayList<StringBuffer>();
            triples.add(transformedAssertion);
        } else if (splits.length == 4) {
            triples =
                processQuadAssertion(convertId(splits[0].substring(1, splits[0]
                    .length())), predicate, new String(splits[2]
                    .getBytes("utf8"), "utf8"), new String(splits[3]
                    .getBytes("utf8"), "utf8"));
        }

        return triples;
    }


    private static void display(List<StringBuffer> assertions) {
        for (StringBuffer assertion : assertions)
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


    private String applyDatatypeToValue(String value, String freebaseDataType) {
        String xmlDataType =
            freebaseDataTypeMap.getXmlDatatype(freebaseDataType);

        String typedValue = "";
        // TBD : string utils
        if (xmlDataType != null && !xmlDataType.isEmpty()) {
            typedValue = "\"" + value + "\"^^ <" + xmlDataType + ">";
        } else {
            typedValue = value;
        }

        return typedValue;
    }


    public static void main(String [] args) throws Exception {
        List<String> predicatesToSkip = new ArrayList<String>();
        predicatesToSkip.add("common.topic.notable_for");
        FreebaseRdfizer rdfizer =
            new FreebaseRdfizer(null, null, predicatesToSkip, null, null,
                new FileInputStream(new File(
                    FbSchemaGlobals.FREEBASE_ONTOLOGY_PATH)));

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
                .transformData("/m/026jl_d\t/type/object/name\t/guid/9202a8c04000641f8000000004684bec\t\"2004-11-22\""));

    }
}
