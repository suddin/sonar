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
package org.sonar.batch.bootstrapper;


import java.util.Collections;
import java.util.List;

/**
 * Defines order of projects.
 * This is a part of bootstrap process, so we should take care about backward compatibility.
 * 
 * @since 2.6
 */
public class Reactor {

  private List<ProjectDefinition> projects;

  public Reactor(ProjectDefinition project) {
    this.projects = Collections.singletonList(project);
  }

  public Reactor(List<ProjectDefinition> sortedProjects) {
    this.projects = sortedProjects;
  }

  public List<ProjectDefinition> getSortedProjects() {
    return projects;
  }

}