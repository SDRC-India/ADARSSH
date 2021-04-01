package org.sdrc.rmnchadashboard.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmnchadashboard.model.AttachmentModel;
import org.sdrc.rmnchadashboard.model.CmsDataModel;
import org.sdrc.rmnchadashboard.model.CmsDataRequestModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.model.TableResponseModel;
import org.sdrc.rmnchadashboard.model.ViewDataRequestModel;
import org.sdrc.rmnchadashboard.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class CmsController {
	
	@Autowired
	private CmsService cmsService;

	@Value("${output.path.pdf}")
	private String directoryPath;
	
	@Value("${cms.upload.path}")
	private String cmsFilePath;
	
	@PreAuthorize("hasAnyAuthority('USER_MGMT_ALL_API')")
	@ResponseBody
	@GetMapping("getCMSData")
	ResponseEntity<List<CmsDataModel>> getCMSData() {
		return ResponseEntity.status(HttpStatus.OK).body(cmsService.getCMSData());
	}

	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	@PostMapping("uploadFile")
	@ResponseBody
	public ResponseEntity<AttachmentModel> handleFileUpload(@RequestParam("file") MultipartFile file) {

		try {
			return ResponseEntity.status(HttpStatus.OK).body(cmsService.uploadFile(file));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}
	}

	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	@PostMapping("saveUpdateData")
	@ResponseBody
	ResponseEntity<ResponseModel> saveUpdateData(@RequestBody CmsDataRequestModel cmsDataRequestModel) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(cmsService.saveUpdateData(cmsDataRequestModel));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}
	}

	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	@PostMapping("deleteData")
	@ResponseBody
	ResponseEntity<ResponseModel> deleteData(@RequestBody CmsDataRequestModel cmsDataRequestModel) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(cmsService.deleteData(cmsDataRequestModel));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}
	}

	@PostMapping("anynomus/getCMSRequestData")
	@ResponseBody
	ResponseEntity<Map<String, JsonNode>> getCMSRequestData(@RequestBody ViewDataRequestModel viewDataRequestModel) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(cmsService.getCMSRequestData(viewDataRequestModel));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}
	}

	@PostMapping("anynomus/getCMSRequestDataInTable")
	@ResponseBody
	ResponseEntity<TableResponseModel> getCMSRequestDataInTable(
			@RequestBody ViewDataRequestModel viewDataRequestModel) {

		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(cmsService.getCMSRequestDataInTable(viewDataRequestModel));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}

	}
	
	@GetMapping(value = "anynomus/getFile")
	public void getFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {

		Path filePath = Paths.get(directoryPath+cmsFilePath);
		fileName = filePath.resolve(fileName).toAbsolutePath().toString();
		
		InputStream inputStream;
		try {
			fileName = fileName.trim().replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%5C", "/")
					.replaceAll("%2C", ",").replaceAll("\\\\", "/").replaceAll("\\+", " ").replaceAll("%22", "")
					.replaceAll("%3F", "?").replaceAll("%3D", "=");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = "";
			 String type = new java.io.File(fileName).getName().split("\\.")[new java.io.File(fileName).getName().split("\\.").length-1];
			{
				headerValue = String.format("inline; filename=\"%s\"", new java.io.File(fileName).getName());
				if(type.equalsIgnoreCase("pdf"))
					response.setContentType("application/pdf"); // for all file
					else
						response.setContentType("image/jpeg");
			}
			response.setHeader(headerKey, headerValue);

			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	@GetMapping(value = "anynomus/downloadFile")
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {

		Path filePath = Paths.get(directoryPath+cmsFilePath);
		fileName = filePath.resolve(fileName).toAbsolutePath().toString();
		
		File file = new File( filePath.resolve(fileName).toAbsolutePath().toString());
		 String type = new java.io.File(fileName).getName().split("\\.")[new java.io.File(fileName).getName().split("\\.").length-1];
			try {
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
				if(type.equalsIgnoreCase("pdf"))
					respHeaders.add("Content-Type","application/pdf");
					else
						respHeaders.add("Content-Type","image/jpeg");
				
				InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
				return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
			}
			
			catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}

	}
}
