package tech.intellispaces.framework.javastatements.statement.custom;

import tech.intellispaces.framework.javastatements.statement.reference.CustomTypeReference;

import java.util.List;
import java.util.Optional;

public interface RecordStatement extends CustomType {

  /**
   * Implemented interfaces.
   */
  List<CustomTypeReference> implementedInterfaces();

  @Override
  default Optional<RecordStatement> asRecord() {
    return Optional.of(this);
  }

  /**
   * Related custom type.
   */
  default CustomType asCustomType() {
    return this;
  }
}
