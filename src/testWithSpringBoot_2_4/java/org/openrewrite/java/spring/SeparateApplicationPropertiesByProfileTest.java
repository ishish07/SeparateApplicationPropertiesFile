package org.openrewrite.java.spring;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;


public class SeparateApplicationPropertiesByProfileTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SeparateApplicationPropertiesByProfile());
    }

    @Test
    void noApplicationProperties() {
        rewriteRun(
          org.openrewrite.properties.Assertions.properties("""
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              
              """,
            sourceSpecs -> sourceSpecs.path("application-dev.properties"))
        );
    }

    @Test
    void noSeparateProfile() {
        rewriteRun(
          org.openrewrite.properties.Assertions.properties("""
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              
              """,
            sourceSpecs -> sourceSpecs.path("application.properties"))
        );
    }

    @Test
    void separateProfileWithAppend() {
        rewriteRun(
          org.openrewrite.properties.Assertions.properties(
            """
              line1=line1
              """,
            """
              line1=line1
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              """,
            sourceSpecs -> sourceSpecs.path("application-dev.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              !---
              spring.config.activate.on-profile=dev
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              #---
              spring.config.activate.on-profile=local
              app.config.currentEnvironment=LOCAL
              
              
              #---
              #### XX Configuration ####
              spring.config.activate.on-profile=prod
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              """,
            sourceSpecs -> sourceSpecs.path("application.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              app.config.currentEnvironment=LOCAL
              """,
            sourceSpecs -> sourceSpecs.path("application-local.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              #### XX Configuration ####
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            sourceSpecs -> sourceSpecs.path("application-prod.properties")
          )
        );
    }

    @Test
    void separateProfileWithoutAppend() {
        rewriteRun(
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              """,
            sourceSpecs -> sourceSpecs.path("application-dev.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              !---
              spring.config.activate.on-profile=dev
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              #---
              spring.config.activate.on-profile=local
              app.config.currentEnvironment=LOCAL
              
              
              #---
              #### XX Configuration ####
              spring.config.activate.on-profile=prod
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              """,
            sourceSpecs -> sourceSpecs.path("application.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              app.config.currentEnvironment=LOCAL
              """,
            sourceSpecs -> sourceSpecs.path("application-local.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              #### XX Configuration ####
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            sourceSpecs -> sourceSpecs.path("application-prod.properties")
          )
        );
    }

    @Test
    void pathToApplicationProperties() {
        rewriteRun(
          org.openrewrite.properties.Assertions.properties(
            """
              line1=line1
              """,
            """
              line1=line1
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              """,
            sourceSpecs -> sourceSpecs.path("folder1/folder2/application-dev.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              !---
              spring.config.activate.on-profile=dev
              oauth2.clientId=9999999999999999999999
              service.domainUrl= https://this.is.my.dev.url.com
              app.config.currentEnvironment=DEV
              #---
              spring.config.activate.on-profile=local
              app.config.currentEnvironment=LOCAL
              
              
              #---
              #### XX Configuration ####
              spring.config.activate.on-profile=prod
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            """
              spring.application.name=Openrewrite-PR-Service
              #PR-Service
              base-url.PR-services=http://my.url.com
              exchange-token=1234567890
              exchange-tokens=${base-url.PR-services}/exchange-token
              """,
            sourceSpecs -> sourceSpecs.path("folder1/folder2/application.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              app.config.currentEnvironment=LOCAL
              """,
            sourceSpecs -> sourceSpecs.path("folder1/folder2/application-local.properties")
          ),
          org.openrewrite.properties.Assertions.properties(
            null,
            """
              #### XX Configuration ####
              oauth2.clientId=77777777777777
              service.domainUrl=https://this.is.my.prod.url.com
              """,
            sourceSpecs -> sourceSpecs.path("folder1/folder2/application-prod.properties")
          )
        );
    }
}
