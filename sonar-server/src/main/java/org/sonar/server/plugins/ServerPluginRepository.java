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
package org.sonar.server.plugins;

import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.sonar.api.Plugin;
import org.sonar.api.ServerExtension;
import org.sonar.core.plugin.JpaPluginDao;
import org.sonar.core.plugin.JpaPlugin;
import org.sonar.api.platform.PluginRepository;

import java.io.IOException;

/**
 * @since 2.2
 */
public class ServerPluginRepository extends PluginRepository {

  private JpaPluginDao dao;
  private PluginClassLoaders classloaders;

  public ServerPluginRepository(JpaPluginDao dao, PluginClassLoaders classloaders) {
    this.dao = dao;
    this.classloaders = classloaders;
  }

  public void registerPlugins(MutablePicoContainer pico) throws ClassNotFoundException, IllegalAccessException,
      InstantiationException, IOException {
    for (JpaPlugin jpaPlugin : dao.getPlugins()) {
      Class pluginClass = classloaders.getClassLoader(jpaPlugin.getKey()).loadClass(jpaPlugin.getPluginClass());
      pico.as(Characteristics.CACHE).addComponent(pluginClass);
      Plugin plugin = (Plugin) pico.getComponent(pluginClass);
      registerPlugin(pico, plugin, ServerExtension.class);
    }
  }

}