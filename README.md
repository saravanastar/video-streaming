# Video Service Overview

This service is designed to manage video files by providing CRUD (Create, Read, Update, Delete) operations and enabling HTTP-based streaming of videos.

## Features
- **Video Management**: Perform CRUD operations on video files.
- **HTTP Streaming**: Stream video content directly over HTTP.

## Local Setup
To run the service locally, you can launch it either as a Java application or within a Docker container.

### Configuration
- **Port**: The application listens on port `8080` by default.
- **Context Path and API Endpoint**: The base context path is `/video-service`, with an API prefix of `/api/v1/videos`.

### Prerequisites
Ensure the following dependencies are installed:
- Java 17 or higher
- Docker and Docker Compose (if running in a containerized environment)

### Running the Application
#### Using Java
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd <repository_directory>
   ```
2. Build and run the application using your preferred IDE or a build tool like Maven:
   ```bash
   mvn spring-boot:run
   ```

#### Using Docker
1. Verify that Docker and Docker Compose are installed on your system. If not, refer to the official installation guides for [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).
2. Build and run the Docker container:
   ```bash
   docker-compose build
   docker-compose up
   ```

#### Adding Video Files
- **Default Directory**: The application uses the `./video` directory for video content by default.
- **Custom Directory**: To use a custom directory, set the `VIDEO_CONTENT_PATH` environment variable to point to your desired location.
- **Mounting Volumes**: When running in Docker, mount the desired video folder as a volume in the container to include additional video files.

Example of mounting a directory: docker-compose.yaml
```yaml
services:
  video-service:
    volumes:
      - /path/to/local/video/folder:/tmp/content
```

## Streaming Endpoint
The application provides an HTTP streaming endpoint to serve video content.

### Endpoint Format
- URL: `/video-service/api/v1/videos` - Will list the all video files in the directory
- URL: `/video-service/api/v1/videos/stream/{fileType}/{fileName}`
    - **`fileType`**: The format of the video file (e.g., `mp4`, `mkv`).
    - **`fileName`**: The name of the video file without the extension.
- URL: `/video-service/api/v1/videos/object-key/{objectKey}` - prefer to use this endpoint
    - **`objectKey`**: Object key of the file, can get the object key by hitting the `/video-service/api/v1/videos` 

### Example Request
Once the application is running, you can access the streaming service with the following format:
```
http://{host}:{port}/video-service/api/v1/videos/stream/{fileType}/{fileName}
```
For example:
```
http://localhost:8080/video-service/api/v1/videos/stream/mp4/toystory
```

### Customizing the Video Directory
To change the default video content directory:
1. Set the `VIDEO_CONTENT_PATH` environment variable.
   ```bash
   export VIDEO_CONTENT_PATH=/path/to/your/video/directory
   ```
2. Restart the application to apply the changes.

## Testing and Debugging
### Unit and Integration Tests
- The project includes unit and integration tests to ensure functionality. Use the following command to execute tests:
   ```bash
   mvn test
   ```

### Logging
- The application uses configurable logging via `logback.xml`. Logs are stored in the `logs` directory by default.
- To change the logging level, modify the `application.properties` file or the `logback.xml` configuration.

### Common Issues
1. **Port Already in Use**:
    - If port `8080` is occupied, modify the port in the `application.properties` file:
      ```properties
      server.port=9090
      ```
2. **File Not Found**:
    - Ensure that video files are present in the specified directory.
    - Verify the `VIDEO_CONTENT_PATH` environment variable if using a custom directory.


## Further Reading
For an in-depth explanation of how this service is implemented, including details on HTTP-based video streaming using Spring Boot, check out [this Medium article](https://medium.com/@saravanastar/video-streaming-over-http-using-spring-boot-51e9830a3b8).

