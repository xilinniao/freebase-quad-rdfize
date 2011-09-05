package com.sj.freebase.schema.rdf;

import static com.freebase.json.JSON.a;
import static com.freebase.json.JSON.o;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;
import com.freebase.json.JSON.Type;

public class MQLFreebaseSchemaFetcher implements FbOntologyFetch {

    private Freebase freebase = null;


    public MQLFreebaseSchemaFetcher () {
        freebase = Freebase.getFreebase();
    }


    @Override
    public Object createEnvelope(String envelopParam, String envelopVal) {
        return o(envelopParam, envelopVal);
    }


    @Override
    public Object createEnvelope(String envelopParam, boolean envelopVal) {
        return o(envelopParam, envelopVal);
    }


    private void handleResponseStatus(final JSON response) throws Exception {
        if (response == null) {
            throw new NullPointerException(
                "Handle Resoponse Status : No response to handle");
        }

        if (!response.get(FbSchemaGlobals.CODE_FIELD).string().equals(
            FbSchemaGlobals.CODE_SUCCESS_VALUE)) {
            throw new Exception(response.get(FbSchemaGlobals.CODE_FIELD)
                .string());
        }
    }


    // domain.
    @Override
    public void getDomain(List<CharSequence> domainList) throws Exception {

        if (domainList == null) {
            throw new NullPointerException("domain list cannot be null");
        }

        JSON response = freebase.mqlread(FbSchemaGlobals.DOMAIN_QUERY);
        handleResponseStatus(response);
        JSON domainResponse = response.get("result");
        int size = domainResponse.array().size();

        for (int i = 0; i < size; i++) {
            domainList.add(domainResponse.get(i).get("id").string().replace(
                "/", ""));
        }
    }


    // e.g. :
    // [{"limit":200,"id":null,"domain":{"id":"/type"},"type":"/type/type"}]​
    private JSON executeTypeQuery(String domain, JSON envelope) {
        JSON query =
            a(o("type", "/type/type", "domain", o("id", domain), "id", null,
                "limit", FbSchemaGlobals.DEFAULT_LIMIT));

        // System.out.println(query.toJSONString());
        return freebase.mqlread(query, envelope, null);
    }


    private void fetchType(final JSON response, List<CharSequence> typeList)
    throws Exception {
        if (typeList == null)
            throw new NullPointerException("TypeList cannot be null");

        handleResponseStatus(response);

        if (response == null)
            throw new NullPointerException(
                "FetchType: No response from freebase");
        int size = response.get("result").array().size();
        for (int i = 0; i < size; i++) {
            String type =
                response.get(FbSchemaGlobals.RESULT_FIELD).get(i).get("id")
                    .string();
            typeList.add(type.substring(1, type.length()).replace("/", "."));
        }
    }


    @Override
    public void getType(final String domain, final Object env,
        List<CharSequence> typeList) throws Exception {

        if (typeList == null) {
            throw new NullPointerException("Type List cannot be null");
        }

        if (env == null)
            return;

        if (!(env instanceof JSON))
            throw new Exception("Incompatible types in the parameters");

        JSON envelope = (JSON) env;

        JSON cursor = envelope.get(FbSchemaGlobals.CURSOR_FIELD);
        if (cursor.type().equals(Type.BOOLEAN)) {
            if (cursor.bool() == false) {
                return;
            }

            JSON response = executeTypeQuery(domain, envelope);
            fetchType(response, typeList);
            getType(domain, createEnvelope(FbSchemaGlobals.CURSOR_FIELD,
                response.get(FbSchemaGlobals.CURSOR_FIELD).bool()), typeList);
        } else {
            JSON response = executeTypeQuery(domain, envelope);
            fetchType(response, typeList);
            getType(domain, createEnvelope(FbSchemaGlobals.CURSOR_FIELD,
                response.get(FbSchemaGlobals.CURSOR_FIELD).string()), typeList);
        }
    }


    private JSON executePropertyQuery(String domain, String type, JSON envelope) {
        JSON query =
            a(o("type", "/type/domain", "id", domain, "types", o("id", type,
                "properties", a(o("id", null, "expected_type", a()))), "limit",
                FbSchemaGlobals.DEFAULT_LIMIT));
        return freebase.mqlread(query, envelope, null);
    }


