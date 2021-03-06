#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2011 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
class DeleteDeprecatedLibraries < ActiveRecord::Migration

  def self.up
    say_with_time "Deleting deprecated dependencies on libs..." do
      Dependency.delete_all("from_scope='LIB' OR to_scope='LIB'")
    end

    say_with_time "Deleting deprecated snapshots on libs..." do
      Snapshot.delete_all("scope='LIB'")
    end

    say_with_time "Deleting deprecated libs..." do
      Project.delete_all("scope='LIB'")
    end
  end

  def self.down

  end
end
