package com.sj.index.create;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.sj.freebase.index.data.FreebaseBoost;
import com.sj.freebase.index.data.FreebaseTopic;

public final class FilebasedTopicIndexer extends TopicIndexer {
    private BufferedReader reader;
    private String currentLine = null;
    private Logger log = Logger.getLogger(this.getClass());


    // Constructor
    public FilebasedTopicIndexer (BufferedReader reader) {
        this.reader = reader;
    }


    @Override
    public boolean hasNext() {
        try {
            currentLine = reader.readLine();
        } catch (IOException e) {
            log.debug("Failed to fetch next element, exception : " +
                e.toString());
        }

        boolean hasNext = true;
        if (currentLine == null) {
            hasNext = false;
        }

        return hasNext;
    }


    /*
     * A tab-separated file containing basic identifying data about every topic
     * in Freebase. The columns are: 1. mid 
     * 2. English display name 
     * 3. Freebase /en keys (comma-separated) 
     * 4. numeric English Wikipedia keys (comma-separated) 
     * 5. Freebase types (comma-separated) from the commons(not base types) 
     * 6. A short text description from Wikipedia (when
     * available).
     */
    @Override
    public FreebaseTopic next() {

        FreebaseTopic topic = new FreebaseTopic();

        String [] topicString = this.currentLine.split("\t");

        String mid  = topicString[0];
        if (mid == null || mid.equals("\\N")) {
            return null;
        }
        
        String displayName = topicString[1];
        if(displayName.equals("\\N")) {
            displayName = "";
        }
        FreebaseBoost nameBoost = new FreebaseBoost(1, displayName);
        
        String enIdString = topicString[2];
        
        String wikiIdString = topicString[3];
        List<String> wikiIds = Collections.emptyList();
        if(!wikiIdString.equals("\\N")) {
            wikiIds = Arrays.asList(wikiIdString.split(","));
        }
        
        String freebaseTypeString = topicString[4];
        List<String> freebaseTypesList = Collections.emptyList();
        if(!freebaseTypeString.equals("\\N")) {
            freebaseTypesList = Arrays.asList(freebaseTypeString.split(","));
        }
        
        String desc = topicString[5];
        
        topic.setMid(mid);
        topic.setName(nameBoost);
        topic.setEn_id(enIdString);
        topic.setWiki_ids(wikiIds);
        topic.setTypes(freebaseTypesList);
        topic.setDescription(desc);
        
        return topic;
    }


    @Override
    public void remove() {

    }


    @Override
    public void close() throws IOException {
        this.reader.close();
    }


    private List<FreebaseBoost> getBoosts(List<String> strList) {
        List<FreebaseBoost> boosts =
            new ArrayList<FreebaseBoost>(strList.size());
        for (String str : strList) {
            FreebaseBoost boost = new FreebaseBoost(1, str);
            boosts.add(boost);
        }
        
        return boosts;
    }
}
