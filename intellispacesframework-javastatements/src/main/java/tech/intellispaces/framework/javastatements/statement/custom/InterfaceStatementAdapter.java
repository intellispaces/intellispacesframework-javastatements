package tech.intellispaces.framework.javastatements.statement.custom;

import tech.intellispaces.framework.javastatements.context.TypeContext;
import tech.intellispaces.framework.javastatements.session.Session;
import tech.intellispaces.framework.javastatements.statement.StatementType;
import tech.intellispaces.framework.javastatements.statement.StatementTypes;

import javax.lang.model.element.TypeElement;

class InterfaceStatementAdapter extends CustomTypeStatementAdapter implements InterfaceStatement {

  InterfaceStatementAdapter(TypeElement typeElement, TypeContext typeContext, Session session) {
    super(typeElement, typeContext, session);
  }

  @Override
  public StatementType statementType() {
    return StatementTypes.Interface;
  }
}
