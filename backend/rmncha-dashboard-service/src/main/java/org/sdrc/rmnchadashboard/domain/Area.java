package org.sdrc.rmnchadashboard.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "rmncha-index",type="rmncha-area-type",
shards=1,replicas=1, createIndex =false)

public class Area {

	@Id
    private String id;

	private Integer slugidarea;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String areaname;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String code;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String ccode;
	
	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String parentAreaCode;
	
	@Field(type=FieldType.Nested)
	private List<AreaLevel> areaLevel;


	@Field(type=FieldType.Nested)
	private AreaLevel actAreaLevel;
	
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

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String name) {
		this.areaname = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCcode() {
		return ccode;
	}

	public void setCcode(String ccode) {
		this.ccode = ccode;
	}

	

	public List<AreaLevel> getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(List<AreaLevel> areaLevel) {
		this.areaLevel = areaLevel;
	}

	public String getParentAreaCode() {
		return parentAreaCode;
	}

	public void setParentAreaCode(String parentAreaCode) {
		this.parentAreaCode = parentAreaCode;
	}

	public AreaLevel getActAreaLevel() {
		return actAreaLevel;
	}

	public void setActAreaLevel(AreaLevel actAreaLevel) {
		this.actAreaLevel = actAreaLevel;
	}
	
	


	

	public Integer getSlugidarea() {
		return slugidarea;
	}

	public void setSlugidarea(Integer slugidarea) {
		this.slugidarea = slugidarea;
	}

	@Override
	public String toString() {
		return "Area [id=" + id + ", areaname=" + areaname + ", code=" + code + ", ccode=" + ccode + ", parentAreaCode="
				+ parentAreaCode + ", areaLevel=" + areaLevel + ", actAreaLevel=" + actAreaLevel + "]";
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
