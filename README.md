# Marvel Catalog
REST for Characters and Comics catalog

Endpoints:
- ``/swagger-ui/`` Swagger docs
- GET ``/v2/public/characters``
- GET ``/v2/public/characters/{id}``
- GET ``/v2/public/characters/{id}/comics``
- POST``/v2/public/characters``
- PUT ``/v2/public/characters/{id}``
- GET ``/v2/public/comics``
- GET ``/v2/pubic/comics/{id}``
- GET ``/v2/public/comics/{id}/characters`` 
- POST ``/v2/public/comics``
- PUT ``/v2/public/comics/{id}``


Starting:

In Docker:
1. ``gradlew bootJar``
2. ``docker-compose up``

Test setup:
1. Start MongoDB
2. Write host address and port in application.properties
3. Run tests

