package org.sdrc.rmnchadashboard.web.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 */

@Data
@ToString
public class ValueObject {

	private Integer formId;
	private List<String> submissionList;
	private String message;
	private Boolean isRejected;
	private String key;
	private Object value;
	private String description;
	private String groupName;
	private String shortNmae;
	private Boolean isSelected;
	private Integer id;
	private String keyValue;
	private Integer count;
	
	Map<String, List<String>> chartMap;
	Map<String, List<String>> legendsMap;
	
}
