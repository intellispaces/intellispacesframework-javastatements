package tech.intellispaces.javastatements.customtype;

import tech.intellispaces.javastatements.StatementTypes;
import tech.intellispaces.javastatements.context.ContextTypeParameter;
import tech.intellispaces.javastatements.context.NameContextFunctions;
import tech.intellispaces.javastatements.context.TypeContext;
import tech.intellispaces.javastatements.exception.JavaStatementException;
import tech.intellispaces.javastatements.method.MethodParam;
import tech.intellispaces.javastatements.method.MethodParams;
import tech.intellispaces.javastatements.method.MethodSignature;
import tech.intellispaces.javastatements.method.MethodSignatures;
import tech.intellispaces.javastatements.method.MethodStatement;
import tech.intellispaces.javastatements.method.MethodStatements;
import tech.intellispaces.javastatements.reference.CustomTypeReference;
import tech.intellispaces.javastatements.reference.NamedReference;
import tech.intellispaces.javastatements.reference.NotPrimitiveReference;
import tech.intellispaces.javastatements.reference.ThrowableReference;
import tech.intellispaces.javastatements.reference.TypeReference;
import tech.intellispaces.javastatements.reference.TypeReferenceFunctions;
import tech.intellispaces.javastatements.session.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Custom type related functions.
 */
public interface CustomTypeFunctions {

  static Optional<CustomTypeReference> getExtendedClass(CustomType statement) {
    return statement.parentTypes().stream()
        .filter(ref -> StatementTypes.Class.equals(ref.targetType().statementType()))
        .reduce((ref1, ref2) -> {
          throw JavaStatementException.withMessage("Multiple extended classes: {}, {}", ref1, ref2);
        });
  }

  static List<CustomTypeReference> getImplementedInterfaces(CustomType statement) {
    return statement.parentTypes().stream()
        .filter(t -> StatementTypes.Interface.equals(t.targetType().statementType()))
        .toList();
  }

  static List<MethodStatement> getActualMethods(CustomType customType, TypeContext typeContext, Session session) {
    List<MethodStatement> actualMethods = new ArrayList<>();
    customType.declaredMethods().stream()
        .map(method -> getEffectiveMethod(method, typeContext))
        .forEach(actualMethods::add);
    inheritedMethods(customType, actualMethods, typeContext);
    return actualMethods;
  }

  private static MethodStatement getEffectiveMethod(MethodStatement originMethod, TypeContext typeContext) {
    return MethodStatements.build(
        originMethod.owner(),
        getEffectiveMethodSignature(originMethod.signature(), typeContext)
    );
  }

  private static MethodSignature getEffectiveMethodSignature(
      MethodSignature originMethodSignature, TypeContext typeContext
  ) {
    return MethodSignatures.build()
        .isAbstract(originMethodSignature.isAbstract())
        .isPublic(originMethodSignature.isPublic())
        .isDefault(originMethodSignature.isDefault())
        .isStatic(originMethodSignature.isStatic())
        .name(originMethodSignature.name())
        .annotations(originMethodSignature.annotations())
        .returnType(originMethodSignature.returnType().isPresent()
            ? getActualTypeReference(originMethodSignature.returnType().orElseThrow(), typeContext) : null
        )
        .defaultValue(originMethodSignature.defaultValue().orElse(null))
        .typeParameters(
            originMethodSignature.typeParameters().stream()
                .map(e -> (NamedReference) getActualTypeReference(e, typeContext))
                .toList()
        )
        .params(originMethodSignature.params().stream()
            .map(p -> MethodParams.build()
                .name(p.name())
                .type(getActualTypeReference(p.type(), typeContext))
                .get())
            .toList()
        )
        .exceptions(originMethodSignature.exceptions().stream()
            .map(e -> (ThrowableReference) getActualTypeReference(e, typeContext))
            .toList()
        )
        .get();
  }

  private static void inheritedMethods(
      CustomType customType, List<MethodStatement> allMethods, TypeContext typeContext
  ) {
    customType.parentTypes().forEach(parent ->
        extractMethods(parent, allMethods, typeContext));
  }

  private static void extractMethods(
      CustomTypeReference customTypeReference, List<MethodStatement> allMethods, TypeContext typeContext
  ) {
    CustomType customType = customTypeReference.targetType();
    TypeContext actualNameContext = NameContextFunctions.getActualNameContext(
        typeContext, customType.typeParameters(), customTypeReference.typeArguments()
    );
    customType.declaredMethods().forEach(
        method -> addMethod(method, allMethods, actualNameContext)
    );
    inheritedMethods(customType, allMethods, actualNameContext);
  }

