package com.sj.freebase.ontology;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.sj.freebase.schema.rdf.FbSchemaGlobals;

public class FreebaseJenaOntologyHandler implements FreebaseOntology {

    private OntModel model = null;
    private String baseURI;


    public FreebaseJenaOntologyHandler (String baseURI) {
        model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        this.baseURI = baseURI;
        model.createOntProperty(baseURI +
            FbSchemaGlobals.DOMAIN_CLASS_ASSOCIATION_PROPERTY);
    }


    @Override
    public void createDomain(String domainName) throws Exception {
        model.createResource(baseURI + domainName).addProperty(
            model.createProperty(FbSchemaGlobals.RDF_TYPE_PROPERTY),
            model.getResource(baseURI + FbSchemaGlobals.DOMAIN_TYPE_PROPERTY));
    }


    @Override
    public void createClass(String className, String domainName)
    throws Exception {
        model.createClass(baseURI + className);
        model.getResource(baseURI + domainName).addProperty(
            model.getOntProperty(baseURI +
                FbSchemaGlobals.DOMAIN_CLASS_ASSOCIATION_PROPERTY),
            model.getResource(baseURI + className));

    }


    @Override
    public void createProperty(String propertyName, String PropsdomainName,
        List<CharSequence> ranges) throws Exception {

        model.createOntProperty(baseURI + propertyName);

        model.getOntProperty(baseURI + propertyName).addDomain(
            model.getResource(baseURI + PropsdomainName));

        for (CharSequence range : ranges) {
            model.getOntProperty(baseURI + propertyName).addRange(
                model.getResource(baseURI + range));
        }
    }


    public void close() throws Exception {
        model.close();
    }


    public void write(OutputStream stream) throws Exception {
        model.write(stream);
    }


    @Override
    public List<CharSequence> getAllDomains() {
        List<CharSequence> domains = new ArrayList<CharSequence>();
        ResIterator it =
            model.listSubjectsWithProperty(model
                .getProperty(FbSchemaGlobals.RDF_TYPE_PROPERTY), model
                .getResource(baseURI + FbSchemaGlobals.DOMAIN_TYPE_PROPERTY));

        while (it.hasNext()) {
            domains.add(it.nextResource().getLocalName());
        }

        return domains;
    }


    @Override
    public List<CharSequence> getAllClasses(String domainName) throws Exception {
        List<CharSequence> xClasses = new ArrayList<CharSequence>();
        NodeIterator it =
            model.listObjectsOfProperty(
                model.getResource(baseURI + domainName), model
                    .getProperty(baseURI +
                        FbSchemaGlobals.DOMAIN_CLASS_ASSOCIATION_PROPERTY));
        while (it.hasNext()) {

            xClasses.add(it.nextNode().asNode().getLocalName());
        }

        return xClasses;
    }


    @Override
    public List<CharSequence> getProperties(String xClass) throws Exception {
        List<CharSequence> properties = new ArrayList<CharSequence>();
        ResIterator it =
            model.listResourcesWithProperty(model
                .getProperty(FbSchemaGlobals.RDFS_DOMAIN), model
                .getResource(baseURI + xClass));

        while (it.hasNext()) {

            properties.add(it.nextResource().getLocalName());
        }
        return properties;
    }


    @Override
    public String getPropertyDomain(String propertyName) throws Exception {
        List<CharSequence> domainList = new ArrayList<CharSequence>();
        NodeIterator it =
            model.listObjectsOfProperty(model.getResource(baseURI +
                propertyName), model.getProperty(FbSchemaGlobals.RDFS_DOMAIN));

        int count = 0;
        while (it.hasNext()) {
            if (count != 0) {
                throw new IllegalStateException(
                    "Unknown state, found more than one domain for the property: " +
                        propertyName);
            }
            domainList.add(it.nextNode().asNode().getLocalName());
            ++count;
        }

        if (count == 0)
            throw new Exception("Failed to find domain for the property : " +
                propertyName);

        return (String) domainList.get(0);
    }


    @Override
    public List<CharSequence> getPropertyRange(String propertyName)
    throws Exception {
        List<CharSequence> rngeList = new ArrayList<CharSequence>();
        NodeIterator it =
            model.listObjectsOfProperty(model.getProperty(baseURI +
                propertyName), model.getProperty(FbSchemaGlobals.RDFS_RANGE));

        while (it.hasNext()) {
            rngeList.add(it.nextNode().asNode().getLocalName());
        }

        return rngeList;
    }


    @Override
    public void readOntology(InputStream ipStream, String lang)
    throws Exception {
        model.read(ipStream, baseURI, lang);
    }


    public static void main(String [] args) throws Exception {

        FreebaseJenaOntologyHandler creator =
            new FreebaseJenaOntologyHandler("http://rdf.freebase.com/ns#");
        creator.createDomain("music");
        creator.createClass("music.album", "music");
        creator.createClass("music.artist", "music");
        creator.createDomain("common");
        creator.createClass("common.album", "common");

        List<CharSequence> ranges = new ArrayList<CharSequence>();
        ranges.add("music.artist");
        creator.createProperty("music.album.artist", "music.album", ranges);
        ranges = new ArrayList<CharSequence>();
        ranges.add("music.genre");
        creator.createProperty("music.album.genre", "music.album", ranges);

        System.out.println("Domain : " + creator.getAllDomains());
        System.out.println("Class : " + creator.getAllClasses("music"));
        System.out.println("Album Properties : " +
            creator.getProperties("music.album"));
        System.out.println("Artist Domain : " +
            creator.getPropertyDomain("music.album.artist"));
        System.out.println("Artist Range : " +
            creator.getPropertyRange("music.album.artist"));
        System.out.println("Genre Domain : " +
            creator.getPropertyDomain("music.album.genre"));
        System.out.println("Genre Range : " +
            creator.getPropertyRange("music.album.genre"));

        creator.write(System.out);
    }

}
