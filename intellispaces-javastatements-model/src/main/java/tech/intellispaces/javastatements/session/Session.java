package tech.intellispaces.javastatements.session;

import tech.intellispaces.javastatements.customtype.CustomType;

/**
 * Parsing session.
 */
public interface Session {

  CustomType getType(String typeName);

  void putType(String typeName, CustomType customType);

  void clear();
}
