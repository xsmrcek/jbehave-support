package org.jbehavesupport.core.file;

import static org.jbehavesupport.core.internal.MetadataUtil.userDefined;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Authenticator;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.annotations.Then;
import org.jbehavesupport.core.TestContext;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class FileSteps {

    private static final String FILE_NAME_PATTERN = "%s_%s.";
    private static final String FILE_DIRECTORY = "./target/reports";

    private final TestContext testContext;
    private final FileNameResolver fileNameResolver;

    @When("a file with the [$extension] extension is created and the file path is stored as [$alias]: $content")
    @Given("a file with the [$extension] extension is created and the file path is stored as [$alias]: $content")
    public void createFileToContext(String extension, String alias, String content) {
        try {
            File tempFile = File.createTempFile("test", extension);
            tempFile.deleteOnExit();
            FileUtils.writeStringToFile(tempFile, content, Charset.defaultCharset());
            testContext.put(alias, tempFile.getCanonicalPath(), userDefined());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Given("authentication is prepared for user [$user] with a password [$password]")
    public void authenticate(ExpressionEvaluatingParameter<String> user, ExpressionEvaluatingParameter<String> password) {
        JbusAuthenticator.setPasswordAuthentication("homerselect", "homerselect");
    }

    @Then("file with the [$extension] from url [$url] is downloaded, and its path is saved to context as [$contextAlias]")
    public void downloadFile(ExpressionEvaluatingParameter<String> extension, ExpressionEvaluatingParameter<String> url, ExpressionEvaluatingParameter<String> contextAlias) {
        Authenticator.setDefault(new JbusAuthenticator());
        try (InputStream in = new URL(url.getValue()).openStream()) {
            Path filePath = Paths.get(fileNameResolver.resolveFilePath(FILE_NAME_PATTERN.concat(extension.getValue()), FILE_DIRECTORY, contextAlias.getValue()).toString());
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            testContext.put(contextAlias.getValue(), filePath, userDefined());
        } catch (IOException e) {
            throw new UncheckedIOException("downloading of file from url [" + url.getValue() + "] failed", e);
        }
    }
}
