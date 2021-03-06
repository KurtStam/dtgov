/**
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
package org.overlord.dtgov.jbpm.util;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.api.IdentityProvider;

/**
 * Dummy <code>IdentityProvider</code> implementation that allows to provide user identity
 * and his/her roles to the services to be able to capture process instance initiator for example.
 * This needs to be replaced with more proper implementation that is bound to actual security mechanism used.
 *
 */
@ApplicationScoped
public class CustomIdentityProvider implements IdentityProvider {

    public String getName() {
        return "dummy"; //$NON-NLS-1$
    }

    public List<String> getRoles() {

        return Collections.emptyList();
    }

}
