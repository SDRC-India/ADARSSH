/**
 * 
 */
package org.sdrc.rmnchadashboard.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Data;
import org.sdrc.rmnchadashboard.jpadomain.DataSyncMaster;
import org.sdrc.rmnchadashboard.model.DataSyncStatusEnum;
import org.sdrc.rmnchadashboard.model.MasterDataModel;
import org.sdrc.rmnchadashboard.model.MasterDataSyncModel;
import org.sdrc.rmnchadashboard.model.RequestModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Harsh Pratyush
 * @email harsh@sdrc.co.in
 *
 */
public interface MobileSyncService {

	/**
	 * @author Harsh Pratyush
	 * @email harsh@sdrc.co.in This method will validate the excel template with
	 *        data and return the validated excel file path with the response type.
	 *        If excel file has invalid data then all the rows with wrong data will
	 *        be highlighted in red color
	 * @param excelPath
	 * @return ResponseModel
	 */
	public ResponseModel validateData(String excelPath);

	/**
	 * @author Harsh Pratyush
	 * @email harsh@sdrc.co.in This file will except the validated file path
	 *        validated through {@link MobileSyncService.validateData(String
	 *        excelPath);} This method will import the new data and update old data
	 *        if any. Then it will calculate rank and trend of new updated data.
	 * 
	 * @param validatedExcelPath
	 * @return
	 */
	public ResponseModel importNewData(RequestModel requestModel);

	/**
	 * @author Sourav Nath
	 * This method will create a blank template for data update.
	 * @return
	 */
	public File generateExcelTemplate();
	
	
	/**
	 * @author Harsh Pratyush
	 * @email harsh@sdrc.co.in
	 * This method will store the file for the template
	 * @param file
	 * @return
	 * @throws Exception
	 */
	String store(MultipartFile file) throws Exception;
	
	/**
	 * This method will sync the master data from mobile.
	 * @author Ratikanta Pradhan(ratikanta@sdrc.co.in)
	 * @param masterDataSyncModel
	 * @return {@link MasterDataModel}
	 */
	public MasterDataModel getMasterData(MasterDataSyncModel masterDataSyncModel);

	/**
	 * @author Harsh Pratyush
	 * @email harsh@sdrc.co.in
	 * @param lastModifiedData
	 * @return
	 */
	public boolean checkDataSyncData(String lastModifiedData);

	/**
	 * This method will create the data file in JSON and zip it
	 * @param fcmToken 
	 * @return
	 * @throws IOException
	 */
	boolean createJsonAndZip(String fcmToken) throws IOException;
	
	/**
	 * This method will update  the sync status
	 * @return
	 * @throws IOException
	 */
	DataSyncMaster updateDataSyncMaster(DataSyncMaster dataSyncMaster);
	
	
	/**
	 * This method will return the syncStatus
	 * @return
	 * @throws IOException
	 */
	List<DataSyncMaster> getSyncStatus(List<DataSyncStatusEnum> datasyncStatus);
	
	
	/**
	 * @author Harsh Pratyush (harsh@sdrc.co.in) 
	 * @param responseModel
	 */
	void sendNotfication(ResponseModel responseModel,String token);
	
	
	/**
	 * 
	 * @param page
	 * @param indicatorSlugIds
	 * @param areaCodes
	 * @param searchType
	 * @param request 
	 * @return
	 */
	List<Data> getCustomViewData(Integer page,
		 List<Integer> indicatorSlugIds,List<String> areaCodes,
			 Integer searchType, Pageable request);


}
