package org.sdrc.rmncha.model;

import java.util.List;
import java.util.Set;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.util.Gender;

import lombok.Data;
/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 24-Jul-2019 4:03:18 PM
 */

@Data
public class UserModel {

	private String userId;

	private Set<String> roleIds;

	private Set<String> roles;

	private List<Area> areas;
	
	private Set<Integer> authorityIds;

	private Set<String> authorities;
	
	private List<String> authorityNames;
	
	private String name;

	private String emailId;

	private Integer organization;

	private String orgName;

	private String designation;
	
	private String desgnName;
	
	private List<Integer> desgSlugIds;

	private String firstName;

	private String middleName;

	private String lastName;

	private Gender gender;

	private String dob;

	private String mobNo;

	private String areaLevel;

	private String devPartner;

	private String idProType;

	private String idProofFile;

	private String othersDevPartner;

	private String othersIdProof;
	
	private String createdDate;
	
	private boolean enable;
	
	private Integer roleId;
	
	private String devPartnerId;

	private String idProTypeName;
	
	private String updateDate;
	
	private String aproveRejectDate;
	
	private String rejectReason;
	
	private String bulkId;
	
}
