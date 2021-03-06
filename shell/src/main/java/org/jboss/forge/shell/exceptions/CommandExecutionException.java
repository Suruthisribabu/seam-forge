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

package org.jboss.forge.shell.exceptions;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CommandExecutionException extends ShellExecutionException
{
   private static final long serialVersionUID = -6474891123733228235L;
   private final CommandMetadata command;

   public CommandExecutionException(final CommandMetadata command, final String message)
   {
      super(message);
      this.command = command;
   }

   public CommandExecutionException(final CommandMetadata command, final Throwable e)
   {
      super(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
      this.command = command;
   }

   public CommandExecutionException(final CommandMetadata command, final String message, final Throwable e)
   {
      super(message, e);
      this.command = command;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }
}
