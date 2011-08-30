package com.sj.freebase.ontology;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FreebaseOntology {
    public void createDomain(String domainName) throws Exception;


    public void createClass(String className, String domainName)
    throws Exception;


    public void createProperty(String propName, String PropsdomainName,
        List<CharSequence> rangeNames) throws Exception;


    public List<CharSequence> getAllDomains() throws Exception;


    public List<CharSequence> getAllClasses(String domainName) throws Exception;


    public List<CharSequence> getProperties(String xClass) throws Exception;


    public String getPropertyDomain(String propertyName) throws Exception;


    public List<CharSequence> getPropertyRange(String propertyName)
    throws Exception;


    public void readOntology(InputStream ipStream, String lang)
    throws Exception;
}
