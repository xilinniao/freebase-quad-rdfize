package com.sj.freebase.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnotherTestUtil {

    public static void removeEntityFromList(List<String> lists) {

        lists.remove(0);
    }


    public static void display(List<String> tLists) {

        for (int i = 0; i < tLists.size(); i++) {
            System.out.println(tLists.get(i));
        }
    }


    public static void main(String [] args)
    throws MalformedURLException, IOException {
        // String t = "Iv\u00e1n Ferreiro";
        //
        // String tDecoded = new String(t.getBytes());
        //
        // System.out.println(t.getBytes());
        // System.out.println(tDecoded);
        //
        Document doc =
            Jsoup.parse(new URL("http://www.facebook.com/pages/create.php"),
                10000);

        //organization_form
        //local_business_form
        //product_form
        //celebrity_form
        //entertainment_form
        Element localBusinessForm = doc.getElementById("organization_form");

        Elements localBusinessCategories =
            localBusinessForm.getElementById("category").getElementsByTag(
                "option");
        
        ListIterator<Element> localBusinessCategoriesIterator = localBusinessCategories.listIterator();
        
        while (localBusinessCategoriesIterator.hasNext()) {
            Element localBusinessCategory = localBusinessCategoriesIterator.next();
            System.out.println(localBusinessCategory.data());
        }
        
        // System.out.println(localBusinessForm.toString());

        // List<String> tLists = new ArrayList<String>();
        //
        // tLists.add("first");
        // tLists.add("second");
        // tLists.add("third");
        // tLists.add("four");
        //
        //
        // System.out.println(tLists.size());
        // display(tLists);
        //
        // removeEntityFromList(tLists);
        // System.out.println(tLists.size());
        // display(tLists);

    }

}
