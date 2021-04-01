package org.sdrc.rmnchadashboard.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-source-type",
shards=1,replicas=1, createIndex =false)

public class Source {
	
	@Id
    private String id;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String sourceName;

	private Integer slugidsource;
	
	@Field(type=FieldType.Date)
	private Date createdDate;
	
	@Field(type=FieldType.Date)
	private Date lastModified;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String name) {
		this.sourceName = name;
	}

	@Override
	public String toString() {
		return "Source [id=" + id + ", name=" + sourceName + "]";
	}

	public Integer getSlugidsource() {
		return slugidsource;
	}

	public void setSlugidsource(Integer slugidsource) {
		this.slugidsource = slugidsource;
	}

	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	
	
}
