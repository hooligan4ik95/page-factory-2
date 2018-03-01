package ru.sbtqa.tag.pagefactory.support.properties;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:config/application.properties")
public interface Configuration extends Config {
    
    @Key("driver.environment")
    String getEnvironment();

    
    @Key("page.load.timeout")
    @DefaultValue("20000")
    int getTimeout();
    
    @Key("page.package")
    String getPagesPackage();


    @Key("webdriver.create.attempts")
    @DefaultValue("3")
    int getWebDriverCreateAttempts();

    @Key("webdriver.starting.url")
    String getStartingUrl();

    @Key("webdriver.browser.name")
    String getBrowserName();

    @Key("webdriver.drivers.path")
    @DefaultValue("")
    String getDriversPath();

    @Key("webdriver.version")
    @DefaultValue("")
    String getWebDriverVersion();

    @Key("webdriver.browser.version")
    @DefaultValue("")
    String getBrowserVersion();

    @Key("webdriver.nexus.url")
    @DefaultValue("")
    String getNexusUrl();

    @Key("webdriver.os.arch")
    @DefaultValue("")
    String getOsArchitecture();

    @Key("webdriver.browser.path")
    @DefaultValue("")
    String getBrowserPath();

    @Key("webdriver.url")
    @DefaultValue("")
    String getWebDriverUrl();

    @Key("webdriver.proxy")
    @DefaultValue("")
    String getProxy();

    @Key("webdriver.browser.ie.killOnDispose")
    @DefaultValue("false")
    boolean isIEKillOnDispose();

    
    @Key("video.highlight.enabled")
    @DefaultValue("false")
    boolean isVideoHighlightEnabled();
    
    @Key("tasks.to.kill")
    @DefaultValue("")
    String getTasksToKill();
    
    
    @Key("selenoid.browserVersion")
    @DefaultValue("")
    String getSelenoidBrowserVersion();
    
    @Key("selenoid.enableVNC")
    @DefaultValue("")
    String getSelenoidEnableVNC();
    
    @Key("selenoid.screenResolution")
    @DefaultValue("")
    String getSelenoidScreenResolution();
    
    @Key("selenoid.enableVideo")
    @DefaultValue("false")
    boolean getSelenoidEnableVideo();
    
    @Key("selenoid.video.name")
    @DefaultValue("")
    String getSelenoidVideoName();
    
    @Key("selenoid.video.screenSize")
    @DefaultValue("")
    String getSelenoidVideoScreenSize();
    
    @Key("selenoid.video.frameRate")
    @DefaultValue("")
    String getSelenoidVideoFrameRate();
    
    @Key("selenoid.nameOfTests")
    @DefaultValue("")
    String getSelenoidNameOfTests();
    
    @Key("selenoid.timeZone")
    @DefaultValue("")
    String getSelenoidTimeZone();
    
    @Key("selenoid.hostEntries")
    @DefaultValue("")
    String getSelenoidHostEntries();
    
    @Key("selenoid.applicationContainers")
    @DefaultValue("")
    String getSelenoidApplicationContainers();
    
    @Key("selenoid.containerLables")
    @DefaultValue("")
    String getSelenoidContainerLables();
}
