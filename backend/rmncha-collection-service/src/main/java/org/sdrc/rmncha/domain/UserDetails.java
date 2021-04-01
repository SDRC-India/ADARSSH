package org.sdrc.rmncha.domain;

import java.io.Serializable;

import org.sdrc.rmncha.util.Gender;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetails implements Serializable {

	private static final long serialVersionUID = -4503442884290702891L;

	private Integer organization;

//	private Integer designation;

	private String orgName;

//	private String desgnName;

	private String firstName;

	private String middleName;

	private String lastName;

	private Gender gender;

	private String dob;

	private String mobNo;

	private AreaLevel areaLevel;

	private String devPartner;
	
	private String idProType;

	private String idProofFile;

	private String othersDevPartner;

	private String othersIdProof;
	
	private String createdDate;
	
	private String updateDate;
	
	private String aproveRejectDate;
	
	private String rejectReason;
	
	private String bulkId;

}
