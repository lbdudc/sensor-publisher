# Sensor Publisher

![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)
![Node.js Version](https://img.shields.io/badge/node-%3E%3D%2020.2.0-brightgreen.svg)

This library is a powerful tool designed to streamline the creation of dynamic web-based Geographic Information Systems (GIS) from a specialized Domain Specific Language (DSL). Seamlessly translating sensor data into captivating and interactive visualizations, this library empowers users to effortlessly build GIS applications tailored to their unique needs.

## Installation

```bash
git clone https://github.com/lbdudc/sensor-publisher.git

nvm use

npm install
```

### Pre-requisites

- Have installed in your machine:
  [Node.js](https://nodejs.org/en/download/)

- Check the `config.json` file and update the values accordingly.
- For the deployment to work, you need to have the following environment variables set:

```json
## Local configuration
"deploy": {
    "type": "local",
}
```

```json
## AWS configuration
"deploy": {
    "type": "aws",
    "AWS_ACCESS_KEY_ID": "AKIAJY2Q...",
    "AWS_SECRET_ACCESS_KEY": "X8Y4X0...",
    "AWS_REGION": "eu-west-2",
    "AWS_AMI_ID": "ami-xxxxxxxxxxx",
    "AWS_INSTANCE_TYPE": "t2.micro",
    "AWS_INSTANCE_NAME": "my-aws-instance",
    "AWS_SECURITY_GROUP_ID": "sg-xxxxxxxxxxxxx",
    "AWS_KEY_NAME": "mykey",
    "AWS_USERNAME": "ec2-user",
    "AWS_SSH_PRIVATE_KEY_PATH": "user/.ssh/mykey.pem",
    "REMOTE_REPO_PATH": "/home/ec2-user/code"
}
```

You can specify more deployment options, like for example where the Postgres databases will be on your generated product, also on the `config.json` file (if not specified, they will be deployed on the same machine as the platform on the `localhost`)

```json
  ## Postgres configuration
  "database": {
    "host": "host",
    "database": "database",
    "username": "username",
    "password": "password"
  }
```

## Usage

This library can be used in two ways, through the command line or through a visual interface.

### Command line

It is required that the first parameter of the command line is the path to the folder where the DSL definition is located, in a `.txt` file

```bash
Usage: npx sensorpublisher [options] [command]
General options:
  --help            # Print this info
  --generate, -g    # Just generate the product, do not deploy
  --version         # Print version
  --config          # Path to config file (default config file if not used)
  --deploy, -d      # Only deploy the product to the server, no generation
  -fm              # Path the custom FM selection JSON file
To print debug messages, set the env variable DEBUG=true
```

There are examples on the `examples` folder. For example, to generate the product from the `dsl.txt` file, you can run:

```bash
npx sensorpublisher examples/intecmar.txt
npx sensorpublisher examples/magist_qa.txt
```

If the user wants to change the features selected from the feature model, they can do it by creating a JSON file with an array of features, and passing it as a parameter to the command line:

```bash
npx sensorpublisher examples/intecmar.txt -fm examples/customFeatures.json
```

### Visual Interface

```bash
npm run dev
```

This will start a local server on port 5173. You can access it by opening a browser and going to <http://localhost:5173>

Note: If you want to override the deployment configuration, you can do it in the button "Deploy" in the top right corner of the interface of the editor.

Also it is possible to change the features selected from the feature model, by clicking on right screen arrow button on the interface, and selecting the features you want to include in the product.

### Docker

To run the platform in a Docker container, you can use the following commands:

```bash
docker-compose up
```

## Author

Victor Lamas
Email: <victor.lamas@udc.es>

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
