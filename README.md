# TEI4J

[![JitPack](https://jitpack.io/v/PAIR-Systems-Inc/tei4j.svg)](https://jitpack.io/#PAIR-Systems-Inc/tei4j)

A Java client library for [HuggingFace Text Embeddings Inference (TEI)](https://github.com/huggingface/text-embeddings-inference).

## Features

- **Complete TEI API Coverage**: Support for all TEI endpoints including embeddings, tokenization, reranking, and predictions
- **Type-Safe**: Generated from OpenAPI specification with full type safety
- **Java 11+ Compatible**: Built for modern Java applications
- **Lightweight**: Minimal dependencies using OkHttp and Gson
- **OpenAI Compatible**: Supports OpenAI-compatible embedding endpoints

## Installation

### Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.PAIR-Systems-Inc:tei4j:main-SNAPSHOT' // Latest development
    // Or use a specific version: 'com.github.PAIR-Systems-Inc:tei4j:v1.0.2'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.PAIR-Systems-Inc</groupId>
        <artifactId>tei4j</artifactId>
        <version>main-SNAPSHOT</version> <!-- Latest development -->
        <!-- Or use a specific version: <version>v1.0.2</version> -->
    </dependency>
</dependencies>
```


## Quick Start

```java
import ai.pairsys.tei4j.client.ApiClient;
import ai.pairsys.tei4j.client.api.TextEmbeddingsInferenceApi;
import ai.pairsys.tei4j.client.model.EmbedRequest;
import ai.pairsys.tei4j.client.model.EmbedResponse;

// Create client
ApiClient client = new ApiClient();
client.setBasePath("http://localhost:8010"); // Your TEI server URL
TextEmbeddingsInferenceApi api = new TextEmbeddingsInferenceApi(client);

// Get embeddings
EmbedRequest request = new EmbedRequest().inputs("Hello world");
List<List<Float>> embeddings = api.embed(request);
```

## API Overview

### Embeddings

```java
// Single text embedding
EmbedRequest request = new EmbedRequest()
    .inputs("Your text here")
    .normalize(true);
List<List<Float>> embeddings = api.embed(request);

// Batch embeddings
EmbedRequest batchRequest = new EmbedRequest()
    .inputs(Arrays.asList("Text 1", "Text 2", "Text 3"));
List<List<Float>> batchEmbeddings = api.embed(batchRequest);
```

### Tokenization

```java
TokenizeRequest tokenizeRequest = new TokenizeRequest()
    .inputs("Hello world")
    .addSpecialTokens(true);
List<List<SimpleToken>> tokens = api.tokenize(tokenizeRequest);
```

### Reranking

```java
RerankRequest rerankRequest = new RerankRequest()
    .query("What is machine learning?")
    .texts(Arrays.asList(
        "Machine learning is a subset of AI",
        "The weather is nice today",
        "Deep learning uses neural networks"
    ))
    .returnText(true);
List<Rank> rankings = api.rerank(rerankRequest);
```

### OpenAI Compatible API

```java
OpenAICompatRequest openaiRequest = new OpenAICompatRequest()
    .input("Your text here")
    .model("your-model-name");
OpenAICompatResponse response = api.openaiEmbed(openaiRequest);
```

## Configuration

### Custom Server URL

```java
ApiClient client = new ApiClient();
client.setBasePath("https://your-tei-server.com");
```

### Authentication

```java
// API Key authentication (if required)
ApiClient client = new ApiClient();
client.setApiKey("your-api-key");
```

### Timeouts

```java
ApiClient client = new ApiClient();
client.setConnectTimeout(30000); // 30 seconds
client.setReadTimeout(60000);    // 60 seconds
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/PAIR-Systems-Inc/tei4j.git
cd tei4j

# Generate client from OpenAPI spec (if needed)
./gradlew openApiGenerate

# Build the library
./gradlew build
```

## Requirements

- Java 11 or higher
- A running TEI server instance

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Support

For issues and questions:
- [GitHub Issues](https://github.com/PAIR-Systems-Inc/tei4j/issues)
- [TEI Documentation](https://github.com/huggingface/text-embeddings-inference)