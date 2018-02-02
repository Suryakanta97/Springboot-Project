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

package org.springframework.boot.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * An {@link Action} to be executed on a {@link Project} in response to a particular type
 * of {@link Plugin} being applied.
 *
 * @author Andy Wilkinson
 */
interface PluginApplicationAction extends Action<Project> {

    /**
     * The class of the {@code Plugin} that, when applied, will trigger the execution of
     * this action. May return {@code null} if the plugin class is not on the classpath.
     *
     * @return the plugin class or {@code null}
     */
    Class<? extends Plugin<? extends Project>> getPluginClass();

}
