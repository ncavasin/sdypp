FROM node:14-alpine
# create root application folder
WORKDIR /

# copy configs to /app folder
COPY package*.json ./
COPY tsconfig.json ./
# copy source code to /src folder
COPY src /src
# Install dependencies
RUN npm install
# Build the app
RUN npm run build

EXPOSE 4001

CMD [ "npm", "run", "start" ]