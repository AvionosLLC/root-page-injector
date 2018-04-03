package com.avionos.aem.rootpageinjector.core.models.rootpage;

import com.day.cq.wcm.api.Page;

import java.util.List;
import java.util.Optional;

public interface RootedPage {

    Optional<SectionRootPage> getSectionRoot();

    Optional<SiteRootPage> getSiteRoot();

    List<Page> getPathToSectionRoot();

    List<Page> getPathToSiteRoot();

}
