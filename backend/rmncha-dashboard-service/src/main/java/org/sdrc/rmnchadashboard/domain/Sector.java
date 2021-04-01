package org.sdrc.rmnchadashboard.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-sector-type",
shards=1,replicas=1, createIndex =false)

public class Sector {
	
	@Id
    private String id;
	
	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String sectorName;

	private Integer slugidsector;
	
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

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String name) {
		this.sectorName = name;
	}

	@Override
	public String toString() {
		return "Sector [id=" + id + ", name=" + sectorName + "]";
	}

	public Integer getSlugidsector() {
		return slugidsector;
	}

	public void setSlugidsector(Integer slugidsector) {
		this.slugidsector = slugidsector;
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
