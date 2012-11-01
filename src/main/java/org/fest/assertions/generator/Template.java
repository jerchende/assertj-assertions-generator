/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.fest.assertions.generator;

import static java.lang.Thread.currentThread;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

/**
 * 
 * Holds the template content for assertion generation, can be initialized from a {@link File} or an {@link URL}.
 * <p>
 * Template content example for <code>hasXXX</code> property assertion :
 * 
 * <pre>
 * public ${class_to_assert}Assert has${Property}(${propertyType} ${property}) {
 *   // check that actual ${class_to_assert} we want to make assertions on is not null.
 *   isNotNull();
 * 
 *   // we overrides the default error message with a more explicit one
 *   String errorMessage = format("Expected ${class_to_assert}'s ${property} to be <%s> but was <%s>", ${property}, actual.get${Property}());
 *   
 *   // check
 *   if (!actual.get${Property}().equals(${property})) { throw new AssertionError(errorMessage); }
 * 
 *   // return the current assertion for method chaining
 *   return this;
 * }
 * </pre>
 * 
 * @author Miguel Bazire
 * @author Joel Costigliola
 */
public class Template {

  private final String content;

  /**
   * Creates a new </code>{@link Template}</code> from the given content.
   * 
   * @param templateContent the template content
   */
  public Template(String templateContent) {
    this.content = templateContent;
  }

  /**
   * Creates a new </code>{@link Template}</code> from the content of the given {@link URL}.
   * 
   * @param url the {@link URL} to read to set the content of the {@link Template}
   * @throws RuntimeException if we fail to read the {@link URL} content
   */
  public Template(URL url) {
    if (url.getPath().endsWith("/")) {
      throw new RuntimeException("Failed to read template from " + url);
    }

    try {
      InputStream input = url.openStream();
      content = readContentThenClose(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read template from " + url, e);
    }
  }

  /**
   * Creates a new </code>{@link Template}</code> from the content of the given {@link File} searched in the classpath.
   * 
   * @param file the {@link File} to read to set the content of the {@link Template}
   * @throws RuntimeException if we fail to read the {@link File} content
   */
  public Template(File file) {
    try {
      // load from classpath
      InputStream inputStream = currentThread().getContextClassLoader().getResourceAsStream(file.getPath());
      content = readContentThenClose(inputStream);
    } catch (Exception e) {
      throw new RuntimeException("Failed to read template from file " + file, e);
    }
  }

  String getContent() {
    return content;
  }

  private String readContentThenClose(InputStream input) throws IOException {
    StringWriter writer = null;
    try {
      writer = new StringWriter();
      copy(input, writer);
      return writer.toString();
    } finally {
      closeQuietly(input);
      closeQuietly(writer);
    }
  }

}
