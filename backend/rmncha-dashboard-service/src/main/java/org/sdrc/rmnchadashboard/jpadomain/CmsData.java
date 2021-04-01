package org.sdrc.rmnchadashboard.jpadomain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import lombok.Data;

@Entity
@TypeDefs({ @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class), })
@Table(name = "cms_data")
@Data
public class CmsData implements Serializable {

	private static final long serialVersionUID = 5401149371348134226L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cms_data_id")
	private Long id;

	@Column(length = 200, nullable = false)
	private String mainMenu;

	@Column(length = 1500)
	private String subMenu;

	@Column(length = 200, nullable = false)
	private String valueShowType;

	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode values;

	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false)
	private JsonNode questions;
	
	private String tableHeader;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	
	@Column(nullable=true)
	private Integer maxvalues;
	
	
	@Column(nullable=true)
	private Integer cmsOrder;
	
	@Column(nullable=true)
	private Boolean isLive;
	
	
	@Column( columnDefinition = "boolean DEFAULT true")
	private boolean removable = true;

	@CreatedBy
	@JsonIgnore
	private String createdBy;

	@CreatedBy
	@JsonIgnore
	private String updatedBy;

	@CreationTimestamp
	@Temporal(TemporalType.DATE)
	private Date createdDate;

}