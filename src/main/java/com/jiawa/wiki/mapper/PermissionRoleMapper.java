package com.jiawa.wiki.mapper;

import com.jiawa.wiki.domain.Role;
import java.util.List;

public interface PermissionRoleMapper {
    List<Role> selectAll();
    Role selectById(Long id);
}
