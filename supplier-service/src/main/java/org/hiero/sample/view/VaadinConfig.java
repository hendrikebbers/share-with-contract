package org.hiero.sample.view;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan
@Push
@PWA(name = "Project Base for Vaadin with Spring", shortName = "Project Base")
public class VaadinConfig implements AppShellConfigurator {
}
