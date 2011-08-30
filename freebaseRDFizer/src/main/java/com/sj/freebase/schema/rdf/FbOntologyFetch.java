package com.sj.freebase.schema.rdf;

import java.util.List;
import java.util.Map;

public interface FbOntologyFetch {
    public void getDomain(List<CharSequence> domainList) throws Exception;


    public void getType(final String domain, final Object env,
        List<CharSequence> TypeList) throws Exception;


    public void getProperties(final String domain, final String type,
        final Object envelope,
        List<Map<CharSequence, List<CharSequence>>> propertyList)
    throws Exception;


    public Object createEnvelope(String envelopParam, String envelopVal);


    public Object createEnvelope(String envelopParam, boolean envelopVal);

}
