/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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
package org.iplantc.cas.persondir;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jasig.services.persondir.support.BasePersonImpl;

import java.util.List;
import java.util.Map;

/**
 * An implementation of IPersonAttributes that treats multiple records for a single person separately.
 */
public abstract class BaseMultirecordPersonImpl extends BasePersonImpl {

    /**
     * @param attributes the user attributes.
     */
    public BaseMultirecordPersonImpl(final Map<String, List<Object>> attributes) {
        super(attributes);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1574945487, 827742191)
                .append(this.getName())
                .append(this.getAttributes())
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BaseMultirecordPersonImpl)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        final BaseMultirecordPersonImpl other = (BaseMultirecordPersonImpl) o;
        return new EqualsBuilder()
                .append(this.getName(), other.getName())
                .append(this.getAttributes(), other.getAttributes())
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", this.getName())
                .append("attributes", this.getAttributes())
                .toString();
    }
}
