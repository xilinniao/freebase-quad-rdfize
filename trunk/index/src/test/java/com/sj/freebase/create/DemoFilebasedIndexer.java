package com.sj.freebase.create;

import java.io.File;
import java.io.FileNotFoundException;

import com.google.gson.Gson;
import com.sj.freebase.index.data.FreebaseTopic;
import com.sj.index.create.TopicIndexer;
import com.sj.index.create.TopicIndexerFactory;

public class DemoFilebasedIndexer {

    private static final String simpleTopicDump =
        "/Users/sjagannath/Downloads/freebase-simple-topic-dump.tsv";


    public static void main(String... args) throws FileNotFoundException {
        TopicIndexer indexer =
            TopicIndexerFactory
                .createFilebasedIndexer(new File(simpleTopicDump));

        while (indexer.hasNext()) {
            FreebaseTopic topic = indexer.next();
            System.out.println("Topic -- " + topic.toString(new Gson()));
        }
        
    }
}
