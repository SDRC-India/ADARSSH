package org.sdrc.rmnchadashboard.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Subham Ashish(subham@sdrc.co.in) Created Date:26-Jun-2018 2:22:59 PM
 */
@Data
@AllArgsConstructor
@ToString
@Document
@NoArgsConstructor
public class TypeDetail implements Serializable {

	private static final long serialVersionUID = 7158994858633568625L;

	@Id
	private String id;

	private Integer slugId;

	private String name;

	private Type type;

	private Integer orderLevel;

	private Integer formId;

	private String score;

	private List<Integer> parentIds;

	private Map<Language, String> languages = new HashMap<>();
	
	private String filterByExp;

}
