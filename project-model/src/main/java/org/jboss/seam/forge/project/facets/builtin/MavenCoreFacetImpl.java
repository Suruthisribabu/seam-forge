/*
 * JBoss, by Red Hat.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.seam.forge.project.Facet;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.ProjectModelException;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Named("forge.maven.MavenCoreFacet")
public class MavenCoreFacetImpl implements MavenCoreFacet, Facet
{

   private Project project;
   private ProjectBuildingResult buildingResult;
   private final MavenContainer container;

   @Inject
   public MavenCoreFacetImpl(MavenContainer container)
   {
      this.container = container;
   }

   /*
    * POM manipulation methods
    */
   @Override
   public ProjectBuildingResult getProjectBuildingResult()
   {
      // FIXME This method is SLOW: about 2-5 seconds/call (needs optimization!)
      try
      {
         if (this.buildingResult == null)
         {
            buildingResult = container.getBuilder().build(getPOMFile(), container.getRequest());
         }
         return buildingResult;
      }
      catch (ProjectBuildingException e)
      {
         throw new ProjectModelException(e);
      }
   }

   private void invalidateBuildingResult()
   {
      this.buildingResult = null;
   }

   @Override
   public Model getPOM()
   {
      try
      {
         Model result = new Model();

         MavenXpp3Reader reader = new MavenXpp3Reader();
         FileInputStream stream = new FileInputStream(getPOMFile());
         if (stream.available() > 0)
         {
            result = reader.read(stream);
         }
         stream.close();

         result.setPomFile(getPOMFile());
         return result;
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not open POM file: " + getPOMFile(), e);
      }
      catch (XmlPullParserException e)
      {
         throw new ProjectModelException("Could not parse POM file: " + getPOMFile(), e);
      }
   }

   @Override
   public void setPOM(final Model pom)
   {
      try
      {
         MavenXpp3Writer writer = new MavenXpp3Writer();
         FileWriter fw = new FileWriter(getPOMFile());
         writer.write(fw, pom);
         fw.close();
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not write POM file: " + getPOMFile(), e);
      }
      invalidateBuildingResult();
   }

   private Model createPOM()
   {
      File pomFile = getPOMFile();
      if (!pomFile.exists())
      {
         project.writeFile(new char[] {}, pomFile);
      }
      Model pom = getPOM();
      pom.setGroupId("org.jboss.seam");
      pom.setArtifactId("scaffolding");
      pom.setVersion("1.0.0-SNAPSHOT");
      pom.setPomFile(getPOMFile());
      pom.setModelVersion("4.0.0");
      setPOM(pom);
      return pom;
   }

   private File getPOMFile()
   {
      File file = new File(project.getProjectRoot() + File.separator + "pom.xml");
      return file;
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(final Project project)
   {
      this.project = project;
   }

   @Override
   public boolean isInstalled()
   {
      return getPOMFile().exists();
   }

   @Override
   public Facet install()
   {
      createPOM();
      project.registerFacet(this);
      return this;
   }

   @Override
   public MavenProject getMavenProject()
   {
      return getProjectBuildingResult().getProject();
   }
}
