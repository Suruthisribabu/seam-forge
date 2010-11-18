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
package org.jboss.seam.forge.parser.java.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.jboss.seam.forge.parser.java.Annotation;
import org.jboss.seam.forge.parser.java.Field;
import org.jboss.seam.forge.parser.java.Import;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.Member;
import org.jboss.seam.forge.parser.java.Method;
import org.jboss.seam.forge.parser.java.SyntaxError;
import org.jboss.seam.forge.parser.java.Visibility;
import org.jboss.seam.forge.parser.java.ast.AnnotationAccessor;
import org.jboss.seam.forge.parser.java.ast.MethodFinderVisitor;
import org.jboss.seam.forge.parser.java.ast.ModifierAccessor;
import org.jboss.seam.forge.parser.java.ast.TypeDeclarationFinderVisitor;

/**
 * Represents a Java Source File
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaClassImpl implements JavaClass
{
   private static AnnotationAccessor annotations = new AnnotationAccessor();
   private final ModifierAccessor modifiers = new ModifierAccessor();

   private Document document;
   private CompilationUnit unit;

   /**
    * Parses and process the java source code as a compilation unit and the
    * result it abstract syntax tree (AST) representation and this action uses
    * the third edition of java Language Specification.
    * 
    * @param source - the java source to be parsed (i.e. the char[] contains
    *           Java source).
    * @return CompilationUnit Abstract syntax tree representation of a java
    *         source file.
    */
   public JavaClassImpl(final InputStream inputStream)
   {
      try
      {
         char[] source = Util.getInputStreamAsCharArray(inputStream, inputStream.available(), "ISO8859_1");
         init(source);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("InputStream must be a parsable java file: ", e);
      }
   }

   public JavaClassImpl(final String source)
   {
      this(source.toCharArray());
   }

   public JavaClassImpl(final char[] source)
   {
      init(source);
   }

   public JavaClassImpl()
   {
      this("public class JavaClass { }");
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void init(final char[] source)
   {
      document = new Document(new String(source));
      ASTParser parser = ASTParser.newParser(AST.JLS3);

      parser.setSource(document.get().toCharArray());
      Map options = JavaCore.getOptions();
      options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
      parser.setCompilerOptions(options);

      parser.setResolveBindings(true);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      unit = (CompilationUnit) parser.createAST(null);
      unit.recordModifications();

   }

   /*
    * Annotation modifiers
    */

   @Override
   public Annotation addAnnotation()
   {
      return annotations.addAnnotation(this, getTypeDeclaration());
   }

   @Override
   public Annotation addAnnotation(final Class<? extends java.lang.annotation.Annotation> clazz)
   {
      if (!this.hasImport(clazz))
      {
         this.addImport(clazz);
      }
      return annotations.addAnnotation(this, getTypeDeclaration(), clazz.getSimpleName());
   }

   @Override
   public Annotation addAnnotation(final String className)
   {
      return annotations.addAnnotation(this, getTypeDeclaration(), className);
   }

   @Override
   public List<Annotation> getAnnotations()
   {
      return annotations.getAnnotations(this, getTypeDeclaration());
   }

   @Override
   public boolean hasAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.hasAnnotation(this, getTypeDeclaration(), type.getName());
   }

   @Override
   public boolean hasAnnotation(final String type)
   {
      return annotations.hasAnnotation(this, getTypeDeclaration(), type);
   }

   @Override
   public JavaClass removeAnnotation(final Annotation annotation)
   {
      return annotations.removeAnnotation(this, getTypeDeclaration(), annotation);
   }

   @Override
   public Annotation getAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.getAnnotation(this, getTypeDeclaration(), type);
   }

   @Override
   public Annotation getAnnotation(final String type)
   {
      return annotations.getAnnotation(this, getTypeDeclaration(), type);
   }

   /*
    * Import modifiers
    */

   @Override
   @SuppressWarnings("unchecked")
   public Import addImport(final String className)
   {
      Import imprt = null;
      if (!hasImport(className))
      {
         imprt = new ImportImpl(this).setName(className);
         unit.imports().add(imprt.getInternal());
      }
      else
      {
         imprt = getImport(className);
      }
      return imprt;
   }

   @Override
   public Import getImport(final String className)
   {
      List<Import> imports = getImports();
      for (Import imprt : imports)
      {
         if (imprt.getName().equals(className))
         {
            return imprt;
         }
      }
      return null;
   }

   @Override
   public Import getImport(Class<?> type)
   {
      return getImport(type.getName());
   }

   @Override
   public Import addImport(final Class<?> type)
   {
      return addImport(type.getName());
   }

   @Override
   public JavaClass addImports(final Class<?>... types)
   {
      for (Class<?> type : types)
      {
         addImport(type.getName());
      }
      return this;
   }

   @Override
   public JavaClass addImports(final String... types)
   {
      for (String type : types)
      {
         addImport(type);
      }
      return this;
   }

   @Override
   public boolean hasImport(final Class<?> type)
   {
      return hasImport(type.getName());
   }

   @Override
   public boolean hasImport(final String type)
   {
      return getImport(type) != null;
   }

   @Override
   public JavaClass removeImport(final String name)
   {
      for (Import i : getImports())
      {
         if (i.getName().equals(name))
         {
            removeImport(i);
            break;
         }
      }
      return this;
   }

   @Override
   public JavaClass removeImport(final Class<?> clazz)
   {
      return removeImport(clazz.getName());
   }

   @Override
   public JavaClass removeImport(final Import imprt)
   {
      Object internal = imprt.getInternal();
      if (unit.imports().contains(internal))
      {
         unit.imports().remove(internal);
      }
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Import> getImports()
   {
      List<Import> results = new ArrayList<Import>();

      for (ImportDeclaration i : (List<ImportDeclaration>) unit.imports())
      {
         results.add(new ImportImpl(this, i));
      }

      return Collections.unmodifiableList(results);
   }

   /*
    * Field & Method modifiers
    */

   @Override
   @SuppressWarnings("unchecked")
   public Field addField()
   {
      Field field = new FieldImpl(this);
      getTypeDeclaration().bodyDeclarations().add(field.getInternal());
      return field;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Field addField(final String declaration)
   {
      Field field = new FieldImpl(this, declaration);
      getTypeDeclaration().bodyDeclarations().add(field.getInternal());
      return field;
   }

   @Override
   public List<Field> getFields()
   {
      List<Field> result = new ArrayList<Field>();

      for (FieldDeclaration field : getTypeDeclaration().getFields())
      {
         result.add(new FieldImpl(this, field));
      }

      return Collections.unmodifiableList(result);
   }

   @Override
   public Field getField(final String name)
   {
      for (Field field : getFields())
      {
         if (field.getName().equals(name))
         {
            return field;
         }
      }
      return null;
   }

   @Override
   public boolean hasField(final String name)
   {
      for (Field field : getFields())
      {
         if (field.getName().equals(name))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean hasField(final Field field)
   {
      return getFields().contains(field);
   }

   @Override
   public JavaClass removeField(final Field field)
   {
      getTypeDeclaration().bodyDeclarations().remove(field.getInternal());
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Method addMethod()
   {
      Method m = new MethodImpl(this);
      getTypeDeclaration().bodyDeclarations().add(m.getInternal());
      return m;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Method addMethod(final String method)
   {
      Method m = new MethodImpl(this, method);
      getTypeDeclaration().bodyDeclarations().add(m.getInternal());
      return m;
   }

   @Override
   public List<Method> getMethods()
   {
      List<Method> result = new ArrayList<Method>();

      MethodFinderVisitor methodFinderVisitor = new MethodFinderVisitor();
      unit.accept(methodFinderVisitor);

      List<MethodDeclaration> methods = methodFinderVisitor.getMethods();
      for (MethodDeclaration methodDeclaration : methods)
      {
         result.add(new MethodImpl(this, methodDeclaration));
      }
      return Collections.unmodifiableList(result);
   }

   @Override
   public JavaClass removeMethod(final Method method)
   {
      getTypeDeclaration().bodyDeclarations().remove(method.getInternal());
      return this;
   }

   @Override
   public List<Member<?>> getMembers()
   {
      List<Member<?>> result = new ArrayList<Member<?>>();

      result.addAll(getFields());
      result.addAll(getMethods());

      return result;
   }

   private TypeDeclaration getTypeDeclaration()
   {
      TypeDeclarationFinderVisitor typeDeclarationFinder = new TypeDeclarationFinderVisitor();
      unit.accept(typeDeclarationFinder);
      List<TypeDeclaration> typeDeclarations = typeDeclarationFinder.getTypeDeclarations();
      if (typeDeclarations.isEmpty())
      {
         throw new RuntimeException("A type-declaration is required in order to complete the current operation, but no type-declaration exists in compilation unit: " + unit.toString());
      }
      return typeDeclarations.get(0);
   }

   /*
    * Name modifiers
    */

   @Override
   public String getName()
   {
      return getTypeDeclaration().getName().getIdentifier();
   }

   @Override
   public JavaClass setName(final String name)
   {
      getTypeDeclaration().setName(unit.getAST().newSimpleName(name));
      updateConstructorNames();
      return this;
   }

   private void updateConstructorNames()
   {
      for (Method m : getMethods())
      {
         if (m.isConstructor())
         {
            m.setConstructor(false);
            m.setConstructor(true);
         }
      }
   }

   /*
    * Package modifiers
    */

   @Override
   public String getPackage()
   {
      PackageDeclaration pkg = unit.getPackage();
      if (pkg != null)
      {
         return pkg.getName().getFullyQualifiedName();
      }
      else
      {
         return null;
      }
   }

   @Override
   public JavaClass setPackage(final String name)
   {
      if (unit.getPackage() == null)
      {
         unit.setPackage(unit.getAST().newPackageDeclaration());
      }
      unit.getPackage().setName(unit.getAST().newName(name));
      return this;
   }

   @Override
   public JavaClass setDefaultPackage()
   {
      unit.setPackage(null);
      return this;
   }

   @Override
   public boolean isDefaultPackage()
   {
      return unit.getPackage() == null;
   }

   /*
    * Visibility modifiers
    */
   @Override
   public boolean isPackagePrivate()
   {
      return (!isPublic() && !isPrivate() && !isProtected());
   }

   @Override
   public JavaClass setPackagePrivate()
   {
      modifiers.clearVisibility(getTypeDeclaration());
      return this;
   }

   @Override
   public boolean isPublic()
   {
      return modifiers.hasModifier(getTypeDeclaration(), ModifierKeyword.PUBLIC_KEYWORD);
   }

   @Override
   public JavaClass setPublic()
   {
      modifiers.clearVisibility(getTypeDeclaration());
      modifiers.addModifier(getTypeDeclaration(), ModifierKeyword.PUBLIC_KEYWORD);
      return this;
   }

   @Override
   public boolean isPrivate()
   {
      return modifiers.hasModifier(getTypeDeclaration(), ModifierKeyword.PRIVATE_KEYWORD);
   }

   @Override
   public JavaClass setPrivate()
   {
      modifiers.clearVisibility(getTypeDeclaration());
      modifiers.addModifier(getTypeDeclaration(), ModifierKeyword.PRIVATE_KEYWORD);
      return this;
   }

   @Override
   public boolean isProtected()
   {
      return modifiers.hasModifier(getTypeDeclaration(), ModifierKeyword.PROTECTED_KEYWORD);
   }

   @Override
   public JavaClass setProtected()
   {
      modifiers.clearVisibility(getTypeDeclaration());
      modifiers.addModifier(getTypeDeclaration(), ModifierKeyword.PROTECTED_KEYWORD);
      return this;
   }

   @Override
   public Visibility getVisibility()
   {
      return Visibility.getFrom(this);
   }

   @Override
   public JavaClass setVisibility(Visibility scope)
   {
      return Visibility.set(this, scope);
   }

   /*
    * Type modifiers
    */

   @Override
   public boolean isAbstract()
   {
      return modifiers.hasModifier(getTypeDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
   }

   @Override
   public JavaClass setAbstract(final boolean abstrct)
   {
      if (abstrct)
      {
         modifiers.addModifier(getTypeDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
      }
      else
      {
         modifiers.removeModifier(getTypeDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
      }
      return this;
   }

   /*
    * Non-manipulation methods.
    */

   @Override
   public String toString()
   {
      return unit.toString();
   }

   @Override
   public Object getInternal()
   {
      return unit;
   }

   @Override
   public JavaClass getOrigin()
   {
      try
      {
         TextEdit edit = unit.rewrite(document, null);
         edit.apply(document);
      }
      catch (MalformedTreeException e)
      {
         throw new RuntimeException(e);
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }

      return this;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((toString() == null) ? 0 : unit.toString().hashCode());
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
      if (!this.toString().equals(obj.toString()))
      {
         return false;
      }
      return true;
   }

   @Override
   public List<SyntaxError> getSyntaxErrors()
   {
      List<SyntaxError> result = new ArrayList<SyntaxError>();

      IProblem[] problems = unit.getProblems();
      if (problems != null)
      {
         for (IProblem problem : problems)
         {
            result.add(new SyntaxErrorImpl(this, problem));
         }
      }
      return result;
   }

   @Override
   public boolean hasSyntaxErrors()
   {
      return !getSyntaxErrors().isEmpty();
   }

   @Override
   public String getQualifiedName()
   {
      String packg = getPackage();
      String name = getName();
      if ((packg != null) && !packg.isEmpty())
      {
         return packg + "." + name;
      }
      return name;
   }

}
