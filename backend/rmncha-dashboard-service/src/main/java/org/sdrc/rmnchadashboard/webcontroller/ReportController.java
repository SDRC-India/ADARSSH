package org.sdrc.rmnchadashboard.webcontroller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmnchadashboard.web.model.ReportModel;
import org.sdrc.rmnchadashboard.web.service.AggregationService;
import org.sdrc.rmnchadashboard.web.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 3:02:50 PM
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private AggregationService aggregationService;
	
	@GetMapping("/welcome")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public String welcome() {
		return "welcome";
	}
	
	@GetMapping("/getReportData")
	public ReportModel getReportData() {
		return reportService.getReportData();
	}
	
	@GetMapping("/getRawDataReportFinal")
	public ResponseEntity<String>  getRawData(@RequestParam("formId") Integer formId,
			@RequestParam(value = "facilityTypeId", required = false) Integer facilityTypeId,
			@RequestParam(value = "sDate", required = false) String sDate,
			@RequestParam(value = "eDate", required = false) String eDate,
			@RequestParam(value = "stateId", required = false) Integer stateId,
			@RequestParam(value = "districtId", required = false) Integer districtId,
			@RequestParam(value = "blockId", required = false) Integer blockId, HttpServletResponse response,
			@RequestParam(value = "inline", required = false) Boolean inline,
			@RequestParam(value = "submissionIds", required = false)List<String> submissionIds)throws Exception{
		
		return reportService.getRawDataReport(formId, facilityTypeId, sDate, eDate, stateId, districtId, blockId,response, inline,submissionIds);
		 
	}
	
	@RequestMapping(value = "/downloadImage", method = RequestMethod.GET)
	public void downLoad(@RequestParam("path") String name, HttpServletResponse response) throws IOException {

		byte[] decodedBytes = Base64.getUrlDecoder().decode(name);
		name = new String(decodedBytes);

		name = name.replace("\\", "//");

		InputStream inputStream;
		
		try {
			String fileName = name.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%2C", ",")
					.replaceAll("\\+", " ").replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%5C", "/");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); // for all file
																	// type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
//			log.error("error-while downloading image with payload : {}", name, e);
			throw new RuntimeException();
		}

	}
	
	@GetMapping("/updateTpxxxxx")
	public String updateTp() {
		return reportService.updateTpInSubmission();
	}
	
	@GetMapping("/getAgg")
	public String getAggregatedDataInExcel(@RequestParam("formId") String formId,@RequestParam("tpId") Integer tpId) throws ParseException {
		aggregationService.getAggregatedDataInExcel(formId,tpId);
		return "Success";
	}

	@GetMapping("/updateSubmissionStatus")
	public String updateSubmissionStatus() {
		return aggregationService.updateSubmissionStatusToApprove();
	}
}
