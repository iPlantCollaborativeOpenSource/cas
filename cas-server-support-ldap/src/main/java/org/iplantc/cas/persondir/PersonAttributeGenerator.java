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

import org.jasig.services.persondir.IPersonAttributes;

import java.util.List;
import java.util.Map;

/**
 * An interface that allows person attributes to be created from a username and an LDAP entry.
 */
public interface PersonAttributeGenerator {

    /**
     * Generates an {@code IPersonAttributes} implementation for an {@code LdapEntry}.
     *
     * @param userName          the name of the authenticated user.
     * @param userNameAttribute the name of the LDAP attribute containing the user name.
     * @param attributes        the LDAP entry.
     * @return an {@code IPersonAttributes} implementation for the LDAP entry.
     */
    IPersonAttributes generate(final String userName, final String userNameAttribute, final Map<String, List<Object>> attributes);
}
