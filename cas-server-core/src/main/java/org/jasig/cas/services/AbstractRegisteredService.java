/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.services;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for mutable, persistable registered services.
 *
 * @author Marvin S. Addison
 * @author Scott Battaglia
 * @author Misagh Moayyed
 * @since 3.0.0
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "expression_type", length = 15, discriminatorType = DiscriminatorType.STRING,
                     columnDefinition = "VARCHAR(15) DEFAULT 'ant'")
@Table(name = "RegisteredServiceImpl")
public abstract class AbstractRegisteredService implements RegisteredService, Comparable<RegisteredService> {

    private static final long serialVersionUID = 7645279151115635245L;

    /**
     * The unique identifier for this service.
     */
    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    protected String serviceId;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private String name;

    @Column(length = 255, updatable = true, insertable = true, nullable = true)
    private String theme;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = RegisteredService.INITIAL_IDENTIFIER_VALUE;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private String description;

    /**
     * Proxy policy for the service.
     * By default, the policy is {@link RefuseRegisteredServiceProxyPolicy}.
     */
    @Lob
    @Column(name = "proxy_policy", nullable = false)
    private RegisteredServiceProxyPolicy proxyPolicy = new RefuseRegisteredServiceProxyPolicy();

    @Column(name = "evaluation_order", nullable = false)
    private int evaluationOrder;

    /**
     * Resolve the username for this service.
     * By default the resolver is {@link DefaultRegisteredServiceUsernameProvider}.
     */
    @Lob
    @Column(name = "username_attr", nullable = true)
    private RegisteredServiceUsernameAttributeProvider usernameAttributeProvider =
        new DefaultRegisteredServiceUsernameProvider();

    /**
     * The logout type of the service. 
     * The default logout type is the back channel one.
     */
    @Column(name = "logout_type", nullable = true)
    private LogoutType logoutType = LogoutType.BACK_CHANNEL;

    @Lob
    @Column(name = "required_handlers")
    private HashSet<String> requiredHandlers = new HashSet<>();

    /** The attribute filtering policy. */
    @Lob
    @Column(name = "attribute_release")
    private AttributeReleasePolicy attributeReleasePolicy = new ReturnAllowedAttributeReleasePolicy();

    @Column(name = "logo")
    private URL logo;

    @Lob
    @Column(name = "access_strategy")
    private RegisteredServiceAccessStrategy accessStrategy =
            new DefaultRegisteredServiceAccessStrategy();

    public long getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public String getName() {
        return this.name;
    }

    public String getTheme() {
        return this.theme;
    }

    public RegisteredServiceProxyPolicy getProxyPolicy() {
        return this.proxyPolicy;
    }

