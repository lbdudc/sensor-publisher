# sensorbuilder

Tool to automatically generate a web-based GIS from a DSL (Domain Specific Language) with sensors information.

## Use the application

### CLI

```bash
# if nvm installed, otherwise just use node 19.x
nvm use

npm install

# to set husky git hooks (linting)
npm run prepare

# to run, first update config.json and then:
npx sensorbuilder args

# for example
npx sensorbuilder examples/intecmar
```

### Visual client
  
  ```bash
  # if nvm installed, otherwise just use node 19.x
  nvm use

  npm install

  # to set husky git hooks (linting)
  npm run prepare

  # to run, first update config.json and then:
  npm run serve
  # will run on localhost:8080
  ```

## Example of config.json

### Local

- Pre-requisites:
  Have docker and docker-compose installed

```json
"deploy": {
    "type": "local",
}
```

### AWS

```json
"deploy": {
    "type": "aws",
    "AWS_ACCESS_KEY_ID": "AKIAJY2Q...",
    "AWS_SECRET_ACCESS_KEY": "X8Y4X0...",
    "AWS_REGION": "eu-west-2",
    "AWS_AMI_ID": "ami-08b064b1296caf3b2",
    "AWS_INSTANCE_TYPE": "t2.micro",
    "AWS_INSTANCE_NAME": "my-aws-instance",
    "AWS_SECURITY_GROUP_ID": "sg-0a1b2c3d4e5f6a7b8",
    "AWS_KEY_NAME": "mykey",
    "AWS_USERNAME": "ec2-user",
    "AWS_SSH_PRIVATE_KEY_PATH": "user/.ssh/mykey.pem",
    "REMOTE_REPO_PATH": "/home/ec2-user/code"
}
```
