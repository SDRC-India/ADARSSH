package org.sdrc.rmncha.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.domain.AllChecklistFormData;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.domain.UserDetails;
import org.sdrc.rmncha.model.ChecklistSubmissionStatus;
import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.sdrc.rmncha.repositories.AllChecklistFormDataRepository;
import org.sdrc.rmncha.repositories.CustomAuthorityRepository;
import org.sdrc.rmncha.repositories.EnginesFormRepository;
import org.sdrc.rmncha.service.SubmissionService;
import org.sdrc.rmncha.util.Mail;
import org.sdrc.rmncha.util.MailService;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;


@Service
public class NotificationJob {
	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;
	
	@Autowired
	private CustomAuthorityRepository customAuthorityRepository;
	
	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;
	
	@Autowired
	ConfigurableEnvironment configurableEnvironment;
	
	@Autowired
	private EnginesFormRepository enginesFormRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private CollectionChannel collectionChannel;
	
//	@Scheduled(cron="0 0 0 18/7 * ?")
	void sendNotificationForPenndingSubmission() {
		
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.add(Calendar.MONTH, -1);
		//aCalendar.set(Calendar.DATE, 1);
		aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		TimePeriod timePeriod = submissionService.getCurrentTimePeriod(aCalendar.getTime());
		
		List<AllChecklistFormData> fetchedData = allChecklistFormDataRepository.findByChecklistSubmissionStatusAndTimePeriodTimePeriodId(ChecklistSubmissionStatus.PENDING,timePeriod.getTimePeriodId());
		
		Map<Integer,Map<Integer,List<AllChecklistFormData>>> stateIdFormIdAllChecklistFormDataMap = new LinkedHashMap<>();
		Map<Integer,List<AllChecklistFormData>> formIdAllChecklistFormDataMap = null;
		List<AllChecklistFormData> listofAllChecklistFormData = null;
		Map<Integer,String> formMap = new HashMap<Integer, String>();
		Map<String,Map<Integer,List<AllChecklistFormData>>> userFormIdAllChecklistFormDataMap = new LinkedHashMap<>();
		
		List<EnginesForm>  listOfEngineForm = enginesFormRepository.findAll();
		listOfEngineForm.stream().forEach(form->{
			formMap.put(form.getFormId(), form.getName());
		});
		
		
		
		List<Account> listOfStateAdmin = new ArrayList<>();
		Map<String,Account> accountMap= new LinkedHashMap<>();
		List<Authority> authorities = customAuthorityRepository
				.findByAuthorityIn(Arrays.asList("Submission Management"));
		
		List<Mail> listOfMailModel = new ArrayList<>();
		List<Account> listOfAccounts =  accountRepository.findAll();
		for (Account account : listOfAccounts) {
			accountMap.put(account.getUserName(), account);
			if(account.getAuthorityIds().contains(authorities.get(0).getId())) {
				listOfStateAdmin.add(account);
			}
		}
		
		
		
		for (AllChecklistFormData allChecklistFormData : fetchedData) {
			Map<String,Object> dataMap = allChecklistFormData.getData();
			
			int stateId=0;
			 switch (allChecklistFormData.getFormId()) {
				case 1:
					stateId = (int) dataMap.get("f1q_state");
					break;
				case 2:
					stateId = (int) dataMap.get("f2qstate");
					break;
				case 3:
					stateId = (int) dataMap.get("f3q5_state");
					break;
				case 4:
					stateId = (int) dataMap.get("hwc5_state");
					break;

				default:
					break;
				}
			 
				if (stateIdFormIdAllChecklistFormDataMap.containsKey(stateId)) {
					formIdAllChecklistFormDataMap = stateIdFormIdAllChecklistFormDataMap.get(stateId);
					if (stateIdFormIdAllChecklistFormDataMap.get(stateId).containsKey(allChecklistFormData.getFormId())) {
						listofAllChecklistFormData = formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId());
						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId()).add(allChecklistFormData);
					} else {
						listofAllChecklistFormData = new ArrayList<>();
						listofAllChecklistFormData.add(allChecklistFormData);
						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),listofAllChecklistFormData);
					}
				} else {
					formIdAllChecklistFormDataMap = new LinkedHashMap<>();
					listofAllChecklistFormData = new ArrayList<>();
					if (formIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getFormId())) {
						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId())
								.add(allChecklistFormData);
					} else {
						listofAllChecklistFormData.add(allChecklistFormData);
						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),
								listofAllChecklistFormData);
					}
					stateIdFormIdAllChecklistFormDataMap.put(stateId, formIdAllChecklistFormDataMap);
				}
				
				
				//UserWise form submission
				/*formIdAllChecklistFormDataMap = null;
				listofAllChecklistFormData = null;*/
				
				if (userFormIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getUserName())) {
					formIdAllChecklistFormDataMap = userFormIdAllChecklistFormDataMap.get(allChecklistFormData.getUserName());
					if (userFormIdAllChecklistFormDataMap.get(allChecklistFormData.getUserName()).containsKey(allChecklistFormData.getFormId())) {
						listofAllChecklistFormData = formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId());
						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId()).add(allChecklistFormData);
					} else {
						listofAllChecklistFormData = new ArrayList<>();
						listofAllChecklistFormData.add(allChecklistFormData);
						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),listofAllChecklistFormData);
					}
				} else {
					formIdAllChecklistFormDataMap = new LinkedHashMap<>();
					listofAllChecklistFormData = new ArrayList<>();
					if (formIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getFormId())) {
						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId())
								.add(allChecklistFormData);
					} else {
						listofAllChecklistFormData.add(allChecklistFormData);
						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),
								listofAllChecklistFormData);
					}
					userFormIdAllChecklistFormDataMap.put(allChecklistFormData.getUserName(), formIdAllChecklistFormDataMap);
				}
			 
			
		}
		
		Map<Integer,List<AllChecklistFormData>> data =null;
		for (Account account : listOfAccounts) {
			if (account.getAuthorityIds().contains(authorities.get(0).getId())) {
				data = stateIdFormIdAllChecklistFormDataMap.get(account.getMappedAreaIds().get(0));
			} else {
				data = userFormIdAllChecklistFormDataMap.get(account.getUserName());
			}

			UserDetails user = (UserDetails) account.getUserDetails();
			if (data != null) {
				Mail mailModel = new Mail();
				mailModel.setToEmailIds(Arrays.asList(account.getEmail()));
				mailModel.setFromUserName(
						"RMNCHA Admin" + "\n" + configurableEnvironment.getProperty("email.disclaimer"));

				mailModel.setSubject("ADARSSH: Submission Notification");
				mailModel.setMessage("Please find below the details of submissions pending for approval by 25th."
						+ " \n\n" + formMap.get(1) + " : " + (data.get(1) == null ? 0 : data.get(1).size()) + "\n "
						+ formMap.get(2) + " : " + (data.get(2) == null ? 0 : data.get(2).size()) + "\n "
						+ formMap.get(3) + ": " + (data.get(3) == null ? 0 : data.get(3).size()) + " \n"
						+ formMap.get(4) + " : " + (data.get(4) == null ? 0 : data.get(4).size()) + "\n \n"
						+ "This Email is system generated. Please do not reply to this email ID. For any queries please contact the state admin. \n");

				mailModel.setToUserName("Dear " + user.getFirstName() + ",");
				mailModel.setEmail(account.getEmail());
				listOfMailModel.add(mailModel);

			}

		}