    @Override
    public RegisteredServiceAccessStrategy getAccessStrategy() {
        return this.accessStrategy;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractRegisteredService)) {
            return false;
        }

        final AbstractRegisteredService that = (AbstractRegisteredService) o;

        return new EqualsBuilder()
                .append(this.proxyPolicy, that.proxyPolicy)
                .append(this.evaluationOrder, that.evaluationOrder)
                .append(this.description, that.description)
                .append(this.name, that.name)
                .append(this.serviceId, that.serviceId)
                .append(this.theme, that.theme)
                .append(this.usernameAttributeProvider, that.usernameAttributeProvider)
                .append(this.logoutType, that.logoutType)
                .append(this.attributeReleasePolicy, that.attributeReleasePolicy)
                .append(this.accessStrategy, that.accessStrategy)
                .append(this.logo, that.logo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31)
                .append(this.description)
                .append(this.serviceId)
                .append(this.name)
                .append(this.theme)
                .append(this.evaluationOrder)
                .append(this.usernameAttributeProvider)
                .append(this.accessStrategy)
                .append(this.logoutType)
                .append(this.attributeReleasePolicy)
                .append(this.accessStrategy)
                .append(this.logo)
                .toHashCode();
    }

    public void setProxyPolicy(final RegisteredServiceProxyPolicy policy) {
        this.proxyPolicy = policy;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets the service identifier. Extensions are to define the format.
     *
     * @param id the new service id
     */
    public abstract void setServiceId(final String id);

    public void setId(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTheme(final String theme) {
        this.theme = theme;
    }

    public void setEvaluationOrder(final int evaluationOrder) {
        this.evaluationOrder = evaluationOrder;
    }

    public int getEvaluationOrder() {
        return this.evaluationOrder;
    }

    public RegisteredServiceUsernameAttributeProvider getUsernameAttributeProvider() {
        return this.usernameAttributeProvider;
    }

    public void setAccessStrategy(final RegisteredServiceAccessStrategy accessStrategy) {
        this.accessStrategy = accessStrategy;
    }

    /**
     * Sets the user attribute provider instance
     * when providing usernames to this registered service.
     *
     * @param usernameProvider the new username attribute
     */
    public void setUsernameAttributeProvider(final RegisteredServiceUsernameAttributeProvider usernameProvider) {
        this.usernameAttributeProvider = usernameProvider;
    }

    /**
     * Returns the logout type of the service.
     *
     * @return the logout type of the service.
     */
    public final LogoutType getLogoutType() {
        return logoutType;
    }

    /**
     * Set the logout type of the service.
     *
     * @param logoutType the logout type of the service.
     */
    public final void setLogoutType(final LogoutType logoutType) {
        this.logoutType = logoutType;
    }

    @Override
    public final RegisteredService clone() {
        final AbstractRegisteredService clone = newInstance();
        clone.copyFrom(this);
        return clone;
    }

    /**
     * Copies the properties of the source service into this instance.
     *
     * @param source Source service from which to copy properties.
     */
    public void copyFrom(final RegisteredService source) {
        this.setId(source.getId());
        this.setProxyPolicy(source.getProxyPolicy());
        this.setDescription(source.getDescription());
        this.setName(source.getName());
        this.setServiceId(source.getServiceId());
        this.setTheme(source.getTheme());
        this.setEvaluationOrder(source.getEvaluationOrder());
        this.setUsernameAttributeProvider(source.getUsernameAttributeProvider());
        this.setLogoutType(source.getLogoutType());
        this.setAttributeReleasePolicy(source.getAttributeReleasePolicy());
        this.setAccessStrategy(source.getAccessStrategy());
        this.setLogo(source.getLogo());
    }

    /**
     * {@inheritDoc}
     * Compares this instance with the <code>other</code> registered service based on
     * evaluation order, name. The name comparison is case insensitive.
     *
     * @see #getEvaluationOrder()
     */
    @Override
    public int compareTo(final RegisteredService other) {
        return new CompareToBuilder()
                  .append(this.getEvaluationOrder(), other.getEvaluationOrder())
                  .append(this.getName().toLowerCase(), other.getName().toLowerCase())
                  .append(this.getServiceId(), other.getServiceId())
                  .toComparison();
    }

    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(null, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("id", this.id);
        toStringBuilder.append("name", this.name);
        toStringBuilder.append("description", this.description);
        toStringBuilder.append("serviceId", this.serviceId);
        toStringBuilder.append("usernameAttributeProvider", this.usernameAttributeProvider);
        toStringBuilder.append("theme", this.theme);
        toStringBuilder.append("evaluationOrder", this.evaluationOrder);
        toStringBuilder.append("logoutType", this.logoutType);
        toStringBuilder.append("attributeReleasePolicy", this.attributeReleasePolicy);
        toStringBuilder.append("accessStrategy", this.accessStrategy);
        toStringBuilder.append("proxyPolicy", this.proxyPolicy);
        toStringBuilder.append("logo", this.logo);

        return toStringBuilder.toString();
    }

    /**
     * Create a new service instance.
     *
     * @return the registered service
     */
    protected abstract AbstractRegisteredService newInstance();

    @Override
    public Set<String> getRequiredHandlers() {
        if (this.requiredHandlers == null) {
            this.requiredHandlers = new HashSet<>();
        }
        return this.requiredHandlers;
    }

    /**
     * Sets the required handlers for this service.
     *
     * @param handlers the new required handlers
     */
    public void setRequiredHandlers(final Set<String> handlers) {
        getRequiredHandlers().clear();
        if (handlers == null) {
            return;
        }
        for (final String handler : handlers) {
            getRequiredHandlers().add(handler);
        }
    }
    
    /**
     * Sets the attribute filtering policy.
     *
     * @param policy the new attribute filtering policy
     */
    public final void setAttributeReleasePolicy(final AttributeReleasePolicy policy) {
        this.attributeReleasePolicy = policy;
    }

    @Override
    public final AttributeReleasePolicy getAttributeReleasePolicy() {
        return this.attributeReleasePolicy;
    }

    @Override
    public URL getLogo() {
        return this.logo;
    }

    public void setLogo(final URL logo) {
        this.logo = logo;
    }
}
