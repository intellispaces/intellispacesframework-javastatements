package tech.intellispaces.framework.javastatements.statement.method;

import tech.intellispaces.framework.commons.action.Executor;
import tech.intellispaces.framework.commons.action.string.StringActions;
import tech.intellispaces.framework.commons.function.Consumers;
import tech.intellispaces.framework.javastatements.statement.type.NamedType;
import tech.intellispaces.framework.javastatements.statement.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MethodSignatureDeclarationBuilderBasedOnMethodPrototype {
  private final MethodStatement prototype;
  private String methodName;
  private boolean includeMethodTypeParams;
  private boolean includeOwnerTypeParams ;
  private List<String> additionalParams;

  MethodSignatureDeclarationBuilderBasedOnMethodPrototype(MethodStatement prototype) {
    this.prototype = prototype;
    this.methodName = prototype.name();
    this.includeMethodTypeParams = true;
    this.includeOwnerTypeParams = false;
    this.additionalParams = null;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype methodName(String name) {
    this.methodName = name;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype includeMethodTypeParams(
      boolean includeMethodTypeParams
  ) {
    this.includeMethodTypeParams = includeMethodTypeParams;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype includeOwnerTypeParams(
      boolean includeOwnerTypeParams
  ) {
    this.includeOwnerTypeParams = includeOwnerTypeParams;
    return this;
  }

  public MethodSignatureDeclarationBuilderBasedOnMethodPrototype addAdditionalParams(
      List<String> additionalParam
  ) {
    if (this.additionalParams == null) {
      this.additionalParams = new ArrayList<>();
    }
    this.additionalParams.addAll(additionalParam);
    return this;
  }

  public String get() {
    return get(Consumers.idle(), Function.identity());
  }

  public String get(
      Consumer<String> importConsumer, Function<String, String> canonicalToSimpleNameMapper
  ) {
    var sb = new StringBuilder();
    if ((includeMethodTypeParams && !prototype.typeParameters().isEmpty())
        || (includeOwnerTypeParams && !prototype.owner().typeParameters().isEmpty())
    ) {
      sb.append("<");
      appendTypeParams(sb);
      sb.append("> ");
    }
    appendReturnType(sb, importConsumer, canonicalToSimpleNameMapper);
    sb.append(" ");
    sb.append(methodName);
    sb.append("(");
    appendMethodParams(sb, importConsumer, canonicalToSimpleNameMapper);
    sb.append(")");
    appendExceptions(sb, importConsumer, canonicalToSimpleNameMapper);
    return sb.toString();
  }

  private void appendTypeParams(StringBuilder sb) {
    Executor commaAppender = StringActions.commaAppender(sb);
    commaAppender.execute();
    for (NamedType typeParam : prototype.typeParameters()) {
      sb.append(typeParam.formalFullDeclaration());
    }
    if (includeOwnerTypeParams) {
      for (NamedType typeParam : prototype.owner().typeParameters()) {
        sb.append(typeParam.formalFullDeclaration());
      }
    }
  }

  private void appendReturnType(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    Type returnType = prototype.returnType().orElseThrow();
    returnType.dependencyTypenames().forEach(importConsumer);
    sb.append(returnType.actualDeclaration(canonicalToSimpleNameMapper));
  }

  private void appendMethodParams(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    Executor commaAppender = StringActions.commaAppender(sb);
    if (additionalParams != null) {
      for (String additionalParam : additionalParams) {
        commaAppender.execute();
        sb.append(additionalParam);
      }
    }
    for (MethodParam param : prototype.params()) {
      commaAppender.execute();
      param.type().dependencyTypenames().forEach(importConsumer);
      sb.append(param.type().actualDeclaration(canonicalToSimpleNameMapper));
      sb.append(" ");
      sb.append(param.name());
    }
  }

  private void appendExceptions(
      StringBuilder sb,
      Consumer<String> importConsumer,
      Function<String, String> canonicalToSimpleNameMapper
  ) {
    String exceptions = prototype.exceptions().stream()
        .map(e -> e.asCustom().orElseThrow().statement())
        .peek(e -> importConsumer.accept(e.canonicalName()))
        .map(e -> canonicalToSimpleNameMapper.apply(e.canonicalName()))
        .collect(Collectors.joining(", "));
    if (!exceptions.isEmpty()) {
      sb.append(" throws ").append(exceptions);
    }
  }
}
