package com.avionos.aem.rootpageinjector.core.models.rootpage;

import com.day.cq.wcm.api.Page;

public class DefaultRootPage implements RootPage {

    private final Page page;

    public DefaultRootPage(Page page) {
        this.page = page;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> aClass) {
        if (aClass.equals(Page.class)) {
            return (AdapterType) page;
        }

        return null;
    }

}
