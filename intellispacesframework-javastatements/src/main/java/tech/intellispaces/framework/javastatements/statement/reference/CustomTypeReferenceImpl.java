package tech.intellispaces.framework.javastatements.statement.reference;

import tech.intellispaces.framework.javastatements.statement.StatementType;
import tech.intellispaces.framework.javastatements.statement.StatementTypes;
import tech.intellispaces.framework.javastatements.statement.custom.CustomType;

import java.util.List;

class CustomTypeReferenceImpl extends AbstractTypeReference implements CustomTypeReference {
  private final CustomType targetType;
  private final List<NonPrimitiveTypeReference> typeArguments;

  CustomTypeReferenceImpl(CustomType targetType, List<NonPrimitiveTypeReference> typeArguments) {
    super();
    this.targetType = targetType;
    this.typeArguments = typeArguments;
  }

  @Override
  public StatementType statementType() {
    return StatementTypes.CustomTypeReference;
  }

  @Override
  public CustomType targetType() {
    return targetType;
  }

  @Override
  public List<NonPrimitiveTypeReference> typeArguments() {
    return typeArguments;
  }
}
