
package org.sdrc.rmncha.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * @author Subham Ashish (subham@sdrc.co.in)
 *
 */
@Component
public class TokenInfoExtracter {

	@Autowired(required = false)
	private TokenStore tokenStore;

	/*
	 * it retrieves the user-info from JWT token.
	 */
	public Map<String, Object> tokenInfo(OAuth2Authentication auth) {

		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
		return accessToken.getAdditionalInformation();

	}

	/*
	 * Extracting the user info from JWT token and setting it to UserModel
	 * Object
	 */
	public UserModel getUserModelInfo(OAuth2Authentication auth) {

		Map<String, Object> tokenInfoMap = tokenInfo(auth);

		UserModel user = new UserModel();

		user.setEmailId(tokenInfoMap.get("emailId")!=null ? tokenInfoMap.get("emailId").toString() : "");

		user.setName(tokenInfoMap.get("user_name").toString());

		user.setUserId(tokenInfoMap.get("userId").toString());
		

		Set<String> idSet = new HashSet<>();
		Set<String> roleSet = new HashSet<>();

		StringTokenizer stId = new StringTokenizer(tokenInfoMap.get("designationIds").toString(), ",");

		while (stId.hasMoreTokens()) {
			idSet.add(stId.nextToken());
		}

		StringTokenizer stName = new StringTokenizer(tokenInfoMap.get("designationNames").toString(), ",");

		while (stName.hasMoreTokens()) {
			roleSet.add(stName.nextToken());
		}

		user.setRoleIds(idSet);
		user.setRoles(roleSet);
//		 * extrating area
//		 */
		Map<String, Object> sessionAreaMap = (Map<String, Object>) tokenInfoMap.get("sessionMap");
		user.setOrgName(sessionAreaMap.get("orgName").toString());
		user.setFirstName(sessionAreaMap.get("firstName").toString());
//		user.setMiddleName(sessionAreaMap.containsKey("middleName") ? sessionAreaMap.get("middleName").toString() : "");
		user.setLastName(sessionAreaMap.get("lastName").toString());
//		user.setAreaLevel(sessionAreaMap.containsKey("areaLevel") ? sessionAreaMap.get("areaLevel").toString() : "");
		
		
		List<Object> areaList = (List<Object>) sessionAreaMap.get("area");
		List<Area> areas = new ArrayList<>();

		for (Object a : areaList) {

			Map<String, Object> areaMap = (Map<String, Object>) a;

			Area area = new Area();
			
			area.setId(areaMap.get("id").toString());
			area.setAreaId(Integer.valueOf(areaMap.get("areaId").toString()));
			area.setAreaName(areaMap.get("areaName").toString());
			area.setAreaCode(areaMap.get("areaCode").toString());
			area.setParentAreaId(Integer.valueOf(areaMap.get("parentAreaId").toString()));
			
			if (areaMap.get("blockId")!=null && !areaMap.get("blockId").toString().isEmpty())
				area.setBlockId(Integer.valueOf(areaMap.get("blockId").toString()));
			if (areaMap.get("districtId")!=null && !areaMap.get("districtId").toString().isEmpty())
				area.setDistrictId(Integer.valueOf(areaMap.get("districtId").toString()));
			if (areaMap.get("stateId")!=null && !areaMap.get("stateId").toString().isEmpty())
				area.setStateId(Integer.valueOf(areaMap.get("stateId").toString()));

			Map<String, Object> areaLevelMap = (Map<String, Object>) areaMap.get("areaLevel");

			AreaLevel areaLevel = new AreaLevel();
			areaLevel.setAreaLevelId(Integer.valueOf(areaLevelMap.get("areaLevelId").toString()));
			areaLevel.setAreaLevelName(areaLevelMap.get("areaLevelName").toString());
			area.setAreaLevel(areaLevel);

			areas.add(area);
		}
		user.setAreas(areas);
	
		
		// extracting designation slug-id from tokenInfoMap
		List<Integer> desgSlugIds =  (List<Integer>) (tokenInfoMap.get("desgSlugId"));
		user.setDesgSlugIds(desgSlugIds);
		user.setDesgnName( ((List<String>) (tokenInfoMap.get("designationNames"))).get(0));
		user.setAreaLevel( sessionAreaMap.get("areaLevel").toString());
		
		return user;
	}
}
