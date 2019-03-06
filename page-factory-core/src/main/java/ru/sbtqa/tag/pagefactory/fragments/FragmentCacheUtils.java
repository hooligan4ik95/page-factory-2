package ru.sbtqa.tag.pagefactory.fragments;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import gherkin.ast.Background;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;
import gherkin.ast.Scenario;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.Step;
import gherkin.ast.Tag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.pagefactory.exceptions.FragmentException;
import ru.sbtqa.tag.pagefactory.exceptions.ReadFieldError;
import ru.sbtqa.tag.pagefactory.properties.Configuration;
import ru.sbtqa.tag.pagefactory.reflection.DefaultReflection;

class FragmentCacheUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentCacheUtils.class);
    private static final Configuration PROPERTIES = Configuration.create();
    private static final String FRAGMENT_TAG = "@fragment";

    private FragmentCacheUtils() {
    }

    static List<CucumberFeature> cacheFragmentsToFeatures(Class clazz, List<CucumberFeature> features) {
        if (PROPERTIES.getFragmentsPath().isEmpty()) {
            return features;
        } else {
            ClassLoader classLoader = clazz.getClassLoader();
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            List<CucumberFeature> fragmentsRaw = CucumberFeature.load(resourceLoader, Collections.singletonList(PROPERTIES.getFragmentsPath()));
            return Stream.concat(features.stream(), fragmentsRaw.stream()).collect(Collectors.toList());
        }
    }

    static Map<String, ScenarioDefinition> cacheFragmentsAsMap(List<CucumberFeature> features) {
        Map<String, ScenarioDefinition> fragments = new HashMap<>();

        for (CucumberFeature cucumberFeature : features) {
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarioDefinitions = feature.getChildren();
            for (ScenarioDefinition scenario : scenarioDefinitions) {
                List<Tag> tags = new DefaultReflection().getScenarioTags(scenario);
                if (isFragmentTagContains(tags)) {
                    fragments.put(scenario.getName(), scenario);
                }
            }
        }

        return fragments;
    }

    private static boolean isFragmentTagContains(List<Tag> tags) {
        return tags.stream().anyMatch(tag -> tag.getName().equals(FRAGMENT_TAG));
    }

    static MutableValueGraph<Object, String> cacheFragmentsAsGraph(List<CucumberFeature> features,
                                                                   Map<String, ScenarioDefinition> fragmentsMap,
                                                                   Map<ScenarioDefinition, String> scenarioLanguageMap) throws FragmentException {
        MutableValueGraph<Object, String> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();

        for (CucumberFeature cucumberFeature : features) {
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarioDefinitions = feature.getChildren().stream()
                    .filter(FragmentCacheUtils::isScenario)
                    .collect(Collectors.toList());

            for (ScenarioDefinition scenario : scenarioDefinitions) {
                addGraphNode(graph, scenario, "", fragmentsMap, scenarioLanguageMap);
            }
        }

        if (Graphs.hasCycle(graph.asGraph())) {
            LOG.error("Fragments graph contains cycles");
        }

        return graph;
    }

    private static void addGraphNode(MutableValueGraph graph, ScenarioDefinition scenario, String data,
                              Map<String, ScenarioDefinition> fragmentsMap,
                              Map<ScenarioDefinition, String> scenarioLanguageMap) throws FragmentException {
        graph.addNode(scenario);
        String language = scenarioLanguageMap.get(scenario);
        List<Step> steps = scenario.getSteps();

        for (Step step : steps) {
            if (FragmentUtils.isStepFragmentRequire(step, language)) {
                String scenarioName = FragmentUtils.getFragmentName(step, language);
                ScenarioDefinition scenarioAsFragment = fragmentsMap.get(scenarioName);

                addGraphNode(graph, scenarioAsFragment, data, fragmentsMap, scenarioLanguageMap);

                if (scenarioAsFragment == null) {
                    throw new FragmentException(String.format("There is no scenario (fragment) with name \"%s\"", scenarioName));
                }

                graph.putEdgeValue(scenario, scenarioAsFragment, data);
            }
        }
    }


    private static boolean isScenario(ScenarioDefinition scenarioDefinition) {
            List<Tag> tags;
            if (scenarioDefinition instanceof Scenario) {
                tags = ((Scenario) scenarioDefinition).getTags();
            } else if (scenarioDefinition instanceof ScenarioOutline) {
                tags = ((ScenarioOutline) scenarioDefinition).getTags();
            } else {
                return true;
            }
            return tags.stream().noneMatch(tag -> tag.getName().equals("@fragment"));
    }


    static Map<ScenarioDefinition, String> cacheScenarioLanguage(List<CucumberFeature> features) {
        Map<ScenarioDefinition, String> scenarioLanguageMap = new HashMap<>();

        for (CucumberFeature cucumberFeature : features) {
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            Feature feature = gherkinDocument.getFeature();
            List<ScenarioDefinition> scenarios = feature.getChildren();
            for (ScenarioDefinition scenario : scenarios) {
                scenarioLanguageMap.put(scenario, feature.getLanguage());
            }
        }

        return scenarioLanguageMap;
    }
}
