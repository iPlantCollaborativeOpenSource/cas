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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractDefaultAttributePersonAttributeDao;
import org.jasig.services.persondir.support.NamedPersonImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of {@link IPersonAttributeDao} that accumulates multiple results into a single instance of
 * a class that implements {@link IPersonAttributes}.  This class is designed to wrap other implementations of
 * IPersonAttributeDao and intercept calls to getPerson().  The primary purpose of this class is to obtain multi-valued
 * person attributes from directory objects that are associated with a person.
 */
public class AccumulatingPersonAttributeDao extends AbstractDefaultAttributePersonAttributeDao {

    /**
     * Used to log debugging messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumulatingPersonAttributeDao.class);

    /**
     * The DAO that actually retrieves the IPersonAttribute instances.
     */
    private IPersonAttributeDao innerDao;

    /**
     * @param innerDao the DAO that actually retrieves the IPersonAttribute instances.
     */
    public void setInnerDao(final IPersonAttributeDao innerDao) {
        this.innerDao = innerDao;
    }

    /**
     * Builds and returns a single IPersonAttribute instance containing the accumulated attributes of all matching
     * query results.
     *
     * @param uid the user ID.
     * @return the cumulative IPersonAttribute instance.
     */
    @Override
    public IPersonAttributes getPerson(final String uid) {
        LOGGER.trace("getPerson called for uid: {}", uid);
        Validate.notNull(uid, "uid may not be null.");
        final Map<String, List<Object>> seed = toSeedMap(uid);
        final Set<IPersonAttributes> people = getPeopleWithMultivaluedAttributes(seed);
        return ensureNameValued(uid, (IPersonAttributes) DataAccessUtils.singleResult(people));
    }

    /**
     * Ensures that the user's name is valued in an instance of a class that implements IPersonAttributes.  If the
     * name is not valued then the user ID is used as the name.
     *
     * @param uid the user ID.
     * @param person the IPersonAttributes instance.
     * @return an IPersonAttributes instance with the name valued.
     */
    private IPersonAttributes ensureNameValued(final String uid, final IPersonAttributes person) {
        return StringUtils.isEmpty(person.getName()) ? new NamedPersonImpl(uid, person.getAttributes()) : person;
    }

    /**
     * Builds and returns a set containing a single IPersonAttribute instance containing the accumulated attributes
     * of all matching query results.
     *
     * @param query the query.
     * @return a set containing the single cumulative IPersonAttribute instance.
     */
    @Override
    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(final Map<String, List<Object>> query) {
        LOGGER.trace("getPeopleWithMultivaluedAttributes called for query: {}", query);
        final Set<IPersonAttributes> people = innerDao.getPeopleWithMultivaluedAttributes(query);
        LOGGER.debug("innerDao.getPeopleWithMultivaluedAttributes returned {}", people);
        if (people.isEmpty()) {
            return people;
        } else {
            final Set<IPersonAttributes> result = new HashSet<IPersonAttributes>();
            result.add(accumulateAttributes(people));
            return result;
        }
    }

    /**
     * Accumulates the attributes of a set of IPersonAttribute instances into a single IPersonAttribute instance.
     *
     * @param people the set of IPersonAttribute instances.
     * @return the cumulative IPersonAttribute instance.
     */
    private IPersonAttributes accumulateAttributes(final Set<IPersonAttributes> people) {
        final String name = people.isEmpty() ? null : people.iterator().next().getName();
        final Map<String, List<Object>> cumulativeAttributes = new HashMap<String, List<Object>>();
        for (final IPersonAttributes person : people) {
            appendAttributes(cumulativeAttributes, person.getAttributes());
        }
        return new NamedPersonImpl(name, cumulativeAttributes);
    }

    /**
     * Appends attribute values from a source attribute map onto the end of equivalently named attributes in a
     * destination attribute map.
     *
     * @param dest the destination attribute map.
     * @param source the source attribute map.
     */
    private void appendAttributes(final Map<String, List<Object>> dest, final Map<String, List<Object>> source) {
        for (final Map.Entry<String, List<Object>> entry : source.entrySet()) {
            final List<Object> sourceValue = entry.getValue();
            if (sourceValue != null) {
                final String key = entry.getKey();
                List<Object> destValue = dest.get(key);
                if (destValue == null) {
                    destValue = new ArrayList<Object>(entry.getValue());
                    dest.put(key, destValue);
                    LOGGER.debug("new attribute: {} => {}", key, destValue);
                } else {
                    destValue.addAll(entry.getValue());
                    LOGGER.debug("updated attribute: {} => {}", key, destValue);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getPossibleUserAttributeNames() {
        return innerDao.getPossibleUserAttributeNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAvailableQueryAttributes() {
        return innerDao.getAvailableQueryAttributes();
    }
}
