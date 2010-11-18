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

package org.jboss.seam.forge.project.util;

import java.io.File;
import java.util.List;

import org.jboss.seam.forge.project.Resource;
import org.jboss.seam.forge.project.services.ResourceFactory;

/**
 * A set of utilities to work with the resources API.
 *
 * @author Mike Brock
 */
public class ResourceUtil
{
   /**
    * A simple utility method to locate the outermost contextual File reference for the specified resource.
    *
    * @param r resource instance.
    * @return outermost relevant file context.
    */
   public static File getContextFile(Resource<?> r)
   {
      do
      {
         Object o = r.getUnderlyingResourceObject();
         if (o instanceof File)
         {
            return (File) r.getUnderlyingResourceObject();
         }

      }
      while ((r = r.getParent()) != null);

      return null;
   }

   public static File getContextDirectory(final Resource<?> r)
   {
      final File ctx = getContextFile(r);
      if ((ctx != null) && !ctx.isDirectory())
      {
         return ctx.getParentFile();
      }
      return ctx;
   }



   public static List<Resource<?>> parsePathspec(final ResourceFactory factory, final Resource<?> resource,
                                                 final String pathspec)
   {
      return new PathspecParser(factory, resource, pathspec).resolve();
   }

   public static boolean isChildOf(final Resource<?> parent, final Resource<?> isChild)
   {
      Resource<?> r = isChild;
      while ((r = r.getParent()) != null)
      {
         if (r.equals(parent))
         {
            return true;
         }
      }
      return false;
   }

}
