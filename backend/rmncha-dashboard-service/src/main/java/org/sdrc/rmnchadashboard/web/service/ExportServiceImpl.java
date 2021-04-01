package org.sdrc.rmnchadashboard.web.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmncha.mongodomain.GroupIndicator;
import org.sdrc.rmncha.mongorepository.GroupIndicatorRepository;
import org.sdrc.rmnchadashboard.utils.HeaderFooter;
import org.sdrc.rmnchadashboard.utils.ImageEncoder;
import org.sdrc.rmnchadashboard.utils.RMNCHAUtil;
import org.sdrc.rmnchadashboard.web.model.GroupChartDataModel;
import org.sdrc.rmnchadashboard.web.model.IndicatorGroupModel;
import org.sdrc.rmnchadashboard.web.model.LegendModel;
import org.sdrc.rmnchadashboard.web.model.ParamModel;
import org.sdrc.rmnchadashboard.web.model.SVGModel;
import org.sdrc.rmnchadashboard.web.model.SectorModel;
import org.sdrc.rmnchadashboard.web.model.SubSectorModel;
import org.sdrc.rmnchadashboard.web.model.ValueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service

public class ExportServiceImpl implements ExportService {

	@Autowired
	private GroupIndicatorRepository groupIndicatorRepository;

	@Autowired
	private DashboardService dashboardService;

	String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());

	@Autowired
	private ConfigurableEnvironment env;
	
	@Autowired
	private ImageEncoder encoder;
	
	@Override
	public String downloadChartDataPDF(List<SVGModel> listOfSvgs, String districtName, String blockName,
			HttpServletRequest request, String stateName, String areaLevel, String dashboardType, String checkListName, String timePeriod, Integer typeId, Integer levelId) {
		String outputPathPdf = env.getProperty("output.path.pdf");
		List<GroupIndicator> groupIndicatorModels = groupIndicatorRepository.findAll();
		Map<String, GroupIndicator> groupIndicatorMap = new LinkedHashMap<>();
		for (GroupIndicator groupIndicatorModel : groupIndicatorModels) {
			groupIndicatorMap.put(groupIndicatorModel.getChartGroup(), groupIndicatorModel);
		}
		Map<String, SVGModel> svgMap = new LinkedHashMap<>();
		for (SVGModel eachSvgModel : listOfSvgs) {
			svgMap.put(eachSvgModel.getIndicatorGroupName(), eachSvgModel);
		}
		try {
			String date = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			String path = outputPathPdf;

			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			outputPathPdf = path + "_" + date + ".pdf";

			Rectangle layout = new Rectangle(PageSize.A4.rotate());
			layout.setBackgroundColor(new BaseColor(255,255,255));

			Document document = new Document(layout);//
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPathPdf));
		
			
			String uri = request.getRequestURI();
			String url = request.getRequestURL().toString();
			url = url.replaceFirst(uri, "");

			HeaderFooter headerFooter = new HeaderFooter(url);
			writer.setPageEvent(headerFooter);

			document.open();
			
			Font headerFont = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, GrayColor.BLACK);
			Paragraph areaParagraph = new Paragraph();
			areaParagraph.setAlignment(Element.ALIGN_CENTER);
			areaParagraph.setSpacingBefore(15);
			areaParagraph.setSpacingAfter(10);
			areaParagraph.setFont(headerFont);
			
			String chunkName = null;
			switch (areaLevel) {
			case "NATIONAL":
				chunkName = "India";
				break;
			case "STATE":
				chunkName = "State : " + stateName;
				break;
			case "DISTRICT":
				chunkName = "State : " + stateName + ", District : " + districtName;
				break;
			case "BLOCK":
				chunkName = "State : " + stateName + ", District : " + districtName + ", Block : " + blockName;
				break;

			}
			Chunk areaChunk = dashboardType.equals("COVERAGE") ?  new Chunk(chunkName) : new Chunk(checkListName+": "+ chunkName+", "+timePeriod);
			areaParagraph.add(areaChunk);
			
			Font font = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD, GrayColor.BLACK);
			Font indFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD, GrayColor.DARK_GRAY);
			Font legendFont = new Font(FontFamily.TIMES_ROMAN, 7, Font.NORMAL, GrayColor.DARK_GRAY);

			writer.setStrictImageSequence(true);
			
			document.add(areaParagraph);
			String sectorName = "";
			String subsectorName = "";
			List<String> subSectorSet = new ArrayList<>();
			Set<String> sectorSet = new LinkedHashSet<>();
			
			float yAxis = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
			float xAxis = 55;
			float xWidth = document.getPageSize().getWidth() - document.leftMargin();
			float textHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin() + 30;

			PdfContentByte canvas = writer.getDirectContent();
