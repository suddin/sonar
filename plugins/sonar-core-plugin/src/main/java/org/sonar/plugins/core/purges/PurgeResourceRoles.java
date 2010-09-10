/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.core.purges;

import org.sonar.core.purge.AbstractPurge;
import org.sonar.api.batch.PurgeContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.security.GroupRole;
import org.sonar.api.security.UserRole;

import javax.persistence.Query;
import java.util.List;

/**
 * @since 1.12
 */
public class PurgeResourceRoles extends AbstractPurge {

  public PurgeResourceRoles(DatabaseSession session) {
    super(session);
  }

  public void purge(PurgeContext context) {
    deleteRoles(UserRole.class.getSimpleName());
    deleteRoles(GroupRole.class.getSimpleName());
  }

  private void deleteRoles(String classname) {
    Query query = getSession().createQuery("SELECT rol.id FROM " + classname + " rol "
        + " WHERE rol.resourceId IS NOT NULL AND NOT EXISTS(FROM " + ResourceModel.class.getSimpleName() + " r WHERE r.id=rol.resourceId)");
    List<Integer> roleIds = (List<Integer>) query.getResultList();
    if (!roleIds.isEmpty()) {
      executeQuery(roleIds, "delete from " + classname + " rol where rol.id in (:ids)");
    }
  }
}