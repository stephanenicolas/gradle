/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.internal.nativeintegration.jna;

import org.gradle.internal.nativeintegration.NativeIntegrationException;
import org.gradle.internal.nativeintegration.NativeIntegrationUnavailableException;
import org.gradle.internal.nativeintegration.ProcessEnvironment;
import org.gradle.internal.os.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Map;

public class UnsupportedEnvironment implements ProcessEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnsupportedEnvironment.class);

    @Override
    public boolean maybeSetEnvironment(Map<String, String> source) {
        return false;
    }

    @Override
    public void removeEnvironmentVariable(String name) throws NativeIntegrationException {
        throw notSupported();
    }

    @Override
    public boolean maybeRemoveEnvironmentVariable(String name) {
        return false;
    }

    @Override
    public void setEnvironmentVariable(String name, String value) throws NativeIntegrationException {
        throw notSupported();
    }

    @Override
    public boolean maybeSetEnvironmentVariable(String name, String value) {
        return false;
    }

    @Override
    public File getProcessDir() throws NativeIntegrationException {
        throw notSupported();
    }

    @Override
    public void setProcessDir(File processDir) throws NativeIntegrationException {
        throw notSupported();
    }

    @Override
    public boolean maybeSetProcessDir(File processDir) {
        return false;
    }

    @Override
    public Long getPid() throws NativeIntegrationException {
        Long pid = maybeGetPid();
        if (pid != null) {
            return pid;
        }
        throw notSupported();
    }

    @Override
    public Long maybeGetPid() {
        Long pid = null;
        try {
            //Try to obtain the PID: This works on Solaris and should work with any Java VM
            String vmName = ManagementFactory.getRuntimeMXBean().getName();
            pid = Long.parseLong(vmName.substring(0, vmName.indexOf("@")));
        } catch (RuntimeException e) {
            LOGGER.debug("Native-platform process: failed to parse PID from Java VM name - " + e.getMessage());
        }
        return pid;
    }

    private NativeIntegrationException notSupported() {
        return new NativeIntegrationUnavailableException("We don't support this operating system: " + OperatingSystem.current());
    }
}
