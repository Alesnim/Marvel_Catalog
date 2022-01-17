# Marvel Catalog
REST for Characters and Comics catalog

Endpoints:
- ``/swagger-ui/`` Swagger docs
- ``/v2/public/characters`` GET
- ``/v2/public/characters/{id}`` GET
- ``/v2/public/charactars/{id}/comics`` GET
- ``/v2/public/characters`` POST
- ``/v2/public/characters/{id}`` PUT
- ``/v2/public/comics`` GET
- ``/v2/pubic/comics/{id}`` GET
- ``/v2/public/comics/{id}/characters`` GET
- ``/v2/public/comics``POST
- ``/v2/public/comics/{id}`` PUT


Starting:

In Docker:
1. ``gradlew bootJar``
2. ``docker-compose up``

Test setup:
1. Start MongoDB
2. Write host address and port in application.properties
3. Run tests

