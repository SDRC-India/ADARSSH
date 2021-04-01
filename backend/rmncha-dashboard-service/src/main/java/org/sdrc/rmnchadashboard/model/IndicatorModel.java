package org.sdrc.rmnchadashboard.model;

import java.util.List;

import org.sdrc.rmnchadashboard.domain.Sector;

public class IndicatorModel {

	private String name;

	private List<Sector> sc;

	private Sector recSector;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Sector> getSc() {
		return sc;
	}

	public void setSc(List<Sector> sc) {
		this.sc = sc;
	}

	public Sector getRecSector() {
		return recSector;
	}

	public void setRecSector(Sector recSector) {
		this.recSector = recSector;
	}

}
