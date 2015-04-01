package io.katharsis.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.katharsis.context.SampleJsonApplicationContext;
import io.katharsis.resource.ResourceInformationBuilder;
import io.katharsis.resource.mock.models.Project;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ResourceRegistryBuilder;
import io.katharsis.resource.registry.ResourceRegistryBuilderTest;
import io.katharsis.resource.registry.ResourceRegistryTest;
import io.katharsis.response.Container;
import org.junit.Before;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class SerializerTest {

    private ObjectMapper sut;

    @Before
    public void setUp() throws Exception {
        ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(new SampleJsonApplicationContext(), new ResourceInformationBuilder());
        ResourceRegistry resourceRegistry = registryBuilder.build(ResourceRegistryBuilderTest.TEST_MODELS_PACKAGE, ResourceRegistryTest.TEST_MODELS_URL);

        sut = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule",
                new Version(1, 0, 0, null, null, null));
        simpleModule.addSerializer(new ContainerSerializer(resourceRegistry));
        simpleModule.addSerializer(new LinksContainerSerializer(resourceRegistry));
        sut.registerModule(simpleModule);
    }

    @Test
    public void onSimpleObjectShouldIncludeType() throws Exception {
        // GIVEN
        Project project = new Project();

        // WHEN
        String result = sut.writeValueAsString(new Container<>(project));

        // THEN
        assertThatJson(result).node("type").isEqualTo("projects");
    }

    @Test
    public void onSimpleObjectShouldIncludeId() throws Exception {
        // GIVEN
        Project project = new Project();
        project.setId(1L);

        // WHEN
        String result = sut.writeValueAsString(new Container<>(project));

        // THEN
        assertThatJson(result).node("id").isEqualTo(1);
    }

    @Test
    public void onSimpleObjectShouldIncludeBasicFields() throws Exception {
        // GIVEN
        Project project = new Project();
        project.setName("name");

        // WHEN
        String result = sut.writeValueAsString(new Container<>(project));

        // THEN
        assertThatJson(result).node("name").isEqualTo("name");
    }

    @Test
    public void onSimpleObjectShouldIncludeLinksField() throws Exception {
        // GIVEN
        Project project = new Project();

        // WHEN
        String result = sut.writeValueAsString(new Container<>(project));

        // THEN
        assertThatJson(result).node("links").isPresent();
    }
}