//			ImageEncoder encoder = new ImageEncoder();

			String rbpath = path + "chart1_" + date + ".svg";
			boolean isCard=false;
			boolean isCardAfterChart=false;
			
			for (String indicatorGroupName : groupIndicatorMap.keySet()) {
				String align = groupIndicatorMap.get(indicatorGroupName).getAlign();
				subsectorName = groupIndicatorMap.get(indicatorGroupName).getSubSector();
				sectorName = groupIndicatorMap.get(indicatorGroupName).getSector();
				String legends = groupIndicatorMap.get(indicatorGroupName).getColorLegends();
				
				if (svgMap.containsKey(indicatorGroupName)) {
					SVGModel svgModel = svgMap.get(indicatorGroupName);

					File svgFile = new File(rbpath);
					FileOutputStream fop = new FileOutputStream(svgFile);
					byte[] contentbytes = svgModel.getSvg().getBytes();
					fop.write(contentbytes);

					String jpgFilePath = encoder.createImgFromFile(rbpath, align, svgModel.getChartType());
					Image jpgImage = Image.getInstance(jpgFilePath);

					jpgImage.setAlignment(Element.ALIGN_CENTER);
					float scalerColMd12 = (float) (((document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin()) / jpgImage.getWidth()) * 97.5);

					float scalerColMd6 = svgModel.getChartType().equals("card") ? ((document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin()) / jpgImage.getWidth()) * 49 :  ((document.getPageSize().getWidth() - document.leftMargin()
									- document.rightMargin()) / jpgImage.getWidth()) * 48;

					float scalerColMd4 = ((document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin()) / jpgImage.getWidth()) * 30;

					float scalerColMd2 = ((document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin()) / jpgImage.getWidth()) * 20;
					
					boolean isSector = false;
					
					String sectorSubsectorName =sectorName + "_" +subsectorName;
					if (!subSectorSet.contains(sectorSubsectorName)) {

						if(yAxis<jpgImage.getHeight() - document.topMargin() - document.bottomMargin() - 5) {
							textHeight = document.getPageSize().getHeight() - document.topMargin();
						}else
							textHeight = yAxis+9;
						

						PdfPTable tableSubsector = new PdfPTable(1);
						PdfPCell cellSubsector = new PdfPCell();
						Paragraph pSubsector = new Paragraph(subsectorName, font);
						
						pSubsector = !sectorSet.contains(sectorName) && !sectorName.trim().equalsIgnoreCase(subsectorName.trim())
								? new Paragraph(sectorName+ " : "+subsectorName, font):  new Paragraph(subsectorName, font);
						
//						if(!sectorName.trim().equalsIgnoreCase(subsectorName.trim())) {
//							 pSubsector = new Paragraph(subsectorName + " : "+sectorName , font);
//						}else {
//							 pSubsector = new Paragraph(subsectorName, font);
//						}
						
						pSubsector.setAlignment(Element.ALIGN_CENTER);
						pSubsector.setSpacingBefore(1);
						pSubsector.setSpacingAfter(3);
						cellSubsector.addElement(pSubsector);
						cellSubsector.setBorder(Rectangle.NO_BORDER);
						tableSubsector.addCell(cellSubsector);
						tableSubsector.setTotalWidth(document.getPageSize().getWidth());
						
						
						if (yAxis < jpgImage.getHeight() - document.topMargin() - document.bottomMargin() - 5) {
							document.newPage();
							textHeight = document.getPageSize().getHeight() - document.topMargin();
							yAxis = (float) (document.getPageSize().getHeight() - document.topMargin()
									- document.bottomMargin() - (jpgImage.getHeight() / 1.5));
							xAxis = 55;
							xWidth = document.getPageSize().getWidth() - document.leftMargin();
//								System.out.println("NEW Doc...chart:"+svgModel.getChartType()+" align:"+align+" yAxis: "+yAxis+ " xAxis:"+xAxis +" xWidth:"+xWidth);
						} else {
							xAxis = 55;
							xWidth = document.getPageSize().getWidth() - document.leftMargin();
							yAxis -= jpgImage.getHeight() > 100 ? jpgImage.getHeight() - 90 : jpgImage.getHeight()-15;
							
							//when the above line was card and this line has also card, then keep the gap less
							yAxis = svgModel.getChartType().equals("card") ?  yAxis += 10 : yAxis;
							if (yAxis < 0) {
								document.newPage();
								textHeight = document.getPageSize().getHeight() - document.topMargin();
								yAxis = (float) (document.getPageSize().getHeight() - document.topMargin()
										- document.bottomMargin() - (jpgImage.getHeight() / 1.5));
							} else { //in case of same page, new sector
								yAxis -=10;
							}

						}
						tableSubsector.writeSelectedRows(-1, -1, 10, textHeight, writer.getDirectContent());

						if (jpgImage.getWidth() > 1000) {// for "col-md-12" images
							xWidth -= jpgImage.getWidth();
							jpgImage.scalePercent(scalerColMd12);
						} else if (jpgImage.getWidth() < 300) {
							jpgImage.scalePercent(scalerColMd2);
						}
						else if (jpgImage.getWidth() < 400) {
							jpgImage.scalePercent(scalerColMd4);
						}
						else {
							jpgImage.scalePercent(scalerColMd6);
						}


//						subSectorSet.add(subsectorName);
						
						subSectorSet.add(sectorName + "_" +subsectorName);
						sectorSet.add(sectorName);
						
						isSector = true;
					}

					if (!isSector) {
						
						if(svgModel.getIndName()!=null && svgModel.getIndName().trim().equals(env.getProperty("export.pdf.ind1")) && yAxis < 250) {
							yAxis = yAxis+15;
						}
						
						if(svgModel.getChartType().equals("table") && svgModel.getIndName().equals(env.getProperty("export.pdf.ind2"))){
							xAxis = 55;
							xWidth = document.getPageSize().getWidth() - document.leftMargin();
							yAxis -= jpgImage.getHeight() > 100 ? jpgImage.getHeight() - 90 : jpgImage.getHeight()-10;
						}	else {
							if (xWidth < jpgImage.getWidth() - document.leftMargin()
									&& yAxis < jpgImage.getHeight() - document.topMargin() - document.bottomMargin() - 5 
									) {//this is specific req to keep Percentage 
								//of In/outborn admission deaths table in the same page
								document.newPage();
								yAxis = (float) (document.getPageSize().getHeight() - document.topMargin()
										- document.bottomMargin() - (jpgImage.getHeight() / 1.5));
								xAxis = 55;
								xWidth = document.getPageSize().getWidth() - document.leftMargin();
							} else if (xWidth < jpgImage.getWidth()) {
								xAxis = 55;
								xWidth = document.getPageSize().getWidth() - document.leftMargin();
								yAxis -= jpgImage.getHeight() > 100 ? jpgImage.getHeight() - 90 : jpgImage.getHeight()-10;

								//when the above line was card and this line has also card, then keep the gap less
								yAxis = isCard && svgModel.getChartType().equals("card") ?  yAxis += 15 : svgModel.getChartType().equals("card") ? yAxis += 10 : yAxis;
								if (yAxis < 0) { //this is specific req to keep Percentage  && !svgModel.getIndName().equals("Percentage of In/outborn admission deaths"
									//of In/outborn admission deaths table in the same page
									
									document.newPage();
									yAxis = (float) (document.getPageSize().getHeight() - document.topMargin()
											- document.bottomMargin() - (jpgImage.getHeight() / 1.5));
								} else {
									yAxis -=8;
								}

							} else {
								xAxis += jpgImage.getWidth() < 400 ? jpgImage.getWidth() - (jpgImage.getWidth() / 4)
										: jpgImage.getWidth() - (jpgImage.getWidth() / 3);
								xWidth -= jpgImage.getWidth();
								
								// col-md-6 -- 1st is a chart and 2nd is a card, then pull up card to vertically middle 
								if(!isCard && svgModel.getChartType().equals("card") && jpgImage.getWidth() > 400 && jpgImage.getWidth() < 1000) {
									yAxis = yAxis+70;
									isCardAfterChart = true;
								}
							}
						}

						

						if (jpgImage.getWidth() > 1000) { // for "col-md-12" images
							xWidth -= jpgImage.getWidth();
							jpgImage.scalePercent(scalerColMd12);
						}  else if (jpgImage.getWidth() < 300) { //col-md-3
							jpgImage.scalePercent(scalerColMd2);
						}else if (jpgImage.getWidth() < 400) {//col-md-4
							jpgImage.scalePercent(scalerColMd4);
						} else {//col-md-6
							jpgImage.scalePercent(scalerColMd6);
						}

					}
					
					PdfPCell indNameCell = new PdfPCell();
					Paragraph indNamePara = new Paragraph(svgModel.getIndName(), indFont);
//					Paragraph indNamePara = new Paragraph(svgModel.getIndName().length() > 32
//							? svgModel.getIndName().substring(0, 32) + "\n\n"
//									+ svgModel.getIndName().substring(32, svgModel.getIndName().length())
//							: svgModel.getIndName(), indFont);
					indNamePara.setAlignment(Element.ALIGN_LEFT);
					indNamePara.setSpacingBefore(1);
					indNamePara.setSpacingAfter(3);
					indNameCell.addElement(indNamePara);
					indNameCell.setBorder(Rectangle.NO_BORDER);

					PdfPTable indNameTblLeft = new PdfPTable(1);
					indNameTblLeft.addCell(indNameCell);
					indNameTblLeft.setTotalWidth(document.getPageSize().getWidth());
					indNameTblLeft.writeSelectedRows(-1, -1, xAxis, (svgModel.getShowValue() != null ?
							svgModel.getChartType().equals("horizontalbar") ?  yAxis+212:	yAxis+218 :  
						 yAxis+212), writer.getDirectContent());
					
					// n value
					if(svgModel.getShowValue() != null) {
						PdfPCell nValCell = new PdfPCell();
						Paragraph nValPara = new Paragraph("n="+ svgModel.getShowValue() + " (" +svgModel.getShowNName()+")" , indFont);
						nValPara.setAlignment(Element.ALIGN_RIGHT);
						nValPara.setSpacingBefore(1);
						nValPara.setSpacingAfter(3);
						nValCell.addElement(nValPara);
						nValCell.setBorder(Rectangle.NO_BORDER);

						PdfPTable nValTbl = new PdfPTable(1);
						nValTbl.addCell(nValCell);
						nValTbl.setTotalWidth(document.getPageSize().getWidth());
						
						if (jpgImage.getWidth() > 1000) 
							nValTbl.writeSelectedRows(-1, -1, xAxis-100, yAxis+212, writer.getDirectContent());	
						else
							nValTbl.writeSelectedRows(-1, -1, xAxis-480, svgModel.getChartType().equals("horizontalbar") ? yAxis+202: yAxis+211, writer.getDirectContent());	
					}
					
					
					if(svgModel.getChartType().equals("pie") || svgModel.getChartType().equals("donut") 
							&& legends!=null && legends.length() > 0) {
						
						String css="";
						String leg = "";
						String[] legendsList = legends.split(",");
						int i =5;
//						for (String legend : legendsList) {
						for (int l=legendsList.length-1; l>=0; l--) { //reverse order
							
							css = legendsList[l].split("_")[0];
							leg = legendsList[l].split("_")[1];

							int color = (int) Long.parseLong(css.split("#")[1], 16);
							int r = (color >> 16) & 0xFF;
							int g = (color >> 8) & 0xFF;
							int b = (color >> 0) & 0xFF;
							
							canvas.rectangle(xAxis+5, yAxis + i + 5, 7, 7);
//							canvas.setColorFill(BaseColor.GRAY);
							canvas.setColorFill(new BaseColor(r, g, b));
							canvas.fill();

							PdfPCell legendCell = new PdfPCell();
							Paragraph legendPara = new Paragraph(leg, legendFont);
							legendPara.setAlignment(Element.ALIGN_LEFT);
							legendPara.setSpacingBefore(1);
							legendPara.setSpacingAfter(3);
							legendCell.addElement(legendPara);
							legendCell.setBorder(Rectangle.NO_BORDER);

							PdfPTable legendTable = new PdfPTable(1);
							legendTable.addCell(legendCell);
							legendTable.setTotalWidth(document.getPageSize().getWidth());
							legendTable.writeSelectedRows(-1, -1, xAxis + 15, yAxis + 18 + i,
									writer.getDirectContent());

							i += 20;
						}
						
						 
					}
					
					//group chart legend... optimize code
					
					else if ((svgModel.getChartType().equals("groupbar") || svgModel.getChartType().equals("stack"))
							&& legends != null && legends.length() > 0) {
						int i = 0;
						String css="";
						String leg = "";
						String[] legendsList = legends.split(",");
						String totalLegend="";
						for (String legend : legendsList) {
							css = legend.split("_")[0];
							leg = legend.split("_")[1];

							totalLegend = totalLegend+leg;
							int color = (int) Long.parseLong(css.split("#")[1], 16);
							int r = (color >> 16) & 0xFF;
							int g = (color >> 8) & 0xFF;
							int b = (color >> 0) & 0xFF;
							
							canvas.rectangle(i ==0 ? xAxis+i+5 : xAxis+i + ( totalLegend.length() * 2), yAxis + 5, 7, 7);
//							canvas.setColorFill(BaseColor.GRAY);
							canvas.setColorFill(new BaseColor(r, g, b));
							canvas.fill();

							PdfPCell legendCell = new PdfPCell();
							Paragraph legendPara = new Paragraph(leg, legendFont);
							legendPara.setAlignment(Element.ALIGN_LEFT);
							legendPara.setSpacingBefore(1);
							legendPara.setSpacingAfter(3);
							legendCell.addElement(legendPara);
							legendCell.setBorder(Rectangle.NO_BORDER);

							PdfPTable legendTable = new PdfPTable(1);
							legendTable.addCell(legendCell);
							legendTable.setTotalWidth(document.getPageSize().getWidth());
							legendTable.writeSelectedRows(-1, -1, i==0 ? xAxis+15+i : xAxis+10+i +( totalLegend.length() * 2) , yAxis + 18,
									writer.getDirectContent());
							
							
							if(leg.equals("PHC") ) {
								i +=20;
							}else {
								i +=90;
							}
							

						}
						
						 
					}
					
					if(!(svgModel.getChartType().equals("table") || svgModel.getChartType().equals("card"))) {
						jpgImage.setBorder(Rectangle.BOX);
						jpgImage.setBorderWidth(2);
						jpgImage.setBorderColor(BaseColor.LIGHT_GRAY);
					}
					
//					// col-md-6 -- 1st is a chart and 2nd is a card, then pull up card to vertically middle 
//					if(svgModel.getChartType().equals("card") && svgModel.getIndName().equals(env.getProperty("export.pdf.ind6.card")) ){
//						yAxis = yAxis+70;
//					}
									
					jpgImage.setAbsolutePosition(xAxis, yAxis);
					
					//again reset the axis
					if(isCardAfterChart) {
						yAxis = yAxis-70;
						isCardAfterChart = false;
					}
					//this is a specific requirement to keep next chart alignment correct, as table has some issue rendering the correct height
					if(svgModel.getChartType().equals("table") &&
							(svgModel.getIndName().equals(env.getProperty("export.pdf.ind3")) 
									||svgModel.getIndName().equals(env.getProperty("export.pdf.ind4"))
									||svgModel.getIndName().equals(env.getProperty("export.pdf.ind5")))) {
						yAxis = yAxis+50;
					}
					
					document.add(jpgImage);
					
					fop.flush();
					fop.close();
					isCard = svgModel.getChartType().equals("card") ? true : false; 
//					svgFile.delete();
//					new File(jpgFilePath).delete();
				}
			
			}
			
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputPathPdf;
	}
	public static PdfPCell createImageCell(Image jpgImage) throws DocumentException {
	    PdfPCell cell = new PdfPCell(jpgImage, true);
	    return cell;
	}
	
	public static PdfPCell createTextCell(String text) throws DocumentException {
	    PdfPCell cell = new PdfPCell();
	    Paragraph p = new Paragraph(text);
	    p.setAlignment(Element.ALIGN_RIGHT);
	    cell.addElement(p);
	    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
	    cell.setBorder(Rectangle.NO_BORDER);
	    return cell;
	}

	@Override
	public String downloadChartDataExcel(List<SVGModel> listOfSvgs, ParamModel paramModel, HttpServletRequest request) {
		Integer areaId = null;
		if (paramModel.getAreaLevelId() != 1) {
			 areaId = paramModel.getBlockId() == null
					? paramModel.getDistrictId() == null ? paramModel.getStateId() : paramModel.getDistrictId()
					: paramModel.getBlockId();
		}else {
			areaId = 1;
		}
		if(paramModel.getDashboardType().equals("COVERAGE"))
		{
			paramModel.setTpId(null);	
		}
		
		ValueObject getSectorMap = getSectorMap(dashboardService.getDashboardData(paramModel.getAreaLevelId(),areaId, paramModel.getSectorId(), 
				paramModel.getTpId(), paramModel.getFormId(), paramModel.getDashboardType(), paramModel.getTypeId(), paramModel.getLevelId()));
		
		String path =  env.getProperty("output.path.pdf") + env.getProperty("output.path.excel");
		String outputPathExcel = getFileName(path);
		List<GroupIndicator> groupIndicatorModels = groupIndicatorRepository.findAll();
		Map<String, GroupIndicator> groupIndicatorMap = new LinkedHashMap<>();
		for (GroupIndicator groupIndicatorModel : groupIndicatorModels) {
			groupIndicatorMap.put(groupIndicatorModel.getChartGroup(), groupIndicatorModel);
		}

//		ImageEncoder encoder = new ImageEncoder();

		String rbpath = path + "chart1_" + date + ".svg";
		
		Map<String, SVGModel> svgMap = listOfSvgs.stream()
				.collect(Collectors.toMap(SVGModel::getIndicatorGroupName, v -> v));

		try {

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(env.getProperty("export.excel.sheet.name"));
			XSSFCellStyle cellstyleFont = RMNCHAUtil.getStyleForFont(workbook);
			XSSFCellStyle cellgetStyleForFontDatavalue = RMNCHAUtil.getStyleForFont(workbook);
			XSSFCellStyle cellgetStyleForSector = RMNCHAUtil.getStyleForSectorFont(workbook);
			CellStyle style = workbook.createCellStyle(); //Create new style
			int rowNum = 0;
			
			BufferedImage bImage = ImageIO.read(new File(ResourceUtils.getFile("classpath:images/Header.jpg").getAbsolutePath()));
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ImageIO.write(bImage, "jpg", bos );
			byte [] headerImgBytes = bos.toByteArray();
			insertimage(0, headerImgBytes, workbook, sheet);
			
			int selectionrow=5; 
			XSSFRow rowselection = null;
			XSSFCell cellselection = null;
			rowselection = sheet.createRow(selectionrow);
			cellselection = rowselection.createCell(0);
			cellselection.setCellValue("Area Level :- "+paramModel.getAreaLevelName());
			cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			if(paramModel.getStateName()!=null) {
				cellselection = rowselection.createCell(1);
				cellselection.setCellValue("State :- "+paramModel.getStateName());
				cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}
			if(paramModel.getDistrictName()!=null) {
				cellselection = rowselection.createCell(2);
				cellselection.setCellValue("District :- "+paramModel.getDistrictName());
				cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}
			if(paramModel.getBlockName()!=null) {
				cellselection = rowselection.createCell(3);
				cellselection.setCellValue("Block :- "+paramModel.getBlockName());
				cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}
			
			if(paramModel.getSectorName()!=null) {
				++selectionrow;
			rowselection = sheet.createRow(selectionrow);
			cellselection = rowselection.createCell(0);
			cellselection.setCellValue("Sector :- "+paramModel.getSectorName());
			cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}
			
			if(paramModel.getChecklistName()!=null) {
				++selectionrow;
			rowselection = sheet.createRow(selectionrow);
			cellselection = rowselection.createCell(0);
			cellselection.setCellValue("CheckList Name :- "+paramModel.getChecklistName());
			cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}
			if(paramModel.getTimeperiod()!=null) {
				++selectionrow;
			rowselection = sheet.createRow(selectionrow);
			cellselection = rowselection.createCell(0);
			cellselection.setCellValue("Time Period :- "+paramModel.getTimeperiod());
			cellselection.setCellStyle(cellgetStyleForFontDatavalue);
			}

			rowNum += 12; 
			int cellNum = 0;
			XSSFRow row = null;
			XSSFCell cell = null;
			Set<String> subSectorSet = new HashSet<>();
			for (String indicatorGroupName : groupIndicatorMap.keySet()) {
				if (svgMap.containsKey(indicatorGroupName)) {
					GroupIndicator groupIndiactor = groupIndicatorMap.get(indicatorGroupName);

					SVGModel svgModel = svgMap.get(indicatorGroupName);
//					byte[] svgImageBytes = Base64.decodeBase64(((String) svgModel.getSvg()).split(",")[1]);
					
					File svgFile = new File(rbpath);
					FileOutputStream fop = new FileOutputStream(svgFile);
					byte[] contentbytes = svgModel.getSvg().getBytes();
					fop.write(contentbytes);

					String jpgFilePath = encoder.createImgFromFile(rbpath, groupIndiactor.getAlign(), svgModel.getChartType());
					InputStream inputStream = new FileInputStream(jpgFilePath);
					byte[] imageBytes = IOUtils.toByteArray(inputStream);
					
					row = sheet.createRow(rowNum);
					cell = row.createCell(cellNum);
					if (!subSectorSet.contains(groupIndiactor.getSubSector())) {
						cell.setCellValue(groupIndiactor.getSubSector());
						sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellNum, cellNum+8));
						cell.setCellStyle(cellgetStyleForSector);
						subSectorSet.add(groupIndiactor.getSubSector());
						rowNum += 1;
					}
					int row2 = insertimage(rowNum, imageBytes, workbook, sheet);
					
					
					int tempRow=0;
					int tempCol=0;
					boolean  flag=true;
					
					List<String> value = getSectorMap.getChartMap().get(svgModel.getIndicatorGroupName());
					
					if(value!=null) {
						
						if(svgModel.getShowValue()!=null) {
							row = sheet.getRow(rowNum+2) == null ? sheet.createRow(rowNum+2) : sheet.getRow(rowNum+2);
							cell = row.createCell(10);
							cell.setCellValue("n="+svgModel.getShowValue()+" ("+svgModel.getShowNName()+")");
						}
						
					if(svgModel.getChartType().equals("card")) {
						
						tempRow=rowNum+3;
						row = sheet.createRow(tempRow);
						cell = row.createCell(10);
						cell.setCellValue(value.get(0).split("#")[0]);
						sheet.autoSizeColumn(value.get(0).split("#")[0].length());
						cell.setCellStyle(cellstyleFont);
						cell = row.createCell(11);
						cell.setCellValue(value.get(0).split("#")[1 ].equals("null") ? "NA" : value.get(0).split("#")[1 ]);
						
						} else if (svgModel.getChartType().equals("groupbar") || svgModel.getChartType().equals("pie")
								|| svgModel.getChartType().equals("donut") || svgModel.getChartType().equals("stack") || svgModel.getChartType().equals("table")) {
						
						List<String> legends = getSectorMap.getLegendsMap().get(svgModel.getIndicatorGroupName());
						int divide = value.size() / legends.size();
						tempRow = rowNum + 3;
						int k = 0,l=0;
						tempCol = 10;
						for (int i = 0; i <= value.size() - 1; i++) {
							if (k > 0) {
								if (k == divide || k % divide == 0) {
									tempCol += 3;
									tempRow = rowNum + 4;
									++l;
									
								}
							}
							if(tempCol ==10) {
							row = sheet.createRow(tempRow);
							}else {
								row = sheet.getRow(tempRow);
							}
							cell = row.createCell(tempCol);
							if (flag) {
								cell.setCellValue(value.get(i).split("#")[0]);
								cell.setCellStyle(cellgetStyleForFontDatavalue);
								
								++tempRow;
								//sheet.addMergedRegion(new CellRangeAddress(tempRow-1, tempRow-1, tempCol, tempCol+11));
							}
							if(tempCol ==10) {
								row = sheet.createRow(tempRow);
								}else {
									row = sheet.getRow(tempRow);
								}
							cell = row.createCell(tempCol);
							cell.setCellValue(value.get(i).split("#")[1]+" ("+legends.get(l).split("@#")[1]+")");
							sheet.setColumnWidth(tempCol, 13000);
						
			                style.setWrapText(true);
							
							cell = row.createCell(tempCol + 1);
							cell.setCellValue(value.get(i).split("#")[2].equals("null") ? "NA" : value.get(i).split("#")[2]);
							
							++tempRow;
							++k;
							flag = false;
						}
						sheet.autoSizeColumn(tempCol);
						tempRow+=1;
						row = sheet.createRow(++tempRow);
						cell = row.createCell(9);
						
						if (!svgModel.getChartType().equals("table")) {
							cell.setCellValue("Legends");
							cell.setCellStyle(cellgetStyleForFontDatavalue);
							
							sheet.addMergedRegion(new CellRangeAddress(tempRow, tempRow, 9, 10));
							for (int m=0;m<=legends.size()-1;m++) {
								
								XSSFCellStyle cellstyleColor = RMNCHAUtil.getColorStyle(workbook, legends.get(m).split("@#")[0]);
								
								row = sheet.createRow(++tempRow);
								cell = row.createCell(10);
								cell.setCellValue(legends.get(m).split("@#")[1]);
								
								cell = row.createCell(9);
								cell.setCellStyle(cellstyleColor);
							}
						}
						
							
					}else {
						tempRow=rowNum+3;
						tempCol=10;
						for(int i=0;i<=value.size()-1;i++) {
							row = sheet.createRow(tempRow);
							cell = row.createCell(tempCol);
							if(flag) {
								cell.setCellValue(value.get(i).split("#")[0]);
								cell.setCellStyle(cellgetStyleForFontDatavalue);
								sheet.autoSizeColumn(value.get(i).split("#")[0].length());
								++tempRow;
								//sheet.addMergedRegion(new CellRangeAddress(tempRow-1, tempRow-1, tempCol, tempCol+2));
							}
							row = sheet.createRow(tempRow);
							cell = row.createCell(tempCol);
							cell.setCellValue(value.get(i).split("#")[1]);
							sheet.autoSizeColumn(value.get(i).split("#")[1].length());
							
							
							cell = row.createCell(tempCol+1);
							cell.setCellValue(value.get(i).split("#")[2].equals("null") ? "NA" : value.get(i).split("#")[2]);
							++tempRow;
							flag=false;
						}
					}
				}
					rowNum = row2 + rowNum;
					
					new File(jpgFilePath).delete();
					fop.flush();
					fop.close();
				}
				

			}

			FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPathExcel));
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputPathExcel;
	}
	
	
	private String getFileName(String outputPathExcel) {
		File file = new File(outputPathExcel);
		if (!file.exists()) {
			file.mkdirs();
		}
		outputPathExcel = outputPathExcel + "_" + date + ".xlsx";

		return outputPathExcel;
	}

	private int insertimage(int rowNum, byte [] imageBytes, XSSFWorkbook xssfWorkbook, XSSFSheet sheet) {
		Integer size = null;
		try {
			int pictureIdx = xssfWorkbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
			CreationHelper helper = xssfWorkbook.getCreationHelper();
			Drawing<?> drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(0);
			anchor.setCol2(6);
			anchor.setRow1(rowNum);
			anchor.setRow2(rowNum+12);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			if(pict.getImageDimension().getHeight()<=100) {
				anchor.setCol2(18);
				anchor.setRow2(4);
				pict.resize();
				size = 4;
			}else if(pict.getImageDimension().getHeight()<150){
				pict.resize();
				size = 12;
			} else if(pict.getImageDimension().getHeight()>150 && pict.getImageDimension().getHeight()<300) {
				pict.resize();
				size = 18;
			} else if (pict.getImageDimension().getHeight()>=300) {
				pict.resize(1.5);
				size = 24;
			}

		} catch (Exception e) {
		}
		return size.intValue();
	}
	
	public ValueObject getSectorMap(List<SectorModel> sectorModelList ) {
		ValueObject valueObject  = new ValueObject();
		Map<String, List<String>> chartMap = new LinkedHashMap<>();
		Map<String, List<String>> legendsMap = new LinkedHashMap<>();
		
		
		for (SectorModel sectorModel : sectorModelList) {
			for (SubSectorModel subsector : sectorModel.getSubSectors()) {
				for (IndicatorGroupModel indicator : subsector.getIndicators()) {

					if (!indicator.getChartsAvailable().get(0).equals("")
							&& indicator.getChartsAvailable().get(0) != null) {
						if (indicator.getChartsAvailable().get(0).equals("card")) {
							chartMap.put(indicator.getChartGroup(),
									Arrays.asList(indicator.getIndicatorName() + "#" + indicator.getIndicatorValue()));
						} else if (indicator.getChartsAvailable().get(0).equals("groupbar") || indicator.getChartsAvailable().get(0).equals("pie")
								|| indicator.getChartsAvailable().get(0).equals("donut") || indicator.getChartsAvailable().get(0).equals("stack")
								|| indicator.getChartsAvailable().get(0).equals("table")) {
							for (GroupChartDataModel chartData : indicator.getChartData()) {
								chartMap = setInMap(chartData, chartMap, indicator);

								List<LegendModel> legends = chartData.getLegends();
								if (legends != null && !legends.isEmpty()) {
									for (int j = 0; j <= (legends.size() - 1); j++) {
										List listOfString = new ArrayList<>();
										if (legendsMap.containsKey(indicator.getChartGroup())) {
											listOfString.add(
													legends.get(j).getCssClass() + "@#" + legends.get(j).getValue());
											legendsMap.get(indicator.getChartGroup()).addAll(listOfString);
										} else {
											listOfString.add(
													legends.get(j).getCssClass() + "@#" + legends.get(j).getValue());
											legendsMap.put(indicator.getChartGroup(), listOfString);
										}
									}
								}
							}

						} else {
							for (GroupChartDataModel chartData : indicator.getChartData()) {
								chartMap = setInMap(chartData, chartMap, indicator);
							}

						}

					}

				}
			}

		}
		
		valueObject.setChartMap(chartMap);
		valueObject.setLegendsMap(legendsMap);
		return valueObject;
	}
	
	private Map<String, List<String>> setInMap(GroupChartDataModel chartData, Map<String, List<String>> chartMap,
			IndicatorGroupModel indicator) {
		List listOfString = null;
		for (int i = 0; i <= (chartData.getChartDataValue().size() - 1); i++) {
			for (int j = 0; j <= (chartData.getChartDataValue().get(i).size() - 1); j++) {
				listOfString = new ArrayList<>();
				if (chartMap.containsKey(indicator.getChartGroup())) {
					listOfString.add(chartData.getHeaderIndicatorName() + "#"
							+ chartData.getChartDataValue().get(i).get(j).getAxis() + "#"
							+ chartData.getChartDataValue().get(i).get(j).getValue());
					chartMap.get(indicator.getChartGroup()).addAll(listOfString);
				} else {
					listOfString.add(chartData.getHeaderIndicatorName() + "#"
							+ chartData.getChartDataValue().get(i).get(j).getAxis() + "#"
							+ chartData.getChartDataValue().get(i).get(j).getValue());
					chartMap.put(indicator.getChartGroup(), listOfString);
				}
			}
		}
		return chartMap;
	}
			
	
}
	
