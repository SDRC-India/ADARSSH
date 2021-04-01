package org.sdrc.rmnchadashboard.model;

import lombok.Data;

@Data
public class OptionModel {

	private int key;
	private String value;
	private int order;
	private boolean isSelected;
	private int parentKey;
	private boolean isLive = true;
}