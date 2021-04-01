/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 24-Jul-2019 3:42:18 PM
 */
package org.sdrc.rmncha.controller;

import org.sdrc.rmncha.model.UserModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sarita Panigrahi email-sari.panigrahi@gmail.com 24-Jul-2019 3:42:18
 *         PM
 */

@RestController
@RequestMapping("/me")
public class PrincipalResource {

//	@PreAuthorize("hasRole('ROLE_USER')")
	public UserModel getPrincipal() {
		return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