//		sending all mail
      for (Mail mailModel : listOfMailModel) {
    	  mailService.sendSimpleMessage(mailModel);
		}
	}
		
		
	/**
	 * This job runs every month on 26th. it updates all PENDING submission status to DEFAULT.
	 * @author Debi prasad parida
	 * 29-Apr-2019 8:03:15 PM
	 */
//	@Scheduled(cron="0 5 0 26 * ?")	 
	//THIS METHOD IS NOT IN USE AFTER UAT FEEDBACK --- DEFAULT STATUS TO BE REMOVED
      public void setPendingToDefault() {
  		
  		Calendar aCalendar = Calendar.getInstance();
  		aCalendar.add(Calendar.MONTH, -1);
  		aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
  		TimePeriod timePeriod = submissionService.getCurrentTimePeriod(aCalendar.getTime());
  		
  		List<AllChecklistFormData> fetchedData = allChecklistFormDataRepository.findByChecklistSubmissionStatusAndTimePeriodTimePeriodId(ChecklistSubmissionStatus.PENDING,timePeriod.getTimePeriodId());
  		
  		Map<Integer,Map<Integer,List<AllChecklistFormData>>> stateIdFormIdAllChecklistFormDataMap = new LinkedHashMap<>();
  		Map<Integer,List<AllChecklistFormData>> formIdAllChecklistFormDataMap = null;
  		List<AllChecklistFormData> listofAllChecklistFormData = null;
  		Map<Integer,String> formMap = new HashMap<Integer, String>();
  		Map<String,Map<Integer,List<AllChecklistFormData>>> userFormIdAllChecklistFormDataMap = new LinkedHashMap<>();
  		
  		Map<Integer,List<AllChecklistFormData>> formCountMap = new HashMap<>();
  		
  		List<EnginesForm>  listOfEngineForm = enginesFormRepository.findAll();
  		listOfEngineForm.stream().forEach(form->{
  			formMap.put(form.getFormId(), form.getName());
  		});
  		
  		List<Authority> authorities = customAuthorityRepository
  				.findByAuthorityIn(Arrays.asList("Submission Management"));
  		
  		List<Authority> adminAuthorities = customAuthorityRepository
  				.findByAuthorityIn(Arrays.asList("USER_MGMT_ALL_API"));
  		
  		List<Mail> listOfMailModel = new ArrayList<>();
  		List<Account> listOfAccounts =  accountRepository.findAll();
  		
  		List<AllChecklistFormData>  listOfAllChecklistFormDatas= new ArrayList<>();
  		
  		List<String> submissionIds = new ArrayList<>();
  		
  		for (AllChecklistFormData allChecklistFormData : fetchedData) {
  			
  			submissionIds.add(allChecklistFormData.getId());
  			
  			Map<String,Object> dataMap = allChecklistFormData.getData();
  			
  			int stateId=0;
  			 switch (allChecklistFormData.getFormId()) {
  				case 1:
  					stateId = (int) dataMap.get("f1q_state");
  					break;
  				case 2:
  					stateId = (int) dataMap.get("f2qstate");
  					break;
  				case 3:
  					stateId = (int) dataMap.get("f3q5_state");
  					break;
  				case 4:
  					stateId = (int) dataMap.get("hwc5_state");
  					break;

  				default:
  					break;
  				}
  			 
  			//Statewise formWise listOfsubmission
  				if (stateIdFormIdAllChecklistFormDataMap.containsKey(stateId)) {
  					formIdAllChecklistFormDataMap = stateIdFormIdAllChecklistFormDataMap.get(stateId);
  					if (stateIdFormIdAllChecklistFormDataMap.get(stateId).containsKey(allChecklistFormData.getFormId())) {
  						listofAllChecklistFormData = formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId());
  						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId()).add(allChecklistFormData);
  					} else {
  						listofAllChecklistFormData = new ArrayList<>();
  						listofAllChecklistFormData.add(allChecklistFormData);
  						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),listofAllChecklistFormData);
  					}
  				} else {
  					formIdAllChecklistFormDataMap = new LinkedHashMap<>();
  					listofAllChecklistFormData = new ArrayList<>();
  					if (formIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getFormId())) {
  						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId())
  								.add(allChecklistFormData);
  					} else {
  						listofAllChecklistFormData.add(allChecklistFormData);
  						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),
  								listofAllChecklistFormData);
  					}
  					stateIdFormIdAllChecklistFormDataMap.put(stateId, formIdAllChecklistFormDataMap);
  				}
  				
  				
  				//UserWise formWise listOfsubmission
  				
  				if (userFormIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getUserName())) {
  					formIdAllChecklistFormDataMap = userFormIdAllChecklistFormDataMap.get(allChecklistFormData.getUserName());
  					if (userFormIdAllChecklistFormDataMap.get(allChecklistFormData.getUserName()).containsKey(allChecklistFormData.getFormId())) {
  						listofAllChecklistFormData = formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId());
  						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId()).add(allChecklistFormData);
  					} else {
  						listofAllChecklistFormData = new ArrayList<>();
  						listofAllChecklistFormData.add(allChecklistFormData);
  						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),listofAllChecklistFormData);
  					}
  				} else {
  					formIdAllChecklistFormDataMap = new LinkedHashMap<>();
  					listofAllChecklistFormData = new ArrayList<>();
  					if (formIdAllChecklistFormDataMap.containsKey(allChecklistFormData.getFormId())) {
  						formIdAllChecklistFormDataMap.get(allChecklistFormData.getFormId())
  								.add(allChecklistFormData);
  					} else {
  						listofAllChecklistFormData.add(allChecklistFormData);
  						formIdAllChecklistFormDataMap.put(allChecklistFormData.getFormId(),
  								listofAllChecklistFormData);
  					}
  					userFormIdAllChecklistFormDataMap.put(allChecklistFormData.getUserName(), formIdAllChecklistFormDataMap);
  				}
  				
  				
  				if(formCountMap.containsKey(allChecklistFormData.getFormId())) {
  					formCountMap.get(allChecklistFormData.getFormId()).add(allChecklistFormData);
  					
  				}else {
  					listofAllChecklistFormData = new ArrayList<>();
  					listofAllChecklistFormData.add(allChecklistFormData);
  					formCountMap.put(allChecklistFormData.getFormId(), listofAllChecklistFormData);
  				}
  				
  				allChecklistFormData.setChecklistSubmissionStatus(ChecklistSubmissionStatus.DEFAULT);
  				listOfAllChecklistFormDatas.add(allChecklistFormData);
  			
  		}
  		allChecklistFormDataRepository.save(listOfAllChecklistFormDatas);
  		
  		//update default status in dashboard service
  		//uncomment to update status of default in dashboard service