  private static void addMethod(
      MethodStatement addedMethod, List<MethodStatement> allMethods, TypeContext typeContext
  ) {
    MethodStatement effectiveAddedMethod = getEffectiveMethod(addedMethod, typeContext);
    MethodSignature effectiveAddedSignature = effectiveAddedMethod.signature();
    int index = 0;
    for (MethodStatement method : allMethods) {
      MethodSignature methodSignature = method.signature();
      if (effectiveAddedSignature.name().equals(methodSignature.name())) {
        if (isSameParams(effectiveAddedSignature, methodSignature)) {
          if (effectiveAddedSignature.returnType().isEmpty() && methodSignature.returnType().isEmpty()) {
            // Ignore override method
            return;
          }
          TypeReference methodReturnTypeReference = methodSignature.returnType().get();
          TypeReference effectiveAddedMethodReturnTypeReference = effectiveAddedSignature.returnType().get();
          Optional<TypeReference> narrowType = TypeReferenceFunctions.narrowestOf(
              methodReturnTypeReference, effectiveAddedMethodReturnTypeReference
          );
          if (narrowType.isEmpty()) {
            throw JavaStatementException.withMessage("Incompatible types: {} and {} of method {}",
                methodReturnTypeReference, effectiveAddedMethodReturnTypeReference, methodSignature.name());
          }
          if (narrowType.get() == effectiveAddedMethodReturnTypeReference
              && AnnotationFunctions.hasAnnotation(effectiveAddedMethod.signature(), Override.class)
          ) {
            // Replace override method
            allMethods.set(index, effectiveAddedMethod);
          }
          // Ignore override method
          return;
        }
      }
      index++;
    }
    allMethods.add(effectiveAddedMethod);
  }

  private static TypeReference getActualTypeReference(TypeReference typeReference, TypeContext typeContext) {
    if (typeReference.asNamedReference().isPresent())  {
      NamedReference namedReference = typeReference.asNamedReference().orElseThrow();
      Optional<NotPrimitiveReference> actualType = typeContext
          .get(namedReference.name())
          .map(ContextTypeParameter::actualType);
      if (actualType.isPresent()) {
        return actualType.get();
      }
    }
    return typeReference;
  }

  private static boolean isSameParams(MethodSignature addedMethod, MethodSignature otherMethod) {
    boolean sameParams = true;
    Iterator<MethodParam> testMethodParams = addedMethod.params().iterator();
    Iterator<MethodParam> methodParams = otherMethod.params().iterator();
    while (testMethodParams.hasNext() && methodParams.hasNext()) {
      if (!TypeReferenceFunctions.isEqualTypes(testMethodParams.next().type(), methodParams.next().type())) {
        sameParams = false;
        break;
      }
    }
    if (testMethodParams.hasNext() || methodParams.hasNext()) {
      sameParams = false;
    }
    return sameParams;
  }

  static List<CustomType> allParents(CustomType customType) {
    List<CustomType> curParents = customType.parentTypes().stream()
        .map(CustomTypeReference::targetType)
        .toList();
    List<CustomType> parents = new ArrayList<>(curParents);
    curParents.forEach(p -> populateParents(p, parents));
    return parents;
  }

  private static void populateParents(CustomType customType, List<CustomType> parents) {
    var curParents = customType.parentTypes().stream()
        .map(CustomTypeReference::targetType)
        .toList();
    parents.addAll(curParents);
    curParents.forEach(p -> populateParents(p, parents));
  }

  static boolean hasParent(CustomType customType, String parentCanonicalName) {
    for (CustomTypeReference parent : customType.parentTypes()) {
      if (parentCanonicalName.equals(parent.targetType().canonicalName())) {
        return true;
      }
    }
    for (CustomTypeReference parent : customType.parentTypes()) {
      if (parent.targetType().hasParent(parentCanonicalName)) {
        return true;
      }
    }
    return false;
  }

  static String getTypeParametersDeclaration(CustomType customType, boolean fullDeclaration) {
    var parametersSource = customType.typeParameters().stream()
        .map(param -> TypeReferenceFunctions.getNamedTypeReferenceDeclaration(param, fullDeclaration))
        .collect(Collectors.joining(", "));
    return (parametersSource.isEmpty() ? "" : "<" + parametersSource + ">");
  }
}
