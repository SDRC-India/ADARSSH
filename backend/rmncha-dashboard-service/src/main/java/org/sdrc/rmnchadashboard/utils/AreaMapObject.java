package org.sdrc.rmnchadashboard.utils;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AreaMapObject {
	private List<Integer> areaList;
	private Map<Integer, Integer> areaMap;
}
