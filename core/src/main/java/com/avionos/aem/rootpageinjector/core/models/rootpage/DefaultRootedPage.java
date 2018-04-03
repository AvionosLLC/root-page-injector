package com.avionos.aem.rootpageinjector.core.models.rootpage;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Model(adaptables = Resource.class, adapters = RootedPage.class)
public class DefaultRootedPage implements RootedPage {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRootedPage.class);

    @Inject @Self
    private Resource resource;

    @Inject @org.apache.sling.models.annotations.Optional
    private SiteRootPage siteRootPage;

    @Inject @org.apache.sling.models.annotations.Optional
    private SectionRootPage sectionRootPage;

    private List<Page> pathToSectionRoot;
    private List<Page> pathToSiteRoot;

    @Override
    public Optional<SectionRootPage> getSectionRoot() {
        return Optional.ofNullable(sectionRootPage);
    }

    @Override
    public Optional<SiteRootPage> getSiteRoot() {
        return Optional.ofNullable(siteRootPage);
    }

    @Override
    public List<Page> getPathToSectionRoot() {
        if (pathToSectionRoot != null) {
            return pathToSectionRoot;
        }

        if (!getSectionRoot().isPresent() && !getSiteRoot().isPresent()) {
            LOG.warn("Request for path to Section Root in a context where the resource is not rooted in a section " + getResource().getPath());
            return Lists.newArrayList();
        }

        PageManager pageManager = getResource().getResourceResolver().adaptTo(PageManager.class);

        if (pageManager == null) {
            LOG.warn("Request for path to Section Root in a context where a PageManager can not be obtained from Resource " + getResource().getPath());
            return Lists.newArrayList();
        }

        Page currentPage = pageManager.getContainingPage(getResource());
        Page rootPage = getSectionRoot().isPresent() ?
                getSectionRoot().get().adaptTo(Page.class) :
                getSiteRoot().map(root -> root.adaptTo(Page.class)).orElse(null);

        if (rootPage == null) {
            LOG.error("Section root of type " + getSectionRoot().get().getClass().toString() + " could not be adapted to a Page");
            return Lists.newArrayList();
        }

        pathToSectionRoot = buildPathToRoot(currentPage, rootPage);

        return pathToSectionRoot;
    }

    @Override
    public List<Page> getPathToSiteRoot() {
        if (pathToSiteRoot != null) {
            return pathToSiteRoot;
        }

        if (!getSiteRoot().isPresent()) {
            LOG.warn("Request for path to Site Root in a context where the resource is not rooted in a site " + getResource().getPath());
            return Lists.newArrayList();
        }

        PageManager pageManager = getResource().getResourceResolver().adaptTo(PageManager.class);

        if (pageManager == null) {
            LOG.warn("Request for path to Site Root in a context where a PageManager can not be obtained from Resource " + getResource().getPath());
            return Lists.newArrayList();
        }

        Page currentPage = pageManager.getContainingPage(getResource());
        Page rootPage = getSiteRoot().get().adaptTo(Page.class);

        if (rootPage == null) {
            LOG.error("Site root of type " + getSiteRoot().get().getClass().toString() + " could not be adapted to a Page");
            return Lists.newArrayList();
        }

        pathToSiteRoot = buildPathToRoot(currentPage, rootPage);

        return pathToSiteRoot;
    }

    protected static List<Page> buildPathToRoot(Page startingPage, Page rootPage) {
        List<Page> path = Lists.newArrayList();

        Page currentPage = startingPage;

        if (rootPage == null) {
            LOG.error("RootPage could not be adapted to a Page");
            return path;
        }

        path.add(currentPage);

        while(currentPage != null && !currentPage.getPath().equals(rootPage.getPath())) {
            currentPage = currentPage.getParent();

            if (currentPage == null) {
                LOG.error("Reached the top of the content tree from " + startingPage.getPath() + " without reaching a root page");
            } else {
                path.add(currentPage);
            }
        }

        return Lists.reverse(path);
    }

    protected Resource getResource() {
        return resource;
    }

}
