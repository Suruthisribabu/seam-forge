/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.forge.project.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.forge.project.PackagingType;

/**
 * This class is internal; instead use {@link DependencyBuilder} for {@link Dependency} creation & instantiation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DependencyImpl implements Dependency
{
   private ScopeType scopeType;
   private String version;
   private String groupId;
   private String artifactId;
   private PackagingType packagingType;
   private List<Dependency> excludedDependencies = new ArrayList<Dependency>();

   DependencyImpl()
   {
   }

   @Override
   public String getArtifactId()
   {
      return artifactId;
   }

   @Override
   public String getGroupId()
   {
      return groupId;
   }

   @Override
   public String getVersion()
   {
      return version;
   }

   @Override
   public ScopeType getScopeType()
   {
      return scopeType;
   }

   public void setScopeType(final ScopeType scope)
   {
      this.scopeType = scope;
   }

   public void setVersion(final String version)
   {
      this.version = version;
   }

   public void setGroupId(final String groupId)
   {
      this.groupId = groupId;
   }

   public void setArtifactId(final String artifactId)
   {
      this.artifactId = artifactId;
   }

   @Override
   public List<Dependency> getExcludedDependencies()
   {
      return excludedDependencies;
   }

   public void setExcludedDependencies(final List<Dependency> excludedDependencies)
   {
      this.excludedDependencies = excludedDependencies;
   }

   public PackagingType getPackagingType()
   {
      return packagingType;
   }

   public void setPackagingType(final PackagingType packagingType)
   {
      this.packagingType = packagingType;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((packagingType == null) ? 0 : packagingType.hashCode());
      result = prime * result + ((scopeType == null) ? 0 : scopeType.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      DependencyImpl other = (DependencyImpl) obj;
      if (artifactId == null)
      {
         if (other.artifactId != null)
         {
            return false;
         }
      }
      else if (!artifactId.equals(other.artifactId))
      {
         return false;
      }
      if (groupId == null)
      {
         if (other.groupId != null)
         {
            return false;
         }
      }
      else if (!groupId.equals(other.groupId))
      {
         return false;
      }
      if (packagingType != other.packagingType)
      {
         return false;
      }
      if (scopeType != other.scopeType)
      {
         return false;
      }
      if (version == null)
      {
         if (other.version != null)
         {
            return false;
         }
      }
      else if (!version.equals(other.version))
      {
         return false;
      }
      return true;
   }

}
