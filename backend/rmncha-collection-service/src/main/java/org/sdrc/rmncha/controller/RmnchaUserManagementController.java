package org.sdrc.rmncha.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.domain.Organization;
import org.sdrc.rmncha.model.AreaModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;

@RestController
public class RmnchaUserManagementController {

	

	@Autowired
	UserManagementService userManagementService;

	@RequestMapping("/")
	public String test() {
		return ("Welcome to rmncha data collection service");

	}
	
	
	@GetMapping("/rejectAndApproveUser")
	@PreAuthorize("hasAnyAuthority('USER_MGMT_ALL_API','ENABLE_DISABLE_USER')")
	public ResponseEntity<String> rejectUser(@RequestParam("userIds") String userIds,
			@RequestParam("isApprove") Boolean isApprove,
			@RequestParam(value = "reason", required = false) String reason) {
		return userManagementService.rejectAndApproveUser( Arrays.asList(userIds.split(",")), isApprove,reason);

	}
	
	@GetMapping("/getAllUsers")
	@PreAuthorize("hasAnyAuthority('USER_MGMT_ALL_API', 'ENABLE_DISABLE_USER')")
	public ResponseEntity<Map<String, List<UserModel>>> getAllUsers() {

		return userManagementService.getAllUser();

	}
	
	@GetMapping("/bypass/getTypeDetails")
	public ResponseEntity<List<TypeDetail>> getTypeDetailsByType(@RequestParam("typeName") String typeName){
		
		return userManagementService.getTypeDetailsByType(typeName);
	}
	
	@GetMapping("/bypass/getAreaLevels")
	public ResponseEntity<List<AreaLevel>> getAreaLevels(){
		
		return userManagementService.getAreaLevels();
	}
	
	@GetMapping("/bypass/getAllOrganization")
	public ResponseEntity<List<Organization>> getAllOrganization(){
		
		return userManagementService.getAllOrganization();
	}
	
//	@PostMapping("/saveDesignation")
//	public ResponseEntity<String> saveDesignation(@RequestParam("designationName") String designationName){
//		
//		return userManagementService.saveDesignation(designationName);
//	}
	
	 @GetMapping(value = "/bypass/getEmailVarificationCode")
	 public  ResponseEntity<String> getEmailVarificationCode(
				@RequestParam("email") String email) {
			return userManagementService.getEmailVerificationCode(email);
		}
	 
	 @GetMapping(value = "/bypass/getEmailOTPAvailability")
	public ResponseEntity<String> OTPAndEmailAvailibility(@RequestParam("email") String email,
			@RequestParam("varificationCode") Integer varificationCode) {
		return userManagementService.oTPAndEmailAvailibility(email, varificationCode);
	}
	 
	 @GetMapping(value = "/bypass/usernameAvailablity")
	public ResponseEntity<Boolean> usernameAvailablity(@RequestParam("username") String username) {
		return userManagementService.usernameAvailablity(username.toLowerCase());
	}
	 
	 @GetMapping(value = "/bypass/getServerDate")
	public ResponseEntity<String> getServerDate() {
		return userManagementService.getServerDate();
	}
	 
	 @PostMapping(value = "/bypass/uploadFile")
		public ResponseEntity<String> uploadFile(@RequestParam(value = "uploadIdProofFile", required=false) MultipartFile uploadIdProofFile) {
			return userManagementService.uploadFile(uploadIdProofFile);
		}
	 
	 @ResponseBody
		@RequestMapping(value = "/bypass/getAllArea")
		public Map<String, List<AreaModel>> getArea() {
			return userManagementService.getAllAreaList();

		}
	 @GetMapping(value="/bypass/addDesignation")
	 public ResponseEntity<String> addDesignation(@RequestParam("designation")String designation){
		return userManagementService.addDesignation(designation);
		 
	 }
	
      
       @RequestMapping(value = "/downloadTemplate", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> downLoad(HttpServletResponse response,
			@RequestParam(value = "inline", required = false) Boolean inline) throws Exception {
//       userManagementService.downLoadFile((userManagementService.downLoadBulkTemplate()), response, inline);
		String filePath = "";
		try {
			filePath = userManagementService.downLoadBulkTemplate();
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

			file.delete();
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
       
       @PostMapping(value = "/uploadTemplate")
       public List<String> uploadTemplate(@RequestParam(value = "templateFile", required=false) MultipartFile uploadIdProofFile) throws Exception {
   		return userManagementService.uploadBulkTemplate(uploadIdProofFile);
    }  
    
    
    @GetMapping(value = "/getUserRoleArea")
   	public List<UserModel> getUserRoleArea(@RequestParam("roleId") Integer roleId,@RequestParam(value="areaId",required=false)Integer areaId) {
   		return userManagementService.getUser(roleId,areaId);
   	}
    
    @GetMapping(value = "/bypass/userEmailAvailablity")
   	public ResponseEntity<Boolean> userEmailAvailablity(@RequestParam("email") String email) {
   		return userManagementService.userEmailAvailablity(email.toLowerCase());
   	}
    
   @GetMapping(value = "/preAuthorizeCheck")
   @PreAuthorize("hasAnyAuthority('USER_MGMT_ALL_API', 'CREATE_USER')")
   	public ResponseEntity<Boolean> preAuthorizeCheck(@RequestParam("email") String email) {
   		return userManagementService.userEmailAvailablity(email);
   	}
   
   @GetMapping(value = "/downloadFromFilePath")
   public void downLoad(@RequestParam("filePath") String filePath,HttpServletResponse response, @RequestParam(value="inline", required=false) Boolean inline) throws IOException {
   userManagementService.downLoadFile(filePath , response, inline);
   
   }

	
}
