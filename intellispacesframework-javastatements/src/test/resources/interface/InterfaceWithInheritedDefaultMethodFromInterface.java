package tech.intellispaces.framework.javastatements.samples;

import tech.intellispaces.framework.javastatements.support.TesteeType;

public interface InterfaceWithInheritedDefaultMethodFromInterface {

  @TesteeType
  interface TesteeInterface extends Interface1 {
    void method1();
  }

  interface Interface1 {
    default void method2() {}
  }
}
