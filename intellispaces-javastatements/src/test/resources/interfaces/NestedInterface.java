package tech.intellispaces.javastatements.samples;

import tech.intellispaces.javastatements.support.TesteeType;

public interface NestedInterface {

  public static interface NestedInterface1 {

    @TesteeType
    public static interface NestedInterface2 {

    }
  }
}