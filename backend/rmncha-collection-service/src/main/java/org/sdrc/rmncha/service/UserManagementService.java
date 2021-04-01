package org.sdrc.rmncha.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.domain.Organization;
import org.sdrc.rmncha.model.AreaModel;
import org.sdrc.rmncha.model.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;

/**
 * @author Sarita Panigrahi
 * Created Date: 05-02-2018
 *
 */
public interface UserManagementService {

/*	*//**
	 * @param userId
	 * @return
	 *//*
	ResponseEntity<String> approveUser(List<String> userId);
*/
	/**
	 * @param userId
	 * @return
	 */
	ResponseEntity<String> rejectAndApproveUser(List<String> listOfuserId, Boolean isApprove, String reason);

	/**
	 * @return
	 */
	ResponseEntity<Map<String, List<UserModel>>> getAllUser();
	
	
	ResponseEntity<List<TypeDetail>> getTypeDetailsByType(String typeName);

	ResponseEntity<List<Organization>> getAllOrganization();

	ResponseEntity<List<AreaLevel>> getAreaLevels();
	
	
	 ResponseEntity<String> getEmailVerificationCode(String email);
	 
	 ResponseEntity<String> oTPAndEmailAvailibility(String email,Integer varificationCode);
		
	 ResponseEntity<Boolean> usernameAvailablity(String username);
		
	 ResponseEntity<String> getServerDate();
	 
	 ResponseEntity<Boolean> getEmailAvailablity(String email);
	 
	 ResponseEntity<String> uploadFile(MultipartFile multipartfiles);

	 Map<String, List<AreaModel>> getAllAreaList();
	 
	 ResponseEntity<String> addDesignation(String designation);
	 


//	ResponseEntity<List<Authority>> getAccessLayers();
	
	String downLoadBulkTemplate() throws Exception;

	List<UserModel> getUser(Integer roleId,Integer areaId);

	ResponseEntity<Boolean> userEmailAvailablity(String email);
	
    void downLoadFile(String filePath,HttpServletResponse response,Boolean inline);

	List<String> uploadBulkTemplate(MultipartFile multipartfiles) throws Exception ;


}
