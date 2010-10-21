package org.jboss.seam.forge.arquillian.test.plugins;

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

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.forge.arquillian.AbstractTestBase;
import org.jboss.seam.forge.project.facets.MavenFacet;
import org.jboss.seam.forge.shell.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class NewProfilePluginTest extends AbstractTestBase
{

   @Test
   public void shouldBeAbleToCreateAProfile() throws Exception
   {
      String profileName = "jbossas-remote-6";

      Shell shell = getShell();
      shell.execute("new-test-profile --named " + profileName + " --container jbossas --type remote --version 6.0.0.20100911-M5");
      
      MavenFacet maven = getProject().getFacet(MavenFacet.class);
      Assert.assertEquals(
            "Verify a Profile with the same id was added",
            profileName, 
            maven.getPOM().getProfiles().get(0).getId());
   }

   @Test
   public void shouldThrowExceptionOnMissingProfileCreator() throws Exception
   {
      String profileName = "jbossas-remote-6";

      Shell shell = getShell();
      shell.execute("new-test-profile --named " + profileName + " --container missing-contianer --type remote --version 6.0.0.20100911-M5");

      MavenFacet maven = getProject().getFacet(MavenFacet.class);
      Assert.assertEquals(
            "Verify no Profiles were added",
            0, maven.getPOM().getProfiles().size());
   }

   @Test
   public void shouldThrowExceptionOnExistingProfile() throws Exception
   {
      String profileName = "duplicate-profile-name";

      Shell shell = getShell();
      shell.execute("new-test-profile --named " + profileName + " --container jbossas --type remote --version 6.0.0.20100911-M5");
      shell.execute("new-test-profile --named " + profileName + " --container jbossas --type remote --version 6.0.0.20100911-M5");

      MavenFacet maven = getProject().getFacet(MavenFacet.class);
      Assert.assertEquals(
            "Verify no Profiles were added",
            1, maven.getPOM().getProfiles().size());
   }
}
