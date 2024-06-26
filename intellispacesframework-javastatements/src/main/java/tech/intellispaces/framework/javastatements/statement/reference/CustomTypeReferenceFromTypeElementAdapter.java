package tech.intellispaces.framework.javastatements.statement.reference;

import tech.intellispaces.framework.commons.action.ActionBuilders;
import tech.intellispaces.framework.commons.action.Getter;
import tech.intellispaces.framework.javastatements.context.TypeContext;
import tech.intellispaces.framework.javastatements.session.Session;
import tech.intellispaces.framework.javastatements.statement.StatementType;
import tech.intellispaces.framework.javastatements.statement.StatementTypes;
import tech.intellispaces.framework.javastatements.statement.TypeElementFunctions;
import tech.intellispaces.framework.javastatements.statement.custom.CustomType;

import javax.lang.model.element.TypeElement;
import java.util.List;

class CustomTypeReferenceFromTypeElementAdapter extends AbstractTypeReference implements CustomTypeReference {
  private final Getter<CustomType> targetTypeGetter;

  CustomTypeReferenceFromTypeElementAdapter(TypeElement typeElement, TypeContext typeContext, Session session) {
    super();
    this.targetTypeGetter = ActionBuilders.cachedLazyGetter(TypeElementFunctions::asCustomTypeStatement, typeElement, session);
  }

  @Override
  public StatementType statementType() {
    return StatementTypes.CustomTypeReference;
  }

  @Override
  public CustomType targetType() {
    return targetTypeGetter.get();
  }

  @Override
  public List<NonPrimitiveTypeReference> typeArguments() {
    return List.of();
  }
}
