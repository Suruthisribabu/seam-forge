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

package org.jboss.seam.forge.project.facets.builtin;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.jboss.seam.forge.project.Facet;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.constraints.RequiresFacets;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.MetadataFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Dependent
@Named("forge.maven.MetadataFacet")
@RequiresFacets({ MavenCoreFacet.class })
public class MavenMetadataFacet implements MetadataFacet
{
   private Project project;

   @Override
   public String getProjectName()
   {
      return project.getFacet(MavenCoreFacet.class).getPOM().getArtifactId();
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(Project project)
   {
      this.project = project;
   }

   @Override
   public Facet install()
   {
      project.registerFacet(this);
      return this;
   }

   @Override
   public boolean isInstalled()
   {
      return project.hasFacet(MavenCoreFacet.class);
   }
}
