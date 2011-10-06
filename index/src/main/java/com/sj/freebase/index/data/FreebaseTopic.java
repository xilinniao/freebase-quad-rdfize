package com.sj.freebase.index.data;

import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.sj.util.JsonUtils;

public class FreebaseTopic {

    private String mid = "";
    private FreebaseBoost name;
    private String en_id = "";
    private List<FreebaseBoost> alias = Collections.emptyList();
    private List<String> twitter_ids  = Collections.emptyList();
    private List<String> myspace_ids = Collections.emptyList();
    private List<String> wiki_ids = Collections.emptyList();
    private String description = "";
    private String main_type = "";
    private List<String> types = Collections.emptyList();
    
    public void setMid(String mid) {
        this.mid = mid;
    }
    
    public String getMid() {
        if(mid == null) {
            mid = "";
        }
        return mid;
    }

    public void setName(FreebaseBoost name) {
        this.name = name;
    }

    public FreebaseBoost getName() {
        if (this.name == null) {
            this.name.setBoost(1);
            this.name.setValue("");
        }
        return name;
    }

    public void setAlias(List<FreebaseBoost> alias) {
        this.alias = alias;
    }

    public List<FreebaseBoost> getAlias() {
        if (this.alias == null) {
            this.alias = Collections.emptyList();
        }
        return alias;
    }

    public void setTwitter_ids(List<String> twitter_ids) {
        this.twitter_ids = twitter_ids;
    }

    public List<String> getTwitter_ids() {
        if (this.twitter_ids == null) {
            this.twitter_ids = Collections.emptyList();
        }
        return twitter_ids;
    }

    public void setMyspace_ids(List<String> myspace_ids) {
        this.myspace_ids = myspace_ids;
    }

    public List<String> getMyspace_ids() {
        if (this.myspace_ids == null) {
            this.myspace_ids = Collections.emptyList();
        }
        return myspace_ids;
    }

    public void setWiki_ids(List<String> wiki_ids) {
        this.wiki_ids = wiki_ids;
    }

    public List<String> getWiki_ids() {
        if (this.wiki_ids == null) {
            this.wiki_ids = Collections.emptyList();
        }
        return wiki_ids;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = "";
        }
        return description;
    }

    public void setMain_type(String main_type) {
        this.main_type = main_type;
    }

    public String getMain_type() {
        if (this.main_type == null) {
            this.main_type = "";
        }
        return main_type;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getTypes() {
        if (this.types == null) {
            this.types = Collections.emptyList();
        }
        return types;
    }    
    
    public void setEn_id(String en_id) {
        this.en_id = en_id;
    }

    public String getEn_id() {
        if (en_id == null) {
            en_id = "";
        }
        
        return en_id;
    }
    
    public String toString(Gson gson) {
        return JsonUtils.toJson(gson, this);
    }
}
