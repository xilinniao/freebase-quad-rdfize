package com.sj.index.create;

import java.util.Iterator;

import com.sj.freebase.index.data.FreebaseTopic;

public abstract class TopicIndexer implements Iterator<FreebaseTopic> {
    public void close() throws Exception {
        
    }
}
