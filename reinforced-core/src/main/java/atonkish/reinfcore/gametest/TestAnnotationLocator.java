package atonkish.reinfcore.gametest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import atonkish.reinfcore.ReinforcedCoreMod;

public final class TestAnnotationLocator {
  private static final String ENTRYPOINT_KEY =
      String.format("%s-gametest", ReinforcedCoreMod.MOD_ID);

  private final FabricLoader fabricLoader;

  private List<TestFunction> testFunctions = null;

  public TestAnnotationLocator(FabricLoader fabricLoader) {
    this.fabricLoader = fabricLoader;
  }

  public List<TestFunction> getTestFunctions() {
    if (testFunctions != null) {
      return testFunctions;
    }

    List<EntrypointContainer<Object>> entrypointContainers =
        fabricLoader.getEntrypointContainers(ENTRYPOINT_KEY, Object.class);

    return testFunctions =
        entrypointContainers.stream()
            .flatMap(entrypoint -> findMagicMethods(entrypoint).stream())
            .toList();
  }

  private List<TestFunction> findMagicMethods(EntrypointContainer<Object> entrypoint) {
    Class<?> testClass = entrypoint.getEntrypoint().getClass();
    List<TestFunction> functions = new ArrayList<>();
    for (Method method : testClass.getDeclaredMethods()) {
      CustomTestProvider customTestProvider = method.getAnnotation(CustomTestProvider.class);
      if (customTestProvider != null) {
        functions.addAll(invokeCustomTestProviderMethod(method));
      }
    }

    return functions;
  }

  @SuppressWarnings("unchecked")
  private static Collection<TestFunction> invokeCustomTestProviderMethod(Method method) {
    try {
      Object object = method.getDeclaringClass().getDeclaredConstructor().newInstance();
      return (Collection<TestFunction>) method.invoke(object);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
