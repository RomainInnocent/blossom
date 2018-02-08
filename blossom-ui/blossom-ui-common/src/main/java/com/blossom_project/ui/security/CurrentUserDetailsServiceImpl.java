package com.blossom_project.ui.security;

import com.blossom_project.core.association_user_role.AssociationUserRoleDTO;
import com.blossom_project.core.association_user_role.AssociationUserRoleService;
import com.blossom_project.core.user.UserDTO;
import com.blossom_project.core.user.UserService;
import com.blossom_project.ui.current_user.CurrentUser;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CurrentUserDetailsServiceImpl implements UserDetailsService {

  private final UserService userService;
  private final AssociationUserRoleService associationUserRoleService;

  public CurrentUserDetailsServiceImpl(UserService userService,
    AssociationUserRoleService associationUserRoleService) {
    this.userService = userService;
    this.associationUserRoleService = associationUserRoleService;
  }

  @Override
  public CurrentUser loadUserByUsername(String identifier) throws UsernameNotFoundException {
    UserDTO user = userService.getByIdentifier(identifier).orElseThrow(
      () -> new UsernameNotFoundException(
        String.format("User with email=%s was not found", identifier)));

    List<AssociationUserRoleDTO> associationsUserRoles = associationUserRoleService
      .getAllLeft(user);

    Set<String> mergedPrivileges = associationsUserRoles.stream()
      .flatMap(association -> association.getB().getPrivileges().stream()).collect(
        Collectors.toSet());

    return new CurrentUser(user, mergedPrivileges);
  }

}