/*
 * Copyright 2012-2014 the original author or authors.
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

package org.springframework.boot.cli.command.init;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.mockito.ArgumentMatcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

/**
 * Abstract base class for tests that use a mock {@link CloseableHttpClient}.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractHttpClientMockTests {

	protected final CloseableHttpClient http = mock(CloseableHttpClient.class);

	protected void mockSuccessfulMetadataGet() throws IOException {
		mockSuccessfulMetadataGet("2.0.0");
	}

	protected void mockSuccessfulMetadataGet(String version) throws IOException {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		Resource resource = new ClassPathResource("metadata/service-metadata-" + version
				+ ".json");
		byte[] content = StreamUtils.copyToByteArray(resource.getInputStream());
		mockHttpEntity(response, content, "application/vnd.initializr.v2+json");
		mockStatus(response, 200);
		given(this.http.execute(argThat(getForJsonMetadata()))).willReturn(response);
	}

	protected void mockSuccessfulProjectGeneration(
			MockHttpProjectGenerationRequest request) throws IOException {
		// Required for project generation as the metadata is read first
		mockSuccessfulMetadataGet();
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		mockHttpEntity(response, request.content, request.contentType);
		mockStatus(response, 200);
		String header = (request.fileName != null ? contentDispositionValue(request.fileName)
				: null);
		mockHttpHeader(response, "Content-Disposition", header);
		given(this.http.execute(argThat(getForNonJsonMetadata()))).willReturn(response);
	}

	protected void mockProjectGenerationError(int status, String message)
			throws IOException {
		// Required for project generation as the metadata is read first
		mockSuccessfulMetadataGet();
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		mockHttpEntity(response, createJsonError(status, message).getBytes(),
				"application/json");
		mockStatus(response, status);
		given(this.http.execute(isA(HttpGet.class))).willReturn(response);
	}

	protected void mockMetadataGetError(int status, String message) throws IOException {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		mockHttpEntity(response, createJsonError(status, message).getBytes(),
				"application/json");
		mockStatus(response, status);
		given(this.http.execute(isA(HttpGet.class))).willReturn(response);
	}

	protected HttpEntity mockHttpEntity(CloseableHttpResponse response, byte[] content,
			String contentType) {
		try {
			HttpEntity entity = mock(HttpEntity.class);
			given(entity.getContent()).willReturn(new ByteArrayInputStream(content));
			Header contentTypeHeader = contentType != null ? new BasicHeader(
					"Content-Type", contentType) : null;
			given(entity.getContentType()).willReturn(contentTypeHeader);
			given(response.getEntity()).willReturn(entity);
			return entity;
		}
		catch (IOException ex) {
			throw new IllegalStateException("Should not happen", ex);
		}
	}

	protected void mockStatus(CloseableHttpResponse response, int status) {
		StatusLine statusLine = mock(StatusLine.class);
		given(statusLine.getStatusCode()).willReturn(status);
		given(response.getStatusLine()).willReturn(statusLine);
	}

	protected void mockHttpHeader(CloseableHttpResponse response, String headerName,
			String value) {
		Header header = value != null ? new BasicHeader(headerName, value) : null;
		given(response.getFirstHeader(headerName)).willReturn(header);
	}

	protected Matcher<HttpGet> getForJsonMetadata() {
		return new HasAcceptHeader("application/vnd.initializr.v2+json", true);
	}

	protected Matcher<HttpGet> getForNonJsonMetadata() {
		return new HasAcceptHeader("application/vnd.initializr.v2+json", false);
	}

	private String contentDispositionValue(String fileName) {
		return "attachment; filename=\"" + fileName + "\"";
	}

	private String createJsonError(int status, String message) {
		JSONObject json = new JSONObject();
		json.put("status", status);
		if (message != null) {
			json.put("message", message);
		}
		return json.toString();
	}

	protected static class MockHttpProjectGenerationRequest {

		String contentType;

		String fileName;

		byte[] content = new byte[] { 0, 0, 0, 0 };

		public MockHttpProjectGenerationRequest(String contentType, String fileName) {
			this(contentType, fileName, new byte[] { 0, 0, 0, 0 });
		}

		public MockHttpProjectGenerationRequest(String contentType, String fileName,
				byte[] content) {
			this.contentType = contentType;
			this.fileName = fileName;
			this.content = content;
		}

	}

	private static class HasAcceptHeader extends ArgumentMatcher<HttpGet> {

		private final String value;

		private final boolean shouldMatch;

		public HasAcceptHeader(String value, boolean shouldMatch) {
			this.value = value;
			this.shouldMatch = shouldMatch;
		}

		@Override
		public boolean matches(Object argument) {
			if (!(argument instanceof HttpGet)) {
				return false;
			}
			HttpGet get = (HttpGet) argument;
			Header acceptHeader = get.getFirstHeader(HttpHeaders.ACCEPT);
			if (this.shouldMatch) {
				return acceptHeader != null && this.value.equals(acceptHeader.getValue());
			}
			return acceptHeader == null || !this.value.equals(acceptHeader.getValue());
		}
	}

}
