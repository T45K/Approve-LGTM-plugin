package io.jenkins.plugins.lgtm;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Optional;

@SuppressWarnings("unused")
public class ApproveLgtmBuilder extends Builder implements SimpleBuildStep {

    private final String inputName;
    private final String definedName;
    private final String organizationName;
    private final String repositoryName;
    private final String pullRequestId;
    private final String eventKey;

    @DataBoundConstructor
    public ApproveLgtmBuilder(@NotNull final String inputName,
                              @NotNull final String definedName,
                              @NotNull final String organizationName,
                              @NotNull final String repositoryName,
                              @NotNull final String pullRequestId,
                              @NotNull final String eventKey) {
        this.inputName = inputName;
        this.definedName = definedName;
        this.organizationName = organizationName;
        this.repositoryName = repositoryName;
        this.pullRequestId = pullRequestId;
        this.eventKey = eventKey;
    }

    public String getInputName() {
        return inputName;
    }

    public String getDefinedName() {
        return definedName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getPullRequestId() {
        return pullRequestId;
    }

    @Override
    public void perform(@NotNull final Run<?, ?> run,
                        @NotNull final FilePath workspace,
                        @NotNull final EnvVars env,
                        @NotNull final Launcher launcher,
                        @NotNull final TaskListener listener) {
        final DescriptorImpl descriptor = (DescriptorImpl) super.getDescriptor();

        final Optional<UsernamePasswordCredentialsImpl> bitbucketUsernamePassword = Optional.ofNullable(
            CredentialsProvider.findCredentialById(descriptor.usernamePasswordId, UsernamePasswordCredentialsImpl.class, run));
        new EntryPoint(
            listener.getLogger(),
            definedName, inputName,
            descriptor.getHostName(), organizationName, repositoryName,
            bitbucketUsernamePassword.map(UsernamePasswordCredentialsImpl::getUsername).orElseThrow(),
            bitbucketUsernamePassword.map(UsernamePasswordCredentialsImpl::getPassword).map(Secret::getPlainText).orElseThrow(),
            pullRequestId, eventKey
        ).start();
    }

    @Symbol("approveLgtm")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private String hostName;
        private String usernamePasswordId;

        public DescriptorImpl() {
            super.load();
        }

        public String getHostName() {
            return hostName;
        }

        public String getUsernamePasswordId() {
            return usernamePasswordId;
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) {
            final JSONObject globalSettings = json.getJSONObject("approveLgtm");
            this.hostName = globalSettings.getString("hostName");
            this.usernamePasswordId = globalSettings.getString("usernamePasswordId");
            save();
            return true;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        @NotNull
        public String getDisplayName() {
            return "Post LGTM picture by approving pull request";
        }

        @SuppressWarnings("deprecation") // FIXME: Investigate alternative ways to use system auth instead of ACL.SYSTEM
        public ListBoxModel doFillUsernamePasswordIdItems() {
            return new StandardListBoxModel()
                .includeEmptyValue()
                .includeAs(ACL.SYSTEM, Jenkins.get(), UsernamePasswordCredentialsImpl.class);
        }
    }
}
