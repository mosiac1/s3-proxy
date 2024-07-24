/*
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
package io.trino.aws.proxy.server;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import io.trino.aws.proxy.spi.credentials.AssumedRoleProvider;
import io.trino.aws.proxy.spi.credentials.CredentialsProvider;
import io.trino.aws.proxy.spi.plugin.config.AssumedRoleProviderConfig;
import io.trino.aws.proxy.spi.plugin.config.CredentialsProviderConfig;

import static com.google.common.base.Preconditions.checkArgument;

public class TrinoAwsProxyPluginValidatorModule
        extends AbstractModule
{
    public static final class TrinoAwsProxyPluginValidator
    {
        @Inject
        public TrinoAwsProxyPluginValidator(
                CredentialsProviderConfig credentialsProviderConfig,
                CredentialsProvider credentialsProvider,
                AssumedRoleProviderConfig assumedRoleProviderConfig,
                AssumedRoleProvider assumedRoleProvider)
        {
            boolean credentialsProviderIsNoop = credentialsProvider.equals(CredentialsProvider.NOOP);
            boolean credentialsProviderIsConfigured = credentialsProviderConfig.getOptionalPluginIdentifier().isPresent();
            checkArgument(!(credentialsProviderIsNoop && credentialsProviderIsConfigured),
                    "%s of type \"%s\" is not registered",
                    CredentialsProvider.class.getSimpleName(),
                    credentialsProviderConfig.getOptionalPluginIdentifier().orElse("<empty>"));

            boolean assumedRoleProviderIsNoop = assumedRoleProvider.equals(AssumedRoleProvider.NOOP);
            boolean assumedRoleProviderIsConfigured = assumedRoleProviderConfig.getOptionalPluginIdentifier().isPresent();
            checkArgument(!(assumedRoleProviderIsNoop && assumedRoleProviderIsConfigured),
                    "%s of type \"%s\" is not registered",
                    AssumedRoleProvider.class.getSimpleName(),
                    assumedRoleProviderConfig.getOptionalPluginIdentifier().orElse("<empty>"));
        }
    }

    @Override
    public void configure()
    {
        binder().bind(TrinoAwsProxyPluginValidator.class).asEagerSingleton();
    }
}