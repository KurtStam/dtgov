/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.sramp.governance;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.ConfigurationFactory;
import org.overlord.commons.config.JBossServer;
import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.common.exception.ConfigException;
import org.overlord.sramp.governance.auth.BasicAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Governance {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static String QUERY_ERROR  = GovernanceConstants.GOVERNANCE_QUERIES + " should be of the format <query>|<processId>|<param::param>\nCheck\n"; //$NON-NLS-1$
    public static String TARGET_ERROR = GovernanceConstants.GOVERNANCE_TARGETS + " should be of the format <targetName>|<directory>\nCheck\n"; //$NON-NLS-1$
    public static String NOTIFICATION_ERROR  = GovernanceConstants.GOVERNANCE + ".<email|..> should be of the format <groupName>|<fromAddress>|<destination1>,<destination2>\nCheck\n"; //$NON-NLS-1$
    public static String DEFAULT_JNDI_EMAIL_REF = "java:jboss/mail/Default"; //$NON-NLS-1$
    public static String DEFAULT_EMAIL_DOMAIN = "example.com"; //$NON-NLS-1$
    public static String DEFAULT_EMAIL_FROM = "overlord@example.com"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_WORKFLOW_GROUP   = "org.overlord.dtgov"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_WORKFLOW_NAME    = "dtgov-workflows"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_WORKFLOW_VERSION = "1.0.0"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_WORKFLOW_PACKAGE = "SRAMPPackage"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_WORKFLOW_KSESSION = "ksessionSRAMP"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_USER = "admin"; //$NON-NLS-1$
    public static String DEFAULT_GOVERNANCE_PASSWORD = "overlord"; //$NON-NLS-1$
    public static String DEFAULT_RHQ_USER = "rhqadmin"; //$NON-NLS-1$
    public static String DEFAULT_RHQ_PASSWORD = "rhqadmin"; //$NON-NLS-1$
    public static String DEFAULT_RHQ_BASEURL = "http://localhost:7080"; //$NON-NLS-1$

    protected static Configuration configuration;
    static {
        String configFile = System.getProperty(GovernanceConstants.GOVERNANCE_FILE_NAME);
        String refreshDelayStr = System.getProperty(GovernanceConstants.GOVERNANCE_FILE_REFRESH);
        Long refreshDelay = 5000l;
        if (refreshDelayStr != null) {
            refreshDelay = new Long(refreshDelayStr);
        }

        configuration = ConfigurationFactory.createConfig(
                configFile,
                "dtgov.properties", //$NON-NLS-1$
                refreshDelay,
                "/governance.config.txt", //$NON-NLS-1$
                Governance.class);
    }

    /**
     * Constructor.
     */
    public Governance() {
        super();
    }

    /**
     * @return the current configuration
     */
    protected Configuration getConfiguration() {
        return configuration;
    }

    public String validate() throws ConfigException {
        StringBuffer configuration = new StringBuffer();
        try {
            configuration.append("Governance configuration:").append("\n");  //$NON-NLS-1$//$NON-NLS-2$
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_URL       + ": " + getBpmUrl()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_USER      + ": " + getBpmUser()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_PASSWORD  + ": " + getBpmPassword().replaceAll(".", "*")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

            configuration.append(GovernanceConstants.SRAMP_REPO_URL           + ": " + getSrampUrl()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.SRAMP_REPO_USER          + ": " + getSrampUser()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.SRAMP_REPO_PASSWORD      + ": " + getSrampPassword()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.SRAMP_REPO_VALIDATING    + ": " + getSrampValidating()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.append(GovernanceConstants.SRAMP_REPO_AUTH_PROVIDER + ": " + getSrampAuthProvider()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

            int i=1;
            for (String name : getTargets().keySet()) {
                configuration.append("Target ").append(i++).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
                configuration.append(getTargets().get(name).toString()).append("\n\n"); //$NON-NLS-1$
            }
            log.debug(configuration.toString());
            return configuration.toString();
        } catch (ConfigException e) {
            throw e;
        } catch (MalformedURLException e) {
            throw new ConfigException(e);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    public String getBpmUser() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_BPM_USER, DEFAULT_GOVERNANCE_USER);
    }

    public String getBpmPassword() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_BPM_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }

    public String getOverlordUser() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_USER, DEFAULT_GOVERNANCE_USER);
    }

    public String getOverlordPassword() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }

    public String getRhqUser() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_RHQ_USER, DEFAULT_RHQ_USER);
    }

    public String getRhqPassword() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_RHQ_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }

    public URL getBpmUrl() throws MalformedURLException {
        return new URL(getConfiguration().getString(GovernanceConstants.GOVERNANCE_BPM_URL, JBossServer.getBaseUrl() + "/gwt-console-server")); //$NON-NLS-1$
    }

    /**
     * This returns the baseURL, which by default is http://localhost:8080/s-ramp-server
     */
    public URL getSrampUrl() throws MalformedURLException {
        return new URL(getConfiguration().getString(GovernanceConstants.SRAMP_REPO_URL, JBossServer.getBaseUrl() + "/s-ramp-server")); //$NON-NLS-1$
    }

    public String getSrampUser() {
        return getConfiguration().getString(GovernanceConstants.SRAMP_REPO_USER, "admin"); //$NON-NLS-1$
    }

    public String getSrampPassword() {
        return getConfiguration().getString(GovernanceConstants.SRAMP_REPO_PASSWORD, "overlord"); //$NON-NLS-1$
    }

    public Class<?> getSrampAuthProvider() throws Exception {
        String authProviderClassName = getConfiguration().getString(
                GovernanceConstants.SRAMP_REPO_AUTH_PROVIDER, BasicAuthenticationProvider.class.getName());
        if (authProviderClassName == null)
            return null;
        return Class.forName(authProviderClassName);
    }

    public boolean getSrampValidating() throws Exception {
        return "true".equals(getConfiguration().getString(GovernanceConstants.SRAMP_REPO_VALIDATING, "false")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This returns the governance baseURL, which by default is http://localhost:8080/s-ramp-server
     */
    public String getGovernanceUrl() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_URL, JBossServer.getBaseUrl() + "/dtgov"); //$NON-NLS-1$
    }

    /**
     * This returns the DTGovUiURL, which by default is http://localhost:8080/s-ramp-server
     */
    public String getDTGovUiUrl() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_UI, JBossServer.getBaseUrl() + "/dtgov-ui"); //$NON-NLS-1$
    }

    public Map<String,Target> getTargets() throws ConfigException {
        Map<String,Target> targets = new HashMap<String,Target>();
        String[] targetStrings = getConfiguration().getStringArray(GovernanceConstants.GOVERNANCE_TARGETS);
        StringBuffer errors = new StringBuffer(TARGET_ERROR);
        boolean hasErrors = false;
        for (String targetString : targetStrings) {
            String[] info = targetString.split("\\|"); //$NON-NLS-1$
            if (info.length != 4) {
                hasErrors = true;
                errors.append(targetString).append("\n"); //$NON-NLS-1$
            }
            if (!hasErrors) {
            	String name = info[0];
                String classifier = info[1];
                String type = info[2];
                if (Target.TYPE.COPY.toString().equalsIgnoreCase(type)) {
            		Target target = new Target(name, classifier, info[3]);
            		targets.put(target.getName(), target);
            	} else if (Target.TYPE.RHQ.toString().equalsIgnoreCase(type)) {
            		String rhqConfigStr = info[3].replaceAll("\\{rhq.user\\}",    DEFAULT_RHQ_USER) //$NON-NLS-1$
            									 .replaceAll("\\{rhq.password\\}",DEFAULT_RHQ_PASSWORD) //$NON-NLS-1$
            									 .replaceAll("\\{rhq.baseUrl\\}", DEFAULT_RHQ_BASEURL); //$NON-NLS-1$

            		String[] rhqConfig = rhqConfigStr.split("\\:\\:"); //$NON-NLS-1$

            		Target target = null;
            		if (rhqConfig.length==3) {
            			target = new Target(name, classifier, rhqConfig[0], rhqConfig[1], rhqConfig[2],"JBossAS7"); //$NON-NLS-1$
            		} else if (rhqConfig.length==4) {
            			target = new Target(name, classifier, rhqConfig[0], rhqConfig[1], rhqConfig[2], rhqConfig[3]);
            		} else {
            			hasErrors = true;
            			errors.append(rhqConfigStr).append("\n"); //$NON-NLS-1$
            		}
            		if (!hasErrors) {
            			targets.put(target.getName(), target);
            		}
            	} else if (Target.TYPE.AS_CLI.toString().equalsIgnoreCase(type)) {
            		String[] cliConfig = info[3].split("\\:\\:"); //$NON-NLS-1$
            		Target target = new Target(name, classifier, cliConfig[0], cliConfig[1], cliConfig[2], Integer.valueOf(cliConfig[3]));
            		targets.put(target.getName(), target);
            	} else if (Target.TYPE.MAVEN.toString().equalsIgnoreCase(type)) {
            		String[] mvnConfig = info[3].split("\\:\\:"); //$NON-NLS-1$
            		Target target = new Target(name, classifier, mvnConfig[0], Boolean.parseBoolean(mvnConfig[1]), Boolean.parseBoolean(mvnConfig[2]));
            		targets.put(target.getName(), target);
            	}
            }
        }
        if (hasErrors) {
            throw new ConfigException(errors.toString());
        }
        return targets;
    }


    public Map<String,NotificationDestinations> getNotificationDestinations(String channel) throws ConfigException {
        Map<String,NotificationDestinations> destinationMap = new HashMap<String,NotificationDestinations>();
        String[] destinationStrings = getConfiguration().getStringArray(GovernanceConstants.GOVERNANCE + channel);
        StringBuffer errors = new StringBuffer(NOTIFICATION_ERROR);
        boolean hasErrors = false;
        for (String destinationString : destinationStrings) {
            String[] info = destinationString.split("\\|"); //$NON-NLS-1$
            if (info.length != 3) {
                hasErrors = true;
                errors.append(destinationString).append("\n"); //$NON-NLS-1$
            }
            if (!hasErrors) {
                NotificationDestinations destination = new NotificationDestinations(info[0],info[1], info[2]);
                destinationMap.put(destination.getName(), destination);
            }
        }
        if (hasErrors) {
            throw new ConfigException(errors.toString());
        }
        return destinationMap;
    }

    public long getQueryInterval() {
        return getConfiguration().getLong(GovernanceConstants.GOVERNANCE_QUERY_INTERVAL, 300000l); //5 min default
    }

    public long getAcceptableLagtime() {
        return configuration.getLong(GovernanceConstants.GOVERNANCE_ACCEPTABLE_LAG, 1000l); //1 s
    }

    public String getJNDIEmailName() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_JNDI_EMAIL_REF, DEFAULT_JNDI_EMAIL_REF);
    }

    public String getDefaultEmailDomain() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_EMAIL_DOMAIN, DEFAULT_EMAIL_DOMAIN);
    }

    public String getDefaultEmailFromAddress() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_EMAIL_FROM, DEFAULT_EMAIL_FROM);
    }

    public String getSrampWagonVersion() {
        return Release.getVersionFromManifest(GovernanceConstants.SRAMP_WAGON_JAR);
    }

    public Boolean getSrampWagonSnapshots() {
        return getConfiguration().getBoolean(GovernanceConstants.SRAMP_WAGON_SNAPSHOTS, true);
    }

    public Boolean getSrampWagonReleases() {
        return getConfiguration().getBoolean(GovernanceConstants.SRAMP_WAGON_RELEASES, true);
    }

    public String getGovernanceWorkflowGroup() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_WORKFLOW_GROUP, DEFAULT_GOVERNANCE_WORKFLOW_GROUP);
    }

    public String getGovernanceWorkflowName() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_WORKFLOW_NAME, DEFAULT_GOVERNANCE_WORKFLOW_NAME);
    }

    public String getGovernanceWorkflowVersion() {
    	String defaultDtGovVersion = Release.getGovernanceVersion();
    	if (defaultDtGovVersion==null || defaultDtGovVersion.equals("unknown")) defaultDtGovVersion = DEFAULT_GOVERNANCE_WORKFLOW_VERSION; //$NON-NLS-1$
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_WORKFLOW_VERSION, defaultDtGovVersion);
    }

    public String getGovernanceWorkflowPackage() {
        return getConfiguration().getString(GovernanceConstants.GOVERNANCE_WORKFLOW_PACKAGE, DEFAULT_GOVERNANCE_WORKFLOW_PACKAGE);
    }


}
