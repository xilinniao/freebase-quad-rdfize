package com.sj.index.create;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TopicIndexerFactory {

    public static TopicIndexer createFilebasedIndexer(File file)
    throws FileNotFoundException {
        return new FilebasedTopicIndexer(new BufferedReader(
            new FileReader(file)));
    }
}
