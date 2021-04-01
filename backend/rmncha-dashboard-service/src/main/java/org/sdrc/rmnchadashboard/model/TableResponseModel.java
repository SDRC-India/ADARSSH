package org.sdrc.rmnchadashboard.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TableResponseModel {

	private List<String>  tableColumns;
	
	private List<Map<String,Object>> tableData;
}