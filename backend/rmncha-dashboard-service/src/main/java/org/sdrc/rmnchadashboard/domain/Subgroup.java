package org.sdrc.rmnchadashboard.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-subgroup-type",
shards=1,replicas=1, createIndex =false)

public class Subgroup {

	@Id
    private String id;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String subgroupName;

	private Integer slugidsubgroup;
	
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

	public String getSubgroupName() {
		return subgroupName;
	}

	public void setSubgroupName(String name) {
		this.subgroupName = name;
	}

	

	@Override
	public String toString() {
		return "Subgroup [id=" + id + ", subgroupName=" + subgroupName + ", slugidsubgroup=" + slugidsubgroup
				+ ", createdDate=" + createdDate + ", lastModified=" + lastModified + "]";
	}

	public Integer getSlugidsubgroup() {
		return slugidsubgroup;
	}

	public void setSlugidsubgroup(Integer slugidsubgroup) {
		this.slugidsubgroup = slugidsubgroup;
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
