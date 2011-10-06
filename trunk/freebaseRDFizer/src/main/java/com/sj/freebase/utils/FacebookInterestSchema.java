package com.sj.freebase.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FacebookInterestSchema {
    private Logger log = Logger.getLogger(this.getClass());
    private List<String> interestDomains = new ArrayList<String>();
    private Document document = null;
    private Map<String, List<String>> schemaMap = Collections.emptyMap();
    private String [] defaultDomains = {"organization",
        "local_business", "product", "celebrity",
        "entertainment"};

    private static final String CATEGORY_ELEMENT_ID = "category";
    private static final String OPTION_ELEMENT_ID = "option";


    public FacebookInterestSchema (String uri)
    throws MalformedURLException, IOException {
        this(uri, null);
    }


    public FacebookInterestSchema (String uri, List<String> interestDomains)
    throws MalformedURLException, IOException {
        this.document = Jsoup.parse(new URL(uri), 10000);

        if (interestDomains != null) {
            this.interestDomains.addAll(interestDomains);
            getInterestSchema(this.interestDomains);
        } else {
            this.interestDomains.addAll(Arrays.asList(defaultDomains));
            getInterestSchema(this.interestDomains);
        }
    }


    public void setInterestDomains(List<String> interestDomains) {
        if (interestDomains != null) {
            this.interestDomains.addAll(interestDomains);
            getInterestSchema(this.interestDomains);
        }
    }


    public List<String> getInterestDomains() {
        return interestDomains;
    }


    public Map<String, List<String>> getInterestSchemaMap() {
        if (this.schemaMap == null) {
            schemaMap = Collections.emptyMap();
        }

        return this.schemaMap;
    }


    public List<String> getCategories(String domain) {
        List<String> categories = this.schemaMap.get(domain);

        if (categories == null) {
            categories = Collections.emptyList();
        }

        return categories;
    }


    private void getInterestSchema(List<String> domains) {
        for (String domain : domains) {
            Element mainForm = this.document.getElementById(domain + "_form");
            if (mainForm != null) {
                Element category = mainForm.getElementById(CATEGORY_ELEMENT_ID);
                if (category != null) {
                    Elements subCategories =
                        category.getElementsByTag(OPTION_ELEMENT_ID);

                    ListIterator<Element> categoriesIterator =
                        subCategories.listIterator();

                    if (this.schemaMap.isEmpty() || this.schemaMap == null) {
                        this.schemaMap = new HashMap<String, List<String>>();
                    }

                    List<String> categories = new ArrayList<String>();
                    while (categoriesIterator.hasNext()) {
                        Element localBusinessCategory =
                            categoriesIterator.next();

                        String fbCategory =
                            localBusinessCategory.data().replaceAll("\\s", "_");
                        if (!fbCategory.equals("Choose_a_category")) {
                            categories.add(fbCategory);
                        }
                    }

                    this.schemaMap.put(domain, categories);
                } else {
                    log.debug("Failed to get categories for domain : " +
                        domain + ", category id : " + category.id());
                }
            } else {
                log.debug("Failed to get categories for domain : " + domain);
            }
        }
    }


    public static void main(String [] args)
    throws MalformedURLException, IOException {

        // List<String> failList = new ArrayList<String>();
        //
        // failList.add("psuedoList1");
        // failList.add("psuedoList2");
        // failList.add("psuedoList3");

        FacebookInterestSchema schema =
            new FacebookInterestSchema(
                "http://www.facebook.com/pages/create.php");

        List<String> domains = schema.getInterestDomains();
        for (String domain : domains) {
            List<String> categories = schema.getCategories(domain);

            System.out.println("Domain -- " + domain);
            System.out.println("Categories -- " + categories);
        }
    }
}
