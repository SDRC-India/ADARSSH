package org.sdrc.rmnchadashboard.model;


import java.util.List;

import lombok.Data;

@Data
public class CMSSubMenuDataModel {

	private long leftmenuId;
	
	private String leftsubmenuName;
	
	private String valueShowType;
	
	private List<CmsQuestionModel> questions;
	
	private TableResponseModel tableData;
}