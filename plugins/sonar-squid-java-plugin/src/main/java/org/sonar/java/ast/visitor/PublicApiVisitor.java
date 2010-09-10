/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.ast.visitor;

import java.util.Arrays;
import java.util.List;

import org.sonar.squid.api.SourceCode;
import org.sonar.squid.measures.Metric;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.Scope;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class PublicApiVisitor extends JavaAstVisitor {

  final static String OVERRIDE_ANNOTATION_KEYWORD = "Override";

  private static final List<Integer> TOKENS = Arrays.asList(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF,
      TokenTypes.CTOR_DEF, TokenTypes.ANNOTATION_DEF, TokenTypes.ANNOTATION_FIELD_DEF, TokenTypes.VARIABLE_DEF);

  public PublicApiVisitor() {
  }

  @Override
  public List<Integer> getWantedTokens() {
    return TOKENS;
  }

  @Override
  public void visitToken(DetailAST ast) {
    SourceCode currentResource = peekSourceCode();
    if (isPublic(ast) && !isStaticFinalVariable(ast) && !isMethodWithOverrideAnnotation(ast) && !isEmptyDefaultConstructor(ast)) {
      currentResource.add(Metric.PUBLIC_API, 1);
      if (isDocumentedApi(ast)) {
        currentResource.add(Metric.PUBLIC_DOC_API, 1);
      }
    }
  }

  private boolean isEmptyDefaultConstructor(DetailAST ast) {
    return (isConstructorWithoutParameters(ast)) && (ast.getLastChild().getChildCount() == 1);
  }

  private boolean isConstructorWithoutParameters(DetailAST ast) {
    return ast.getType() == TokenTypes.CTOR_DEF && ast.findFirstToken(TokenTypes.PARAMETERS).getChildCount() == 0;
  }

  private boolean isMethodWithOverrideAnnotation(DetailAST ast) {
    if (isMethod(ast)) {
      DetailAST modifier = ast.findFirstToken(TokenTypes.MODIFIERS);
      for (AST annotation = modifier.getFirstChild(); annotation != null; annotation = annotation.getNextSibling()) {
        if (isAnnotation(annotation) && ((DetailAST) annotation).findFirstToken(TokenTypes.IDENT) != null) {
          String name = ((DetailAST) annotation).findFirstToken(TokenTypes.IDENT).getText();
          return OVERRIDE_ANNOTATION_KEYWORD.equals(name);
        }
      }
    }
    return false;
  }

  private boolean isAnnotation(AST annotation) {
    return annotation.getType() == TokenTypes.ANNOTATION;
  }

  private boolean isMethod(DetailAST ast) {
    return ast.getType() == TokenTypes.METHOD_DEF;
  }

  private boolean isPublic(DetailAST ast) {
    return (AstUtils.isScope(AstUtils.getScope(ast), Scope.PUBLIC) || AstUtils.isType(ast, TokenTypes.ANNOTATION_FIELD_DEF));
  }

  private boolean isDocumentedApi(DetailAST ast) {
    return getFileContents().getJavadocBefore(ast.getLineNo()) != null;
  }

  private boolean isStaticFinalVariable(DetailAST ast) {
    return (AstUtils.isClassVariable(ast) || AstUtils.isInterfaceVariable(ast)) && AstUtils.isFinal(ast) && AstUtils.isStatic(ast);
  }
}