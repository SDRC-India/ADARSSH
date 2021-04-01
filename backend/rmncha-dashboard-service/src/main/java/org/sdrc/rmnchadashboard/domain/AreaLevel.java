package org.sdrc.rmnchadashboard.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-arealevel-type",
shards=1,replicas=1, createIndex =false)

public class AreaLevel {

	@Id
	private String id;
	
	private Integer slugidarealevel;

	@Field(type = FieldType.String, searchAnalyzer = "keyword", analyzer = "keyword", index = FieldIndex.not_analyzed)
	private String areaLevelName;

	private Integer level;

	private Boolean isStateAvailable;

	private Boolean isDistrictAvailable;

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

	public String getAreaLevelName() {
		return areaLevelName;
	}

	public void setAreaLevelName(String name) {
		this.areaLevelName = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsStateAvailable() {
		return isStateAvailable;
	}

	public void setIsStateAvailable(Boolean isStateAvailable) {
		this.isStateAvailable = isStateAvailable;
	}

	public Boolean getIsDistrictAvailable() {
		return isDistrictAvailable;
	}

	public void setIsDistrictAvailable(Boolean isDistrictAvailable) {
		this.isDistrictAvailable = isDistrictAvailable;
	}

	

	public Integer getSlugidarealevel() {
		return slugidarealevel;
	}

	public void setSlugidarealevel(Integer slugidarealevel) {
		this.slugidarealevel = slugidarealevel;
	}

	@Override
	public String toString() {
		return "AreaLevel [id=" + id + ", areaLevelName=" + areaLevelName + ", level=" + level + ", isStateAvailable="
				+ isStateAvailable + ", isDistrictAvailable=" + isDistrictAvailable + "]";
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
