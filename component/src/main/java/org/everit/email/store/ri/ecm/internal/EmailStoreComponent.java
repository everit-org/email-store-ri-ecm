/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.email.store.ri.ecm.internal;

import java.util.Hashtable;

import org.everit.blobstore.Blobstore;
import org.everit.email.store.EmailStore;
import org.everit.email.store.ri.EmailStoreImpl;
import org.everit.email.store.ri.ecm.EmailStoreConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.transaction.propagator.TransactionPropagator;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM component for {@link EmailStore} interface based on Email Store reference implementation.
 */
@Component(componentId = EmailStoreConstants.SERVICE_PID,
    configurationPolicy = ConfigurationPolicy.OPTIONAL,
    label = "Everit Email Store Component",
    description = "ECM component for Email Store")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION,
        defaultValue = EmailStoreConstants.DEFAULT_SERVICE_DESCRIPTION,
        priority = EmailStoreComponent.P_SERVICE_DESCRIPTION,
        label = "Service Description",
        description = "The description of this component configuration."
            + "It is used to easily identify the service registered by this component.") })
@ManualService(EmailStore.class)
public class EmailStoreComponent {

  public static final int P_BLOBSTORE = 3;

  public static final int P_QUERYDSL_SUPPORT = 1;

  public static final int P_SERVICE_DESCRIPTION = 0;

  public static final int P_TRANSACTION_PROPAGATOR = 2;

  private Blobstore blobstore;

  private QuerydslSupport querydslSupport;

  private ServiceRegistration<EmailStore> serviceRegistration;

  private TransactionPropagator transactionPropagator;

  /**
   * The activate method that registers a {@link EmailStore} OSGi service.
   */
  @Activate
  public void activate(final ComponentContext<EmailStoreComponent> componentContext) {
    EmailStore emailStore = new EmailStoreImpl(querydslSupport, transactionPropagator, blobstore);

    Hashtable<String, Object> properties = new Hashtable<>(componentContext.getProperties());

    serviceRegistration = componentContext.registerService(
        EmailStore.class,
        emailStore,
        properties);
  }

  /**
   * Unregisters the {@link EmailStore} OSGi service.
   */
  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  @ServiceRef(attributeId = EmailStoreConstants.ATTR_BLOBSTORE, defaultValue = "",
      attributePriority = P_BLOBSTORE, label = "Blobstore",
      description = "OSGi service filter for org.everit.blobstore.Blobstore.")
  public void setBlobstore(final Blobstore blobstore) {
    this.blobstore = blobstore;
  }

  @ServiceRef(attributeId = EmailStoreConstants.ATTR_QUERYDSL_SUPPORT, defaultValue = "",
      attributePriority = P_QUERYDSL_SUPPORT, label = "QuerydslSupport",
      description = "OSGi service filter for "
          + "org.everit.persistence.querydsl.support.QuerydslSupport.")
  public void setQuerydslSupport(final QuerydslSupport querydslSupport) {
    this.querydslSupport = querydslSupport;
  }

  @ServiceRef(attributeId = EmailStoreConstants.ATTR_TRANSACTION_PROPAGATOR, defaultValue = "",
      attributePriority = P_TRANSACTION_PROPAGATOR, label = "TransactionPropagator",
      description = "OSGi service filter for "
          + "org.everit.transaction.propagator.TransactionPropagator.")
  public void setTransactionPropagator(final TransactionPropagator transactionPropagator) {
    this.transactionPropagator = transactionPropagator;
  }
}
