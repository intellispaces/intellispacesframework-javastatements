package tech.intellispaces.javastatements.samples;

import tech.intellispaces.javastatements.support.TesteeType;

public interface EnumWithInheritedDefaultMethodFromInterface {

  @TesteeType
  enum TesteeEnum implements Interface1 {
    ;
    public void method1() {}
  }

  interface Interface1 {
    default void method2() {}
  }
}
