
How to run:

I went with a docker first approach where the configs are optimized to run faster with docker but
we can still run it without docker at all but will need manual work to get dbs up etc.. most the properties are overridable
and can be configured

infrastructure, config eurka mongo, execute:

```./run/docker/start.sh```

services:
if you want to work on specific service
example

```./run/start-mvnw food-catalog```