//  		collectionChannel.defaultChannel().send(MessageBuilder.withPayload(submissionIds).build());
  		
  		LocalDateTime now = LocalDateTime.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	     
	     
  		Map<Integer,List<AllChecklistFormData>> data =null;
  		for (Account account : listOfAccounts) {
  			
  			if(account.getAuthorityIds().contains(adminAuthorities.get(0).getId())) {
  				data=formCountMap;
  			}else if(account.getAuthorityIds().contains(authorities.get(0).getId())) {
  		 data = stateIdFormIdAllChecklistFormDataMap.get(account.getMappedAreaIds().get(0));
  			}else {
  				data=userFormIdAllChecklistFormDataMap.get(account.getUserName());
  			}

  			UserDetails user = (UserDetails) account.getUserDetails();
  			if(data!=null) {
  			Mail mailModel = new Mail();
  			mailModel.setToEmailIds(Arrays.asList(account.getEmail()));
  			mailModel.setFromUserName("RMNCHA Admin" + "\n"  + configurableEnvironment.getProperty("email.disclaimer"));
  			
  				mailModel.setSubject("ADARSSH: Submission Notification");
  				mailModel.setMessage("please find below the details of submissions marked as default by the system as it was not approved by the state admin before deadline date("+now.format(formatter)+")."
  						+ " \n\n"+ formMap.get(1) +" : "+ (data.get(1)==null ? 0 :data.get(1).size())+"\n"+formMap.get(2) +" : "+(data.get(2)==null ? 0 : data.get(2).size())
  						+"\n"+ formMap.get(3) +": "+(data.get(3)==null? 0 : data.get(3).size()) +"\n"+ formMap.get(4) +" : "+(data.get(4)==null? 0 : data.get(4).size())+ 
  						"\n \n" + "This Email is system generated. Please do not reply to this email ID. For any queries please contact the state admin. \n" );
  			
  			mailModel.setToUserName("Dear "+user.getFirstName()+",");
  			mailModel.setEmail(account.getEmail());
  			listOfMailModel.add(mailModel);
  			
  			
  			}
  			
  		}
//  		sending all mail
        for (Mail mailModel : listOfMailModel) {
      	  mailService.sendSimpleMessage(mailModel);
  		}
  		
  		
  		 
  	}
  	
  	
  }

