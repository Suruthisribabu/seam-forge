package org.jboss.seam.forge.test.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;
import org.jboss.weld.extensions.log.Category;

public class LoggerProducer
{
   @Produces
   public Logger produceLog(final InjectionPoint injectionPoint)
   {
      if (injectionPoint.getAnnotated().isAnnotationPresent(Category.class))
      {
         String category = injectionPoint.getAnnotated().getAnnotation(Category.class).value();
         return Logger.getLogger(category);
      }
      else
      {
         return Logger.getLogger(injectionPoint.getMember().getDeclaringClass());
      }
   }
}