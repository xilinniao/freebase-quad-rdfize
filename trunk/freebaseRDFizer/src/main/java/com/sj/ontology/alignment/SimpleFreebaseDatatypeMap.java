package com.sj.ontology.alignment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TBD : datatype to be read by the ontology.
public class SimpleFreebaseDatatypeMap {

    private Map<String, String> datatypeMap = Collections.emptyMap();


    /*
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.text_encoding"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.datetime"/>
     * <j.0:associatedClass rdf:resource="http://rdf.freebase.com/ns#type.uri"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.float"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.unit"/>
     * <j.0:associatedClass rdf:resource="http://rdf.freebase.com/ns#type.int"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.media_type"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.rawstring"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.enumeration"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.text"/>
     * <j.0:associatedClass
     * rdf:resource="http://rdf.freebase.com/ns#type.boolean"/>
     */

    public SimpleFreebaseDatatypeMap () {
        this.datatypeMap = new HashMap<String, String>();

        this.datatypeMap.put("http://rdf.freebase.com/ns#type.float", "http://www.w3.org/2001/XMLSchema#float");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.text", "http://www.w3.org/2001/XMLSchema#string");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.boolean", "http://www.w3.org/2001/XMLSchema#boolean");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.int", "http://www.w3.org/2001/XMLSchema#integer");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.text_encoding",
            "http://www.w3.org/2001/XMLSchema#string");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.datetime", "http://www.w3.org/2001/XMLSchema#dateTime");
        // this.datatypeMap.put("http://rdf.freebase.com/ns#type.media_type",
        // "");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.rawstring", "http://www.w3.org/2001/XMLSchema#string");
        this.datatypeMap.put("http://rdf.freebase.com/ns#type.uri", "http://www.w3.org/2001/XMLSchema#anyURI");

    }

    public String getXmlDatatype(String freebaseDatatype) {
        
        String xmlDataType = "";
        xmlDataType = this.datatypeMap.get(freebaseDatatype);
        
        if (xmlDataType == null) {
            xmlDataType = "";
        }
        
        return xmlDataType;
    }

    public static void main(String [] args) {
        SimpleFreebaseDatatypeMap datatypeMap = new SimpleFreebaseDatatypeMap();
        System.out.println(datatypeMap.getXmlDatatype("http://rdf.freebase.com/ns#type.float"));
    }

}
