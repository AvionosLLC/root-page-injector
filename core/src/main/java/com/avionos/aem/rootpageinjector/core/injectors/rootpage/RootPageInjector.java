package com.avionos.aem.rootpageinjector.core.injectors.rootpage;

import com.avionos.aem.rootpageinjector.core.models.rootpage.*;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {Injector.class}, property = { "service.ranking:Integer=" + Integer.MAX_VALUE })
public class RootPageInjector implements Injector {

    public static final String NAME = "rootpage";

    @Reference
    private ModelFactory modelFactory;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(Object adaptable, String name, Type type, AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;

            if (RootPage.class.isAssignableFrom(clazz)) {
                Resource resource = getResource(adaptable);

                if (resource != null) {

                    PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);

                    if (pageManager != null) {
                        Page currentPage = pageManager.getContainingPage(resource);

                        while (currentPage != null) {
                            /*
                             * The following mess is due to the fact that the Sling Models implementation will
                             * still adapt if there is ANY model which it can instantiate from a Resource, even
                             * if the model is registered to particular resource types.  So, to check for resource
                             * type viability we actually need to inspect the Model annotation on the class of
                             * the object returned from the adaptation.
                             *
                             * I'm not any more happy about it than you are.
                             */
                            Object adapted = currentPage.getContentResource().adaptTo(clazz);

                            if (adapted != null) {
                                List<String> resourceTypes = getAnnotatedResourceTypes(adapted.getClass());

                                for(String currentType : resourceTypes) {
                                    if (currentPage.getContentResource().isResourceType(currentType)) {
                                        return adapted;
                                    }
                                }
                            }
                            else if (currentPage.getContentResource().getValueMap().get(SectionRootPage.IS_SECTION_ROOT, false) &&
                                    !SiteRootPage.class.isAssignableFrom(clazz)) {
                                return new DefaultSectionRootPage(currentPage);
                            }
                            else if (currentPage.getContentResource().getValueMap().get(SiteRootPage.IS_SITE_ROOT, false) &&
                                    !SectionRootPage.class.isAssignableFrom(clazz)) {
                                return new DefaultSiteRootPage(currentPage);
                            }

                            currentPage = currentPage.getParent();
                        }
                    }
                }
            }
        }

        return null;
    }

    private Resource getResource(Object adaptable) {
        if (adaptable instanceof Resource) {
            return (Resource) adaptable;
        }

        if (adaptable instanceof SlingHttpServletRequest) {
            return ((SlingHttpServletRequest) adaptable).getResource();
        }

        return null;
    }

    private static List<String> getAnnotatedResourceTypes(Class<?> clazz) {
        Model modelAnnotation = clazz.getAnnotation(Model.class);

        if (modelAnnotation != null) {
            return Arrays.asList(modelAnnotation.resourceType());
        }

        return new ArrayList<>();
    }

}
