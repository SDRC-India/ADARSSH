package org.sdrc.rmnchadashboard.webcontroller;

import java.util.ArrayList;
import java.util.List;

import org.sdrc.rmnchadashboard.domain.Data;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.sdrc.rmnchadashboard.repository.AreaRepository;
import org.sdrc.rmnchadashboard.repository.DataDAL;
import org.sdrc.rmnchadashboard.repository.DataRepository;
import org.sdrc.rmnchadashboard.repository.IndicatorDAL;
import org.sdrc.rmnchadashboard.repository.IndicatorRepository;
import org.sdrc.rmnchadashboard.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {

	
//	@Autowired
//	private AreaLevelRepository areaLevelRepository;
//
//	
//	@Autowired
//	private SourceRepository sourceRepository;
//
//	@Autowired
//	private SubgroupRepository subgroupRepository;
//
//	@Autowired
//	private SectorRepository sectorRepository;
//
//	@Autowired
//	private UnitRepository unitRepository;

	@Autowired
	private IndicatorRepository indicatorRepository;

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	DataRepository dataRepository;

	@Autowired
	IndicatorDAL indicatorDAL;
	
	@Autowired
	DataDAL dataDAL;
	
	@Autowired
	private ConfigurationService configurationService; 
	
	@CrossOrigin
	@GetMapping("/data/findDataForIndicator")
	public List<Data> getInitalDataForIndicator(@RequestParam String iName) {
		List<Data> datas = new ArrayList<>();
		Iterable<Data> itr = dataDAL.getInitalDataForIndicator(iName);
		itr.forEach(datas::add);
		return datas;
	}
	
	@CrossOrigin
	@GetMapping("/indicators/findAll")
	public List<Indicator> getIndicators() {
		List<Indicator> datas = new ArrayList<>();
		Iterable<Indicator> itr = indicatorRepository.findAll();
		itr.forEach(datas::add);
		return datas;
	}
	
	@GetMapping("/createJSONFile")
	public String createJSONFiles() {
		try
		{
		return configurationService.createJSON();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping("/hello")
	public String hello() {
			return "hello";
	}

}
