package org.sdrc.rmnchadashboard.model;

import lombok.Data;

@Data
public class CmsDataRequestModel {

	private long contentId;
	
	private String cmsData;
	
	private int index;
	
	private boolean update;
}