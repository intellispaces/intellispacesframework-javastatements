package tech.intellispaces.javastatements.samples;

import tech.intellispaces.javastatements.support.TesteeType;

import java.util.List;

@TesteeType
public interface InterfaceWithMethodUsingLocalTypeParameter {

  <T> List<T> methodUsingLocalTypeParameter(T arg);
}