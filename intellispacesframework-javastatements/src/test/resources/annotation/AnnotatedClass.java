package tech.intellispaces.framework.javastatements.samples;

import tech.intellispaces.framework.javastatements.samples.AnnotationWithElementsView;
import tech.intellispaces.framework.javastatements.samples.TestAnnotation;
import tech.intellispaces.framework.javastatements.samples.TestEnum;
import tech.intellispaces.framework.javastatements.support.TesteeType;

public interface AnnotatedClass {

  @TesteeType
  @AnnotationWithElementsView(
      byteElement = 10,
      shortElement = 11,
      intElement = 12,
      longElement = 13,
      floatElement = 14.0f,
      doubleElement = 15.0,
      charElement = 'x',
      booleanElement = true,
      stringElement = "abcde",
      arrayOfIntElement = { 1, 2, 3, 4 , 5 },
      arrayOfStringElement = { "a", "b", "c", "d", "e" },
      arrayOfEnumElement = { TestEnum.Value1, TestEnum.Value2, TestEnum.Value3 },
      arrayOfClassElement = { Object.class, String.class },
      arrayOfAnnotationElement = { @TestAnnotation("abc"), @TestAnnotation("def") },
      enumElement = TestEnum.Value3
  )
  public class TesteeClass  {
  }
}
