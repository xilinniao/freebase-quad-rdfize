package com.sj.freebase.schema.rdf;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;

import static com.freebase.json.JSON.a;
import static com.freebase.json.JSON.o;

public class FbSchemaGlobals {
    // Default Limit
    public static int DEFAULT_LIMIT = 200;
    public static String CURSOR_FIELD = "cursor";
    public static String STATUS_FIELD = "status";
    public static String CODE_FIELD = "code";
    public static String STATUS_SUCCESS_VALUE = "200 OK";
    public static String CODE_SUCCESS_VALUE = "/api/status/ok";
    public static String RESULT_FIELD = "result";

    // Queries
    public static JSON DOMAIN_QUERY = a(o("id", null, "type", "/type/domain",
        "key", a(o("namespace", "/"))));

    public static final String DOMAIN_CLASS_ASSOCIATION_PROPERTY =
        "/associatedClass";
    public static final String DOMAIN_TYPE_PROPERTY = "/domain";
    public static final String RDF_TYPE_PROPERTY =
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String RDFS_DOMAIN =
        "http://www.w3.org/2000/01/rdf-schema#domain";
    public static final String RDFS_RANGE =
        "http://www.w3.org/2000/01/rdf-schema#range";

    public static final String FREEBASE_ONTOLOGY_PATH =
        "./resources/freebaseOntology.rdfs";

}
