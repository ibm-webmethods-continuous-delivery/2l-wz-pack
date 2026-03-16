# Build Sources

This folder contains the necessary artifact to build the java and assimilated sources, so that the packages may be copied into their destination with all the necessary dependencies included.

## Procedures

If for some reason the ownership of the files gets in tha way of building, the jcode container will stop and allow the user to reset the file permissions.

some useful commands for the ubi based microservices runtime image:

```sh
docker exec -it -u root $containerID sh

microdnf -y update
microdnf -y install findutils

cd opt/softwareag/IntegrationServer/packages/
find . -exec chmod a+rw {} \;
```