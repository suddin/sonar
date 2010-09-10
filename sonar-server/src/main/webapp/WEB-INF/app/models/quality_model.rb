#
# Sonar, entreprise quality control tool.
# Copyright (C) 2009 SonarSource SA
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
class QualityModel < ActiveRecord::Base

  validates_length_of :name, :within => 1..100
  validates_uniqueness_of :name

  has_many :characteristics, :dependent => :delete_all

  def root_characteristics
    @roots ||=
      begin
        characteristics.select do |c|
          c.parents.empty?
        end
      end
  end

  def characteristics_with_rule
    @characteristics_with_rule ||=
      begin
        characteristics.select do |c|
          c.rule
        end
      end
  end
end