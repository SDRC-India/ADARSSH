/**
 * 
 */
package org.sdrc.rmnchadashboard.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.sdrc.rmnchadashboard.model.AnalyticsColModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Harsh Pratyush
 *
 */

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final Path rootLocation = Paths.get("/rmncha-data");
	private final Path downloadLocation = Paths.get("/out");
		
	@Value("${colFile}")
    String colFile;
	
	@Value("${singleColNumericStatstics}")
    String singleColNumericStatstics;
	
	@Value("${singleColCatStatstics}")
    String singleColCatStatstics;
	
	
	@Value("${multiColNumericStatstics}")
    String multiColNumericStatstics;
	
	@Value("${multiColCatStatstics}")
    String multiColCatStatstics;
	
	
	@Value("${outlier}")
    String outlier;
	
	@Value("${missingValue}")
    String missingValue;
	
	@Value("${regression}")
    String regression;
	

//	private Map<Path, ScheduledFuture> futures = new HashMap<>();
//
//	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//
//	private static final TimeUnit UNITS = TimeUnit.HOURS; 
//	
//	private static final int fileLifeSpan=2;

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sdrc.rmnchadashboard.service.AnalyticsService#store(org.springframework
	 * . web.multipart.MultipartFile)
	 */
	@Override
	public String store(MultipartFile file) throws Exception {
		try {
			
			
			

			
			String fileName = file.getOriginalFilename().trim()
					.replaceAll(".csv", "")
					+ new Date().getTime() + ".csv";
			Files.copy(file.getInputStream(),
					this.rootLocation.resolve(fileName));
			
//			Reader reader = Files.newBufferedReader(this.rootLocation.resolve(fileName));
//            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
//                    .withIgnoreHeaderCase()
//                    .withTrim());
//            
//            for (CSVRecord csvRecord : csvParser) {
//                System.out.println("Record No - " + csvRecord.getRecordNumber());
//
//            }
            
//			
//			Path path=this.rootLocation.resolve(fileName);
//			this.scheduleForDeletion(path, fileLifeSpan);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
			throw new Exception("Failed to upload");

		}
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#getColumns(String)
	 */
	@Override
	public List<AnalyticsColModel> getColumns(String filePath) throws Exception {
		    
		String file = this.rootLocation.resolve(filePath).toUri().toString();
		file = file.replaceAll("file:///", "");
		file = file.replaceAll("%20", " ");
		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}

		REXP res;
		System.out.println(file);
		String scriptFile=ResourceUtils.getFile(colFile).getAbsolutePath().replaceAll("\\\\", "/");
		re.eval("source('" + scriptFile + "')");
		res = re.eval("set_col('" + file + "')");

		RVector rvector = res.asVector();
		 re.end();
		Set<String> charType = new HashSet<String>(Arrays.asList(rvector.at(
				"char").asStringArray()));
		List<String> total = (Arrays
				.asList(rvector.at("total").asStringArray()));

		List<AnalyticsColModel> cols = new ArrayList<AnalyticsColModel>();
		total.forEach(col -> {
			AnalyticsColModel column = new AnalyticsColModel();
			column.setId(total.indexOf(col) + 1);
			column.setName(col);
			column.setColType(charType.contains(col) ? 2 : 1);
			cols.add(column);
		});

		return cols;
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#singleDiscriptiveStatics(Map)
	 */
	@Override
	public ResponseModel singleDiscriptiveStatics(
			Map<String, Object> selectionData) throws Exception {

		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}

		REXP res;
		int analysisType = Integer.parseInt(selectionData.get("analysisType")
				.toString());
		String file = this.rootLocation
				.resolve(selectionData.get("fileName").toString()).toUri()
				.toString();
		file = file.replaceAll("file:///", "");

		String scriptFile=null;
		switch (analysisType) {

		case 1:
			 scriptFile=ResourceUtils.getFile(singleColNumericStatstics).getAbsolutePath().replaceAll("\\\\", "/");
			re.eval("source('" + scriptFile + "')");
			break;

		case 2:
			 scriptFile=ResourceUtils.getFile(singleColCatStatstics).getAbsolutePath().replaceAll("\\\\", "/");
			re.eval("source('" + scriptFile + "')");
			break;

		}

		res = re.eval("begin1('"
				+ selectionData.get("indicatorValue").toString() + "','"+file+"')");
		
		String fileName = res.toString();
		 re.end();
		if (!fileName.contains("Please")) {
			fileName = fileName.substring(9);
			fileName = fileName.substring(0, fileName.length() - 2);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setFileName(fileName);
			responseModel.setFilePath(this.downloadLocation.resolve(fileName)
					.toAbsolutePath().toString());
			responseModel.setOutputFilePath(file);
//			Path path=this.downloadLocation.resolve(fileName);
//			this.scheduleForDeletion(path, fileLifeSpan);
			return responseModel;
		}

		else {
			return null;
		}
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#multipleDiscriptiveStatics(Map)
	 */
	@Override
	public ResponseModel multipleDiscriptiveStatics(
			Map<String, Object> selectionData) throws Exception {
		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}

		String file = this.rootLocation
				.resolve(selectionData.get("fileName").toString()).toUri()
				.toString();
		file = file.replaceAll("file:///", "");

		REXP res;

		int analysisType = Integer.parseInt(selectionData.get("analysisType")
				.toString());

		String scriptFile=null;
		switch (analysisType) {

		case 1:
			 scriptFile=ResourceUtils.getFile(multiColNumericStatstics).getAbsolutePath().replaceAll("\\\\", "/");
				re.eval("source('" + scriptFile + "')");
			break;

		case 2:
			 scriptFile=ResourceUtils.getFile(multiColCatStatstics).getAbsolutePath().replaceAll("\\\\", "/");
				re.eval("source('" + scriptFile + "')");
			break;

		}

		List<String> indicatorValues=(List<String>) selectionData.get("indicatorValue");
		String indicators=null;
		for(String indicatorValue:indicatorValues)
		{
			if(indicators==null)
			{
				indicators=indicatorValue;	
			}
			else
			{
				indicators+=","+indicatorValue;	
			}
		}
		
		res = re.eval("begin1('"
				+ indicators+ "','"+file+"')");
		String fileName = res.toString();
		 re.end();
		if (!fileName.contains("Please")) {
			fileName = fileName.substring(9);
			fileName = fileName.substring(0, fileName.length() - 2);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setFileName(fileName);
			responseModel.setFilePath(this.downloadLocation.resolve(fileName)
					.toAbsolutePath().toString());
//			Path path=this.downloadLocation.resolve(fileName);
//			this.scheduleForDeletion(path, fileLifeSpan);
			responseModel.setOutputFilePath(file);
			return responseModel;
		}

		else {
			return null;
		}
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#outlierDataMatrix(Map)
	 */
	@Override
	public ResponseModel outlierDataMatrix(Map<String, Object> selectionData)
			throws Exception {
		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}
		String file = this.rootLocation
				.resolve(selectionData.get("fileName").toString()).toUri()
				.toString();
		file = file.replaceAll("file:///", "");
		
		List<String> indicatorValues=(List<String>) selectionData.get("indicators");
		String indicators=null;
		for(String indicatorValue:indicatorValues)
		{
			if(indicators==null)
			{
				indicators=indicatorValue;	
			}
			else
			{
				indicators+=","+indicatorValue;	
			}
		}
		
		REXP res;
		String scriptFile=ResourceUtils.getFile(outlier).getAbsolutePath().replaceAll("\\\\", "/");
		re.eval("source('" + scriptFile + "')");
		res = re.eval("begin1('"
				+ indicators+ "','"+file+"')");
//		res = re.eval("begin1('" + file + "')");

		String fileName = res.toString();
		 re.end();
		if (!fileName.contains("Please")) {
			fileName = fileName.substring(9);
			fileName = fileName.substring(0, fileName.length() - 2);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setFileName(fileName);
			responseModel.setFilePath(this.downloadLocation.resolve(fileName)
					.toAbsolutePath().toString());
//			Path path=this.downloadLocation.resolve(fileName);
//			this.scheduleForDeletion(path, fileLifeSpan);
			responseModel.setOutputFilePath(file);
			return responseModel;
		}

		else {
			return null;
		}
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#independentLinerRegration(Map)
	 */
	@Override
	public ResponseModel independentLinerRegration(
			Map<String, Object> selectionData) throws Exception {
		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}

		String file = this.rootLocation
				.resolve(selectionData.get("fileName").toString()).toUri()
				.toString();
		file = file.replaceAll("file:///", "");
		
		List<String> indicatorValues=(List<String>) selectionData.get("secondaryIndicator");
		String indicators=null;
		for(String indicatorValue:indicatorValues)
		{
			if(indicators==null)
			{
				indicators=indicatorValue;	
			}
			else
			{
				indicators+=","+indicatorValue;	
			}
		}
		
		REXP res;

		String scriptFile=ResourceUtils.getFile(regression).getAbsolutePath().replaceAll("\\\\", "/");
		re.eval("source('" + scriptFile + "')");

			res = re.eval("begin1('"
					+ selectionData.get("indicator").toString()+ "','"+indicators+  "','"+file+"')");
		
		String fileName = res.toString();
		 re.end();
		if (!fileName.contains("Please")) {
			fileName = fileName.substring(9);
			fileName = fileName.substring(0, fileName.length() - 2);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setFileName(fileName);
			responseModel.setFilePath(this.downloadLocation.resolve(fileName)
					.toAbsolutePath().toString());
			responseModel.setOutputFilePath(file);
			return responseModel;
		}

		else {
			return null;
		}
	}

	/**
	 * @see org.sdrc.rmnchadashboard.service.AnalyticsService#treatMissingValues(String)
	 */
	@Override
	public ResponseModel treatMissingValues(Map<String, Object> selectionData) throws Exception {
		String newargs1[] = { "--no-save" };

		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(newargs1, false, null);
		}

		String file = this.rootLocation.resolve(selectionData.get("fileName").toString()).toUri().toString();
		file = file.replaceAll("file:///", "");

		List<String> indicatorValues=(List<String>) selectionData.get("indicators");
		String indicators=null;
		for(String indicatorValue:indicatorValues)
		{
			if(indicators==null)
			{
				indicators=indicatorValue;	
			}
			else
			{
				indicators+=","+indicatorValue;	
			}
		}
		
		REXP res;
		String scriptFile=ResourceUtils.getFile(missingValue).getAbsolutePath().replaceAll("\\\\", "/");
		re.eval("source('" + scriptFile + "')");

		res = re.eval("begin1('"
				+ indicators+ "','"+file+"')");

		String fileName = res.toString();
		 re.end();
		if (!fileName.contains("Please")) {
			fileName = fileName.substring(9);
			fileName = fileName.substring(0, fileName.length() - 2);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setFileName(fileName);
			responseModel.setFilePath(this.downloadLocation.resolve(fileName)
					.toAbsolutePath().toString());
			responseModel.setOutputFilePath(file);
//			Path path=this.downloadLocation.resolve(fileName);
//			this.scheduleForDeletion(path, fileLifeSpan);
			return responseModel;
		}

		else {
			return null;
		}
	}
	
	
//
//	private void scheduleForDeletion(Path path, long delay) {
//	    ScheduledFuture future = executor.schedule(() -> {
//	        try {
//	            Files.delete(path);
//	        } catch (IOException e) {
//	            log.error("Failed to delete file");
//	        }
//	    }, delay, UNITS);
//
//	    futures.put(path, future);
//	}

//	@Override
//	public boolean onFileAccess(Path path) {
//	    ScheduledFuture future = futures.get(path);
//	    if (future != null) {
//	        boolean result = future.cancel(false);
//	        if (result) {
//	            // reschedule the task
//	            futures.remove(path);
//	            scheduleForDeletion(path, future.getDelay(UNITS));
//	            return true;
//	        } else {
//	        	return false;
//	        }
//	    }
//	    
//	    return true;
//	}

}
