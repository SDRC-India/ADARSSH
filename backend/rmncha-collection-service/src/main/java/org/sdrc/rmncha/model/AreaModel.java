package org.sdrc.rmncha.model;

import lombok.Data;

@Data
public class AreaModel {
	private Integer areaId;

	private String areaName;

	private int parentAreaId;

	private String areaLevel;

	private String areaCode;
}
