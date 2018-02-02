/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.autoconfigure.orm.jpa;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Settings to apply when configuring Hibernate.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class HibernateSettings {

    private String ddlAuto;

    private ImplicitNamingStrategy implicitNamingStrategy;

    private PhysicalNamingStrategy physicalNamingStrategy;

    private Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    public HibernateSettings ddlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
        return this;
    }

    public String getDdlAuto() {
        return this.ddlAuto;
    }

    public HibernateSettings implicitNamingStrategy(
            ImplicitNamingStrategy implicitNamingStrategy) {
        this.implicitNamingStrategy = implicitNamingStrategy;
        return this;
    }

    public ImplicitNamingStrategy getImplicitNamingStrategy() {
        return this.implicitNamingStrategy;
    }

    public HibernateSettings physicalNamingStrategy(
            PhysicalNamingStrategy physicalNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
        return this;
    }

    public PhysicalNamingStrategy getPhysicalNamingStrategy() {
        return this.physicalNamingStrategy;
    }

    public HibernateSettings hibernatePropertiesCustomizers(
            Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        this.hibernatePropertiesCustomizers = new ArrayList<>(
                hibernatePropertiesCustomizers);
        return this;
    }

    public Collection<HibernatePropertiesCustomizer> getHibernatePropertiesCustomizers() {
        return this.hibernatePropertiesCustomizers;
    }

}
