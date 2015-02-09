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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jasig.cas.authentication.principal.DefaultPrincipalAttributesRepository;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.PrincipalAttributesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Abstract release policy for attributes, provides common shared settings such as loggers and attribute filter config.
 * Subclasses are to provide the behavior for attribute retrieval.
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public abstract class AbstractAttributeReleasePolicy implements AttributeReleasePolicy {
    
    private static final long serialVersionUID = 5325460875620586503L;

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The attribute filter. */
    private AttributeFilter attributeFilter;

    /** Attribute repository that refreshes attributes for a principal. **/
    private PrincipalAttributesRepository principalAttributesRepository = new DefaultPrincipalAttributesRepository();

    @Override
    public final void setAttributeFilter(final AttributeFilter filter) {
        this.attributeFilter = filter;
    }

    public final void setPrincipalAttributesRepository(final PrincipalAttributesRepository repository) {
        this.principalAttributesRepository = repository;
    }

    protected PrincipalAttributesRepository getPrincipalAttributesRepository() {
        return principalAttributesRepository;
    }

    /**
     * Gets the attribute filter.
     *
     * @return the attribute filter
     */
    protected final AttributeFilter getAttributeFilter() {
        return this.attributeFilter;
    }
    
    @Override
    public final Map<String, Object> getAttributes(final Principal p) {
        final Map<String, Object> principalAttributes = this.principalAttributesRepository.getAttributes(p);
        final Map<String, Object> attributesToRelease = getAttributesInternal(principalAttributes);
        
        if (this.attributeFilter != null) {
            return this.attributeFilter.filter(attributesToRelease);
        }
        return attributesToRelease;
    }
    
    /**
     * Gets the attributes internally from the implementation.
     *
     * @param attributes the principal attributes
     * @return the attributes allowed for release
     */
    protected abstract Map<String, Object> getAttributesInternal(final Map<String, Object> attributes);

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 133).append(this.attributeFilter).toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractAttributeReleasePolicy)) {
            return false;
        }

        final AbstractAttributeReleasePolicy that = (AbstractAttributeReleasePolicy) o;
        return new EqualsBuilder().append(this.attributeFilter, that.attributeFilter).isEquals();
    }
}
