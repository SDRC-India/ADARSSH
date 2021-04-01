package org.sdrc.rmnchadashboard.rabbitMQ;

import java.io.Serializable;
import java.util.Map;

import org.sdrc.rmncha.domain.AreaLevel;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import lombok.Data;

@Data
public class AreaModelEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5183546473334724534L;

	private Integer areaId;

	private String areaName;

	private String areaCode;

	private Integer parentAreaId;

	private Boolean live;

	private AreaLevel areaLevel;

	private Integer facilitytId;
	
	private Integer blockId;

	private Integer districtId;

	private Integer stateId;

	private String createdBy;
	
	private TypeDetail facilityType;
	
	private TypeDetail facilityLevel;
	
	private Map<String, Object> phcChcType;
	
	private Boolean hwc;
	
	private Map<String, Object> formIdTypeDetails;
}
