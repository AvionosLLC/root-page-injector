# Root Page Injector

Codifies the concept of root pages within a website and exposes 
such pages via Sling Models.

## Root Pages

Most websites present as a tree of pages with rooted branches of 
content all meeting at a single root - commonly the "Home Page".  
Many mechanisms commonly afforded end users rely on knowing 
where they are in relation to their closest section root as well 
as the ultimate site root.  This project presents the concept of 
a section root and a site root as technical artifacts and allows 
their injection via Sling Models.  

### `SiteRootPage`

The `SiteRootPage` is the single root of an entire site.  This 
root is commonly the "Home Page" though the nature of the page 
is not technically important.  A site root may be identified 
in one of two ways.

1. By setting the `isSiteRoot` on the page's page properties to `true`
2. By defining a Sling Model implementation of `SiteRootPage` appropriate 
   to your page's resourceType.
   
Instances of the `SiteRootPage` should be adaptable back to `Page`.

### `SectionRootPage`

The `SectionRootPage` is the root of a section of a site.  It is 
common for a site to be broken into many coarse grain sections with 
each section having a content tree below it.  A section root may 
be identified in one of two ways.

1. By setting the `isSectionRoot` on the page's page properties to `true`
2. By defining a Sling Model implementation of `SectionRootPage` appropriate 
   to your page's resourceType.
   
Instances of the `SectionRootPage` should be adaptable back to `Page`.

## Usage

To obtain an instance of `SectionRootPage` or `SiteRootPage` 
an appropriately typed property simply needs to be annotated as 
an injected property using Sling Models.

```java
@Inject
private SiteRootPage root;
```

## Including in a Project 

The core and ui modules may be included as Maven project dependencies. 

```xml
<dependency>
    <groupId>com.avionos.aem.rootpageinjector</groupId>
    <artifactId>root-page-injector.core</artifactId>
    <version>${rootpage.injector.version}</version>
</dependency>
<dependency>
    <groupId>com.avionos.aem.rootpageinjector</groupId>
    <artifactId>root-page-injector.ui.apps</artifactId>
    <version>${rootpage.injector.version}</version>
    <type>zip</type>
</dependency>
```

If your project will also control the installation of the 
Root Page Injector then the UI module's zip package should be 
included as a sub package of your project's package. 
