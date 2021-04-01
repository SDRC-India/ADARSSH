package org.sdrc.rmnchadashboard.model;

import java.util.List;

import lombok.Data;

@Data
public class CmsDataModel {


	private String leftmenuName;
	private List<CMSSubMenuDataModel> leftsubmenu;
	private List<CmsQuestionModel> questions;
	
}