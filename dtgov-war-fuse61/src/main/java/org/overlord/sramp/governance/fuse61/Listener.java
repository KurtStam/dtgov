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

package org.overlord.sramp.governance.fuse61;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;

import org.jboss.weld.servlet.api.ServletListener;
import org.overlord.commons.osgi.weld.BundleListener;

/**
 * A servlet listener that wraps the Weld servlet listener so that we can
 * fix up the context class loader during creation.
 * 
 * Workarond for  https://ops4j1.jira.com/browse/PAXWEB-666 
 *
 * @author eric.wittmann@redhat.com
 */
public class Listener implements ServletListener {

    private ContextClassLoaderSwapper swapper;
    private org.jboss.weld.environment.servlet.Listener delegate;

    /**
     * Constructor.
     */
    public Listener() {
        swapper = new ContextClassLoaderSwapper();
        delegate = new BundleListener();
        swapper.restore();
    }

    private static final class ContextClassLoaderSwapper {
        private ClassLoader loader = null;

        /**
         * Constructor.
         */
        public ContextClassLoaderSwapper() {
            loader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        }

        /**
         * Restores the classloader to its previous version.
         */
        public void restore() {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        delegate.contextDestroyed(arg0);
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        delegate.contextInitialized(arg0);
    }

    /**
     * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
     */
    @Override
    public void requestDestroyed(ServletRequestEvent arg0) {
        delegate.requestDestroyed(arg0);
    }

    /**
     * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
     */
    @Override
    public void requestInitialized(ServletRequestEvent arg0) {
        delegate.requestInitialized(arg0);
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        delegate.sessionCreated(arg0);
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        delegate.sessionDestroyed(arg0);
    }

}
