## Video Service Overview

The objective of this service is to manage video files, providing CRUD (Create, Read, Update, Delete) operations, and to enable HTTP streaming of videos.

### Local Setup

To run the service locally, you can either launch it as a Java application or within a Docker container.

- **Port Configuration**: By default, the application listens on port `8080`.
- **Context Path and API Endpoint**: The base context path for the service is `/video-service`, with an API prefix of `/api/v1/videos`.

### Running with Docker

To run the application within Docker, you’ll need Docker and Docker Compose installed. The `docker-compose.yaml` file is located in the root directory of the project.

1. **Add Video Files**: To include additional video files or link a video directory from the host, mount the desired folder as a volume in the Docker container.
2. **Dependencies**: Ensure Docker and Docker Compose are installed. If not, consult online resources for installation instructions.
3. **Commands to Start the Service**:
   ```bash
   docker-compose build
   docker-compose up
   ```

### Streaming Endpoint

The application provides a streaming endpoint to serve video content over HTTP.

- **Endpoint URL Format**: `/video-service/api/v1/videos/stream/{fileType}/{fileName}`
- **Default Directory**: The application defaults to the `./video` folder for video files.
- **Custom Video Directory**: To specify a different directory for video content, set the `VIDEO_CONTENT_PATH` environment variable.
- **Example Request**:
  Once the application is running, access the streaming service at:
  ```
  {protocol}://{host}:{port}/video-service/api/v1/videos/stream/{fileType}/{fileName}
  ```
  For example:
  ```
  http://localhost:8080/video-service/api/v1/videos/stream/mp4/toystory
  ```

**Further Reading**

For more details on setting up and using this HTTP-based video streaming service, see [this article on Medium](https://medium.com/@saravanastar/video-streaming-over-http-using-spring-boot-51e9830a3b8).