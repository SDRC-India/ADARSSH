package org.sdrc.rmnchadashboard.model;

import java.util.List;

import lombok.Data;

@Data
public class CmsQuestionModel implements Cloneable {

	
	private Boolean allChecked = false;
	private String columnName;
	private String controlType;
	private String[] dependentCondition;
	private String[] fileExtension;
	private String fileExtensionValidationMessage;
	private Double fileSize;
	private Object[] fileValues;
	private int key;
	private String label;
	private String maxDate;
	private int maxLength;
	private int max;
	private String minDate;
	private int minLength;
	private Boolean multiple = false;
	private List<OptionModel> options;
	private Object optionsParentColumn;
	private String pattern;
	private Boolean required = false;
	private Boolean selectAllOption = false;
	private String type;
	private boolean disabled = false;
	private boolean triggable = false;
	private String[] parentColumns;
	private List<List<CmsQuestionModel>> childQuestionModels;
	private String groupParentId;
	private String indexNumberTrack;
	private String serialNumb;
	private Object[] deletedFileValue;
	private String siNo;
	private String currentDate;
	private String placeHolder;
	private boolean removable =true; 
	private boolean infoAvailable=false;
	private String infoMessage;
	private Object value;
	private String headerName;

}
