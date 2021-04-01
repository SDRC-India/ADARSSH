package org.sdrc.rmnchadashboard.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-unit-type", shards=1, replicas = 1, createIndex =false)
public class Unit {

	@Id
	private String id;

	@Field(type = FieldType.String, searchAnalyzer = "keyword", analyzer = "keyword", index = FieldIndex.not_analyzed)
	private String unitName;

	private Integer slugidunit;

	@Field(type = FieldType.Date)
	private Date createdDate;

	@Field(type = FieldType.Date)
	private Date lastModified;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", unitName=" + unitName + "]";
	}

	public Integer getSlugidunit() {
		return slugidunit;
	}

	public void setSlugidunit(Integer slugidunit) {
		this.slugidunit = slugidunit;
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
