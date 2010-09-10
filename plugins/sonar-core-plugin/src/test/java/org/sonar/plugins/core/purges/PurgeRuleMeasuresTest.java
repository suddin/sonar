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

import org.junit.Test;
import org.sonar.api.batch.PurgeContext;
import org.sonar.jpa.test.AbstractDbUnitTestCase;

public class PurgeRuleMeasuresTest extends AbstractDbUnitTestCase {

  @Test
  public void purgeRuleMeasures() {
    setupData("sharedFixture", "purgeRuleMeasures");

    new PurgeRuleMeasures(getSession()).purge(new PurgeContext(){
      public Integer getPreviousSnapshotId() {
        return 1;
      }
      public Integer getLastSnapshotId() {
        return 4;
      }
    });

    checkTables("purgeRuleMeasures", "snapshots", "project_measures", "measure_data");
  }
}