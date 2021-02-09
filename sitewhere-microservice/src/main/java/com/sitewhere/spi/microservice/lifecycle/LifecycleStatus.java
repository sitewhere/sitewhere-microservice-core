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

/**
 * Lifecycle status for component.
 */
public enum LifecycleStatus {

    /** Component is initializing */
    Initializing,

    /** Component unable to initialize */
    InitializationError,

    /** Component is stopped */
    Stopped,

    /** Component stopped, but with nested errors */
    StoppedWithErrors,

    /** Component is starting */
    Starting,

    /** Component is starting asynchronously */
    StartingAsynchronously,

    /** Component is started */
    Started,

    /** Component started, but with nested errors */
    StartedWithErrors,

    /** Component is pausing */
    Pausing,

    /** Component is paused */
    Paused,

    /** Component is stopping */
    Stopping,

    /** Component is terminating */
    Terminating,

    /** Component is terminated */
    Terminated,

    /** Component errored in lifecycle transition */
    LifecycleError;
}