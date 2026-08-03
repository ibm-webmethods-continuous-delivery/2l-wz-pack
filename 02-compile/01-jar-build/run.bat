docker compose build --no-cache
docker compose run --rm jarbuilder

echo result is %ERRORLEVEL%