    // List<Map <property,List<exp_type>>>
    private void fetchProperties(final JSON response,
        List<Map<CharSequence, List<CharSequence>>> propList) throws Exception {
        if (propList == null)
            throw new NullPointerException("TypeList cannot be null");

        handleResponseStatus(response);

        if (response == null)
            throw new NullPointerException(
                "FetchType: No response from freebase");
        int size = response.get("result").array().size();
        for (int i = 0; i < size; i++) {
            JSON propRsp =
                response.get("result").get(i).get("types").get("properties");
            int propSize = propRsp.array().size();
            HashMap<CharSequence, List<CharSequence>> propMap =
                new HashMap<CharSequence, List<CharSequence>>();
            for (int j = 0; j < propSize; j++) {
                int expSize =
                    propRsp.get(j).get("expected_type").array().size();
                ArrayList<CharSequence> expTypeList =
                    new ArrayList<CharSequence>();
                for (int k = 0; k < expSize; k++) {
                    String type =
                        propRsp.get(j).get("expected_type").get(k).string();
                    expTypeList.add(type.substring(1, type.length()).replace(
                        "/", "."));
                }
                String prop = propRsp.get(j).get("id").string();
                propMap.put(prop.substring(1, prop.length()).replace("/", "."),
                    expTypeList);
            }
            propList.add(propMap);
        }
    }


    public String formatNames(final String name) {
        String n = name;
        n = n.replace(".", "/");
        n = "/" + n;

        return n;
    }


    @Override
    public void getProperties(String domain, String type, Object env,
        List<Map<CharSequence, List<CharSequence>>> propertyMap)
    throws Exception {
        if (propertyMap == null) {
            throw new NullPointerException("Property Map cannot be null");
        }

        if (env == null)
            return;

        if (!(env instanceof JSON))
            throw new Exception("Incompatible types in the parameters");

        JSON envelope = (JSON) env;

        JSON cursor = envelope.get(FbSchemaGlobals.CURSOR_FIELD);
        if (cursor.type().equals(Type.BOOLEAN)) {
            if (cursor.bool() == false) {
                return;
            }

            JSON response = executePropertyQuery(domain, type, envelope);
            fetchProperties(response, propertyMap);
            getProperties(domain, type, createEnvelope(
                FbSchemaGlobals.CURSOR_FIELD, response.get(
                    FbSchemaGlobals.CURSOR_FIELD).bool()), propertyMap);
        } else {
            JSON response = executePropertyQuery(domain, type, envelope);
            fetchProperties(response, propertyMap);
            getProperties(domain, type, createEnvelope(
                FbSchemaGlobals.CURSOR_FIELD, response.get(
                    FbSchemaGlobals.CURSOR_FIELD).string()), propertyMap);
        }
    }


    public void signout() {
        freebase.sign_out();
    }


    public static void main(String [] args) throws Exception {
        MQLFreebaseSchemaFetcher fetcher = new MQLFreebaseSchemaFetcher();
        int count = 0;
        int typeCount = 0;
        int domainCount = 0;
        ArrayList<CharSequence> domains = new ArrayList<CharSequence>();
        fetcher.getDomain(domains);
        System.out.println(FbSchemaGlobals.DOMAIN_QUERY.toJSONString());
        for (CharSequence domain : domains) {
            System.out.println("=============== Domain: " + domain);
            // ArrayList<CharSequence> sequences = new
            // ArrayList<CharSequence>();
            // fetcher.getType(fetcher.formatNames((String) domain), fetcher
            // .createEnvelope(FbSchemaGlobals.CURSOR_FIELD, true), sequences);
            // for (CharSequence sequence : sequences) {
            // System.out.println("=============== Type: " + sequence);
            // ArrayList<Map<CharSequence, List<CharSequence>>> props =
            // new ArrayList<Map<CharSequence, List<CharSequence>>>();
            // fetcher.getProperties(fetcher.formatNames((String) domain),
            // fetcher.formatNames((String) sequence), fetcher
            // .createEnvelope(FbSchemaGlobals.CURSOR_FIELD, true),
            // props);
            // for (Map<CharSequence, List<CharSequence>> propMap : props) {
            // Iterator<CharSequence> it = propMap.keySet().iterator();
            // while (it.hasNext()) {
            // CharSequence prop = it.next();
            // System.out.println("Property : " + prop +
            // "Expected Type : " + propMap.get(prop));
            // ++count;
            // }
            // }
            // ++typeCount;
            // }
            ++domainCount;
        }

        System.out.println("Total Domain: " + domainCount);
        System.out.println("Total Types : " + typeCount);
        System.out.println("Total Properties: " + count);

        fetcher.signout();
    }
}
