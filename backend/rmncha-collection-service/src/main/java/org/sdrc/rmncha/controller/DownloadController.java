package org.sdrc.rmncha.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;


/**
 * @author Sarita Panigrahi
 *
 */
@Controller
@Slf4j
public class DownloadController {
	
	@RequestMapping(value = "/submissionImage", method = RequestMethod.GET)
	public void downLoad(@RequestParam("filePath") String filePath, HttpServletResponse response) throws IOException {

		InputStream inputStream;
				
		try {
			String fileName = filePath.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%2C", ",")
					.replaceAll("\\+", " ").replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%5C", "/");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("inline; filename=\"%s\"", new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("image/jpeg");
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/downloadImage", method = RequestMethod.GET)
	public void downLoadAttachments(@RequestParam("path") String name, HttpServletResponse response)
			throws IOException {

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
			log.error("error-while downloading image with payload : {}", name, e);
			throw new RuntimeException();
		}

	}
}
