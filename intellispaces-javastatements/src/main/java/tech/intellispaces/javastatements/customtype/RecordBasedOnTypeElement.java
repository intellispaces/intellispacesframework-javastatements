package tech.intellispaces.javastatements.customtype;

import tech.intellispaces.actions.Actions;
import tech.intellispaces.actions.getter.Getter;
import tech.intellispaces.javastatements.StatementType;
import tech.intellispaces.javastatements.StatementTypes;
import tech.intellispaces.javastatements.context.TypeContext;
import tech.intellispaces.javastatements.reference.CustomTypeReference;
import tech.intellispaces.javastatements.session.Session;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * Adapter of {@link TypeElement} to {@link RecordType}.
 */
class RecordBasedOnTypeElement
    extends AbstractCustomTypeStatementBasedOnTypeElement
    implements RecordType
{
  private final Getter<List<CustomTypeReference>> implementedInterfacesGetter;

  RecordBasedOnTypeElement(TypeElement typeElement, TypeContext typeContext, Session session) {
    super(typeElement, typeContext, session);
    this.implementedInterfacesGetter = Actions.cachedLazyGetter(CustomTypeFunctions::getImplementedInterfaces, this);
  }

  @Override
  public StatementType statementType() {
    return StatementTypes.Record;
  }

  @Override
  public List<CustomTypeReference> implementedInterfaces() {
    return implementedInterfacesGetter.get();
  }
}
