package atonkish.reinfcore;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import atonkish.reinfcore.api.ReinforcedCoreModInitializer;
import atonkish.reinfcore.gametest.TestAnnotationLocator;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.item.ModItemGroups;

public class ReinforcedCoreMod implements ModInitializer {
  public static final String MOD_ID = "reinfcore";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static ReinforcedCoreConfig CONFIG;
  private static TestAnnotationLocator locator =
      new TestAnnotationLocator(FabricLoader.getInstance());

  @Override
  public void onInitialize() {
    // Auto Config
    AutoConfig.register(ReinforcedCoreConfig.class, GsonConfigSerializer::new);
    CONFIG = AutoConfig.getConfigHolder(ReinforcedCoreConfig.class).getConfig();

    // Items
    ModItemGroups.init();

    // entrypoint: "reinfcore"
    FabricLoader.getInstance()
        .getEntrypoints(MOD_ID, ReinforcedCoreModInitializer.class)
        .forEach(ReinforcedCoreModInitializer::onInitializeReinforcedCore);

    // entrypoint: "reinfcore-gametest"
    this.onInitializeReinforcedCoreGameTest();
  }

  private void onInitializeReinforcedCoreGameTest() {
    for (TestFunction testFunction : locator.getTestFunctions()) {
      LOGGER.debug("Registering test function: {}", testFunction.identifier());
      Registry.register(
          BuiltInRegistries.TEST_FUNCTION, testFunction.identifier(), testFunction.testFunction());
    }
  }

  public static void registerDynamicEntries(List<RegistryLoadTask<?>> loadTasks) {
    Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries =
        new IdentityHashMap<>(loadTasks.size());

    for (RegistryLoadTask<?> task : loadTasks) {
      registries.put(task.registry.key(), task.registry);
    }

    Registry<GameTestInstance> testInstances =
        (Registry<GameTestInstance>) registries.get(Registries.TEST_INSTANCE);
    Registry<TestEnvironmentDefinition<?>> testEnvironmentDefinitionRegistry =
        (Registry<TestEnvironmentDefinition<?>>)
            Objects.requireNonNull(registries.get(Registries.TEST_ENVIRONMENT));

    for (TestFunction testFunction : locator.getTestFunctions()) {
      GameTestInstance testInstance = testFunction.testInstance(testEnvironmentDefinitionRegistry);
      Registry.register(testInstances, testFunction.identifier(), testInstance);
    }
  }
}
