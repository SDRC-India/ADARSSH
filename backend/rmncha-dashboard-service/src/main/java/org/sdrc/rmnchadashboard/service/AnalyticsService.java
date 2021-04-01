/**
 * 
 */
package org.sdrc.rmnchadashboard.service;

import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.model.AnalyticsColModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Harsh Pratyush
 */
public interface AnalyticsService {

	/**
	 * This method will upload the file in server
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	String store(MultipartFile file) throws Exception;

	/**
	 * This method will return the list of columns
	 * 
	 * @param filePath
	 * @return List<AnalyticsColModel>
	 * @throws Exception
	 */
	List<AnalyticsColModel> getColumns(String filePath) throws Exception;

	/**
	 * This method will connect with R server and returns the path of genereated
	 * csv for a single indicator discriptive analytics
	 * 
	 * @param selectionData
	 * @return
	 * @throws Exception
	 */
	ResponseModel singleDiscriptiveStatics(Map<String, Object> selectionData)
			throws Exception;

	/**
	 * This method will connect with R server and returns the path of genereated
	 * csv for a all indicator discriptive analytics
	 * 
	 * @param selectionData
	 * @return
	 * @throws Exception
	 */
	ResponseModel multipleDiscriptiveStatics(Map<String, Object> selectionData)
			throws Exception;

	/**
	 * This method will return the file path after finding the outlier from the data set
	 * @param selectionData
	 * @return
	 * @throws Exception
	 */
	ResponseModel outlierDataMatrix(Map<String, Object> selectionData)
			throws Exception;
	
	
	/**
	 * This method wil return the file path after finding the co-relation between two variable or one to many variable 
	 * @param selectionData
	 * @return
	 * @throws Exception
	 */
	ResponseModel independentLinerRegration(Map<String, Object> selectionData) throws Exception;

	/**
	 * This method will treat missing value and return the genrated file path
	 * @param filePath
	 * @return
	 */
	ResponseModel treatMissingValues(Map<String, Object> selectionData) throws Exception;
	
//	
//	/**
//	 * This will check wether deleting of file started or not
//	 * @param path
//	 * @return
//	 */
//	public boolean onFileAccess(Path path);
}
