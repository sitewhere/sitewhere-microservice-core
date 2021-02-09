/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.spi.microservice.lifecycle;

import java.util.List;

/**
 * Implementation of {@link ILifecycleStep} that executes multiple steps in
 * sequence.
 */
public interface ICompositeLifecycleStep extends ILifecycleStep {

    /**
     * Add a step to the list.
     * 
     * @param step
     */
    void addStep(ILifecycleStep step);

    /**
     * Add step that initializes a component.
     * 
     * @param owner
     * @param component
     * @param require
     */
    void addInitializeStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require);

    /**
     * Add step that starts a component.
     * 
     * @param owner
     * @param component
     * @param require
     */
    void addStartStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require);

    /**
     * Add step that stops a component.
     * 
     * @param owner
     * @param component
     */
    void addStopStep(ILifecycleComponent owner, ILifecycleComponent component);

    /**
     * Add step that terminates a component.
     * 
     * @param owner
     * @param component
     */
    void addTerminateStep(ILifecycleComponent owner, ILifecycleComponent component);

    /**
     * Get an ordered list of steps to be executed.
     * 
     * @return
     */
    List<ILifecycleStep> getSteps();
}